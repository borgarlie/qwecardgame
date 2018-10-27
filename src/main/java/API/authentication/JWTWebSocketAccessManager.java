package API.authentication;

import Database.UserDatabase;
import Pojos.User;
import ThirdParty.javalinjwt.JWTProvider;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Strings;
import io.javalin.security.Role;
import org.json.JSONObject;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class JWTWebSocketAccessManager {

    private String userRoleClaim;
    private String userIdClaim;
    private Map<String, Role> rolesMapping;
    private Role defaultRole;
    private JWTProvider provider;

    private static final String JWT = "jwt";

    public JWTWebSocketAccessManager(
            String userRoleClaim,
            String userIdClaim,
            Map<String, Role> rolesMapping,
            Role defaultRole,
            JWTProvider provider) {
        this.userRoleClaim = userRoleClaim;
        this.userIdClaim = userIdClaim;
        this.rolesMapping = rolesMapping;
        this.defaultRole = defaultRole;
        this.provider = provider;
    }

    public Optional<User> getLoggedInUser(JSONObject jsonObject, Set<Role> permittedRoles) {
        Optional<String> userId = extractUserId(jsonObject, permittedRoles);
        if (!userId.isPresent()) {
            return Optional.empty();
        }
        String id = userId.get();
        return UserDatabase.get(id);
    }

    /**
     * Extracts a user id if the role matches permittedRoles
     */
    private Optional<String> extractUserId(JSONObject jsonObject, Set<Role> permittedRoles) {
        Optional<DecodedJWT> decodedJWT = getJwtFromWebSocket(jsonObject);
        if (!decodedJWT.isPresent()) {
            return Optional.empty();
        }
        DecodedJWT jwt = decodedJWT.get();
        String userLevel = jwt.getClaim(userRoleClaim).asString();
        Role role = Optional.ofNullable(this.rolesMapping.get(userLevel)).orElse(defaultRole);
        if (permittedRoles.contains(role)) {
            return Optional.empty();
        }
        String userId = jwt.getClaim(userIdClaim).asString();
        return Optional.of(userId);
    }

    private Optional<DecodedJWT> getJwtFromWebSocket(JSONObject jsonObject) {
        String token = jsonObject.getString(JWT);
        if (Strings.isNullOrEmpty(token))  {
            return Optional.empty();
        }
        return this.provider.validateToken(token);
    }
}

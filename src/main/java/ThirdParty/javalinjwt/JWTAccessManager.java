package ThirdParty.javalinjwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.javalin.Context;
import io.javalin.Handler;
import io.javalin.security.AccessManager;
import io.javalin.security.Role;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class JWTAccessManager implements AccessManager {
    private String userRoleClaim;
    private Map<String, Role> rolesMapping;
    private Role defaultRole;

    public JWTAccessManager(String userRoleClaim, Map<String, Role> rolesMapping, Role defaultRole) {
        this.userRoleClaim = userRoleClaim;
        this.rolesMapping = rolesMapping;
        this.defaultRole = defaultRole;
    }

    private Role extractRole(Context context) {

        System.out.println("Extracting role");

        if (!JavalinJWT.containsJWT(context)) {
            return defaultRole;
        }

        DecodedJWT jwt = JavalinJWT.getDecodedFromContext(context);
        String userLevel = jwt.getClaim(userRoleClaim).asString();

        return Optional.ofNullable(rolesMapping.get(userLevel)).orElse(defaultRole);
    }

    @Override
    public void manage(Handler handler, Context context, Set<Role> permittedRoles) throws Exception {
        Role role = extractRole(context);

        System.out.println("Got role: " + role.toString());

        System.out.println(permittedRoles);
        if (permittedRoles.isEmpty()) {
            System.out.println("Empty permitted roles");
        }

        permittedRoles.forEach(r -> {
            System.out.println("Allowed role = " + r.toString());
        });

        System.out.println(permittedRoles.contains(role));

        if (permittedRoles.contains(role)) {
            System.out.println("Authorized");
            handler.handle(context);
        } else {
            System.out.println("Unauthorized");
            context.status(401).result("Unauthorized");
        }
    }
}
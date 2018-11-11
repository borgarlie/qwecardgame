package API.authentication;

import Pojos.User;
import ThirdParty.javalinjwt.JWTGenerator;
import ThirdParty.javalinjwt.JWTProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.UUID;


public class JWTUtil {

    public static final String USER_ROLE_CLAIM = "role";
    public static final String USER_ID_CLAIM = "user_id";

    private static final String HMAC_SECRET = UUID.randomUUID().toString();

    public static JWTProvider generateJWTProvider() {
        // TODO: Should have an expiry date on the generated JWT tokens.
        // https://gist.github.com/soulmachine/b368ce7292ddd7f91c15accccc02b8df
        // Set exp time and check for this in access manager.
        // Store all tokens in redis and check that they are there instead?
        // On revoke -> remove from redis. If exp < now -> remove from redis.
        Algorithm algorithm = Algorithm.HMAC256(HMAC_SECRET);
        JWTGenerator<User> generator = (user, alg) -> {
            JWTCreator.Builder token = JWT.create()
                    .withClaim(USER_ROLE_CLAIM, user.getRole().name())
                    .withClaim(USER_ID_CLAIM, user.getUserId());
            return token.sign(alg);
        };
        JWTVerifier verifier = JWT.require(algorithm).build();
        return new JWTProvider(algorithm, generator, verifier);
    }
}

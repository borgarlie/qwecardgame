import API.Game.Cards;
import API.Game.WebSocketHandler;
import API.Menu.DeckBuilder;
import API.authentication.GoogleOAuth;
import API.authentication.JWTWebSocketAccessManager;
import API.authentication.Roles;
import Pojos.User;
import ThirdParty.javalinjwt.*;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import io.javalin.Handler;
import io.javalin.Javalin;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static Config.Config.HMAC_SECRET;

public class Main {

    private static final String USER_ROLE_CLAIM = "role";
    private static final String USER_ID_CLAIM = "user_id";

    public static void main(String[] args) {

        // Setup the server
        Javalin app = Javalin.create()
                .port(1337)
                .enableCorsForAllOrigins();

        // Access manager
        Algorithm algorithm = Algorithm.HMAC256(HMAC_SECRET);
        JWTGenerator<User> generator = (user, alg) -> {
            JWTCreator.Builder token = JWT.create()
                    .withClaim(USER_ROLE_CLAIM, user.getRole().name())
                    .withClaim(USER_ID_CLAIM, user.getUserId());
            return token.sign(alg);
        };
        JWTVerifier verifier = JWT.require(algorithm).build();
        JWTProvider provider = new JWTProvider(algorithm, generator, verifier);

        JWTAccessManager accessManager = new JWTAccessManager(USER_ROLE_CLAIM, Roles.rolesMapping, Roles.ANYONE);
        app.accessManager(accessManager);

        JWTWebSocketAccessManager webSocketAccessManager
            = new JWTWebSocketAccessManager(USER_ROLE_CLAIM, USER_ID_CLAIM, Roles.rolesMapping, Roles.ANYONE, provider);

        WebSocketHandler wsHandler = new WebSocketHandler(webSocketAccessManager);

        GoogleOAuth googleOAuth = new GoogleOAuth(provider);

        // Set JWT decode handler
        Handler decodeHandler = JavalinJWT.createHeaderDecodeHandler(provider);
        app.before(decodeHandler);

        // REST API
        // TODO: Find a better way to declare roles
        app.post("api/auth/google", googleOAuth.login, Collections.singleton(Roles.ANYONE));
        app.get("cards", Cards.getAll, Collections.singleton(Roles.ANYONE));
        app.get("deck/username/:username", DeckBuilder.getDecksByUsername,
                new HashSet<>(Arrays.asList(Roles.USER, Roles.ADMIN)));
        app.post("deck", DeckBuilder.createNew, new HashSet<>(Arrays.asList(Roles.USER, Roles.ADMIN)));
        app.put("deck/id/:id", DeckBuilder.update, new HashSet<>(Arrays.asList(Roles.USER, Roles.ADMIN)));

        // WebSocket - used for the actual gameplay and menu
        app.ws("/game", ws -> {
            ws.onConnect(wsHandler.onConnect);
            ws.onMessage(wsHandler.onMessage);
            ws.onMessage(wsHandler.onBinaryMessage);
            ws.onClose(wsHandler.onClose);
            ws.onError(wsHandler.onError);
        });

        // Start the server
        app.start();

        System.out.println("Server has started");
    }
}

import API.Game.Cards;
import API.Menu.DeckBuilder;
import API.authentication.GoogleOAuth;
import API.authentication.Roles;
import Pojos.User;
import ThirdParty.javalinjwt.JWTAccessManager;
import ThirdParty.javalinjwt.JWTGenerator;
import ThirdParty.javalinjwt.JWTProvider;
import ThirdParty.javalinjwt.JavalinJWT;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import io.javalin.Handler;
import io.javalin.Javalin;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static API.Game.WebSocketHandler.*;


public class Main {

    public static void main(String[] args) {

        // Setup the server
        Javalin app = Javalin.create()
                .port(1337)
                .enableCorsForAllOrigins();

        // Access manager
        // TODO: Get secret from config
        String secret = "very_secret";
        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTGenerator<User> generator = (user, alg) -> {
            JWTCreator.Builder token = JWT.create().withClaim("role", user.getRole().name());
            return token.sign(alg);
        };
        JWTVerifier verifier = JWT.require(algorithm).build();
        JWTProvider provider = new JWTProvider(algorithm, generator, verifier);

        JWTAccessManager accessManager = new JWTAccessManager("role", Roles.rolesMapping, Roles.ANYONE);
        app.accessManager(accessManager);

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

        // TODO: Why does this work and not the other one? seems like only post is the problem? (and put maybe)
        app.get("/test", ctx -> ctx.json("TEST SOMETHING"), Collections.singleton(Roles.ANYONE));

        // WebSocket - used for the actual gameplay and menu
        app.ws("/game", ws -> {
            ws.onConnect(onConnect);
            ws.onMessage(onMessage);
            ws.onMessage(onBinaryMessage);
            ws.onClose(onClose);
            ws.onError(onError);
        });

        // Start the server
        app.start();

        System.out.println("Server has started");
    }
}

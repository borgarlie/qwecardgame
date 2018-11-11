import API.Game.Cards;
import API.Game.WebSocketHandler;
import API.Menu.DeckBuilder;
import API.Menu.UserManager;
import API.authentication.GoogleOAuth;
import API.authentication.JWTWebSocketAccessManager;
import API.authentication.Roles;
import ThirdParty.javalinjwt.*;
import io.javalin.Handler;
import io.javalin.Javalin;

import static API.authentication.JWTUtil.USER_ROLE_CLAIM;
import static API.authentication.JWTUtil.generateJWTProvider;
import static API.authentication.Roles.roles;

public class Main {

    public static void main(String[] args) {

        // Setup the server
        Javalin app = Javalin.create()
                .port(1337)
                .enableCorsForAllOrigins();

        // JWT provider
        JWTProvider provider = generateJWTProvider();

        // Access manager
        JWTAccessManager accessManager = new JWTAccessManager(USER_ROLE_CLAIM, Roles.rolesMapping, Roles.ANYONE);
        app.accessManager(accessManager);

        // WebSocket Access Manager
        JWTWebSocketAccessManager webSocketAccessManager
            = new JWTWebSocketAccessManager(Roles.rolesMapping, Roles.ANYONE, provider);

        // WebSocket Handler
        WebSocketHandler wsHandler = new WebSocketHandler(webSocketAccessManager);

        // Google OAuth Manager
        GoogleOAuth googleOAuth = new GoogleOAuth(provider);

        // Set JWT decode handler
        Handler decodeHandler = JavalinJWT.createHeaderDecodeHandler(provider);
        app.before(decodeHandler);

        // REST API
        // TODO: Add revoke JWT endpoint
        // TODO: Add extend JWT endpoint (should probably be the same as verify?)
        app.post("auth/google", googleOAuth.login, roles(Roles.ANYONE));
        app.get("auth/verify", (handler) -> handler.json("ok"), roles(Roles.USER, Roles.ADMIN));
        app.put("user/username", UserManager.updateUsername, roles(Roles.USER, Roles.ADMIN));
        app.get("cards", Cards.getAll, roles(Roles.ANYONE));
        app.get("deck/username/:username", DeckBuilder.getDecksByUsername, roles(Roles.USER, Roles.ADMIN));
        app.post("deck", DeckBuilder.createNew, roles(Roles.USER, Roles.ADMIN));
        app.put("deck/id/:id", DeckBuilder.update, roles(Roles.USER, Roles.ADMIN));

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

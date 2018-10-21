import API.Game.Cards;
import API.Menu.DeckBuilder;
import API.Oauth.GoogleOAuth;
import io.javalin.Javalin;

import static API.Game.WebSocketHandler.*;
import static io.javalin.apibuilder.ApiBuilder.*;


public class Main {

    public static void main(String[] args) {

        // Setup the REST API
        Javalin app = Javalin.create()
                .port(1337)
                .enableCorsForAllOrigins();

        // REST API
        app.routes(() -> {
            path("api/auth/google", () -> post(GoogleOAuth.login));
            path("cards", () -> get(Cards.getAll));
            path("deck", () -> {
                post(DeckBuilder.createNew);
                path("id/:id", () -> put(DeckBuilder.update));
                path("username/:username", () -> get(DeckBuilder.getDecksByUsername));
            });
        });

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

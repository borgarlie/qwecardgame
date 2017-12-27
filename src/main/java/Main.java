import API.Game.Cards;
import API.Game.Game;
import API.Menu.DeckBuilder;
import Public.Webapp;
import io.javalin.Javalin;

import static io.javalin.ApiBuilder.path;
import static io.javalin.ApiBuilder.get;
import static io.javalin.ApiBuilder.post;


public class Main {

    public static void main(String[] args) {

        // Setup the REST API and web app
        Javalin app = Javalin.create()
                .port(1337)
                .enableStaticFiles("html");

        // Web app
        app.get("/qwe", Webapp.webapp);

        // REST API
        app.routes(() -> {
            path("cards", () -> {
                get(Cards.getAll);
            });
            path("deck", () -> {
                post(DeckBuilder.createNew);
                path("username/:username", () -> {
                    get(DeckBuilder.getDecksByUsername);
                });
            });
        });

        // WebSocket - used for the actual gameplay
        app.ws("/game", Game.class);

        // Start the server
        app.start();

        System.out.println("Server has started");

    }
}

import API.Game.Cards;
import API.Game.Game;
import Public.Webapp;
import io.javalin.Javalin;
import io.javalin.translator.json.JavalinJacksonPlugin;

import static io.javalin.ApiBuilder.path;
import static io.javalin.ApiBuilder.get;



public class Main {

    public static void main(String[] args) {

        // config for database
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // jackson config
//        JavalinJacksonPlugin.configure(objectMapper);

        // setup the REST API and web app
        Javalin app = Javalin.create()
                .port(1337)
                .enableStaticFiles("html");

        // paths
        app.get("/qwe", Webapp.webapp);

        // API
        app.routes(() -> {
            path("cards", () -> {
                get(Cards.getAll);
            });
        });

        // WebSocket
        app.ws("/game", Game.class);

        // start the server
        app.start();

        System.out.println("Webapp is started");
    }
}

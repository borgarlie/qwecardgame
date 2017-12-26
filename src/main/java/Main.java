import io.javalin.Javalin;


public class Main {

    public static void main(String[] args) {
        Javalin app = Javalin.start(1337);
        app.get("/", ctx -> ctx.result("Hello World"));
    }
}

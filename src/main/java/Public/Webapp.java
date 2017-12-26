package Public;

import io.javalin.Handler;

public class Webapp {

    public static Handler webapp = ctx -> {
        ctx.html("qwe");
    };

}

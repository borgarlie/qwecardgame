package API.Menu;

import Database.UserDatabase;
import ThirdParty.javalinjwt.JavalinJWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.javalin.Handler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import static API.authentication.JWTUtil.USER_ID_CLAIM;

public class UserManager {

    public static Handler updateUsername = ctx -> {
        String body = ctx.body();
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(body);
        JSONObject jsonObject = (JSONObject) obj;

        String username = (String) jsonObject.get("username");
        if (username == null || username.trim().length() < 5) {
            ctx.status(400);
            ctx.json(false);
            return;
        }

        DecodedJWT jwt = JavalinJWT.getDecodedFromContext(ctx);
        String userId = jwt.getClaim(USER_ID_CLAIM).asString();

        boolean userUpdated = UserDatabase.updateUsername(userId, username);
        ctx.json(userUpdated);
    };
}

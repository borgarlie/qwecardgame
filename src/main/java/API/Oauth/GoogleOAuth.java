package API.Oauth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.javalin.Handler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Collections;
import java.util.HashMap;

public class GoogleOAuth {

    private static final JacksonFactory jacksonFactory = new JacksonFactory();

    public static Handler login = ctx -> {

        // TODO: Move this to config
        String CLIENT_ID = "63703877913-ofgcofommqkdhc5b8o6c9s63t57lbodq.apps.googleusercontent.com";

        // Extract the id_token from JSON
        String body = ctx.body();
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(body);
        JSONObject jsonObject = (JSONObject) obj;
        String idTokenString = (String) jsonObject.get("id_token");

        // TODO: These can probably be moved somewhere, so we don't have to create them every time
        // Set up google verifier
        HttpTransport httpTransport = new NetHttpTransport();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jacksonFactory)
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        // Set up return map
        HashMap<String, Object> returnMap = new HashMap<>();

        // Verify the token
        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");

            // Use or store profile information
            // ...

            System.out.println(name);
            System.out.println(email);

            returnMap.put("Stuff", "Received email: " + email);

            // TODO: Add user and stuff.
            // Should use AccessManager. E.g. https://javalin.io/2018/09/11/javalin-jwt-example.html

        } else {
            System.out.println("Invalid ID token.");
        }
        // end stuff

        // Send response with jwt token + user information back to client
        ctx.json(returnMap);
    };
}

package API.authentication;

import Database.UserDatabase;
import Pojos.User;
import ThirdParty.javalinjwt.JWTProvider;
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
import java.util.Optional;

public class GoogleOAuth {

    private String client_id;
    private JacksonFactory jacksonFactory;
    private HttpTransport httpTransport;
    private GoogleIdTokenVerifier verifier;
    private JWTProvider provider;

    public GoogleOAuth(JWTProvider provider) {
        // TODO: Get from config
        this.client_id = "63703877913-ofgcofommqkdhc5b8o6c9s63t57lbodq.apps.googleusercontent.com";
        this.jacksonFactory = new JacksonFactory();
        this.httpTransport = new NetHttpTransport();
        this.verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jacksonFactory)
                .setAudience(Collections.singletonList(this.client_id))
                .build();
        this.provider = provider;
    }

    public Handler login = ctx -> {
        // Extract the id_token from JSON
        String body = ctx.body();
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(body);
        JSONObject jsonObject = (JSONObject) obj;
        String idTokenString = (String) jsonObject.get("id_token");

        // Set up return map
        HashMap<String, Object> returnMap = new HashMap<>();

        // Verify the token
        GoogleIdToken idToken = this.verifier.verify(idTokenString);
        if (idToken != null) {
            Payload payload = idToken.getPayload();

            // Get profile information from payload
            int userId = Integer.parseInt(payload.getSubject());
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            System.out.println("Verified User ID: " + userId);

            // Check if user exists
            Optional<User> user = UserDatabase.get(userId);

            // Create new user if user does not exist already
            if (!user.isPresent()) {
                User newUser = User.builder()
                        .userId(userId)
                        .email(email)
                        .name(name)
                        .username(name)
                        .role(Roles.USER)
                        .build();
                int createdUserValue = UserDatabase.create(newUser);
                if (createdUserValue < 0) {
                    System.out.println("Error when creating user with userId: " + userId);
                    returnMap.put("Error", "Error while creating user");
                }

                user = Optional.of(newUser);
            }

            // Set JWT in header
            String token = this.provider.generateToken(user.get());
            ctx.header("x-auth-token", token);

            // Add user data
            returnMap.put("user", user.get());
        } else {
            System.out.println("Invalid ID token.");
            returnMap.put("Error", "Invalid ID token");
        }

        // Send response with jwt token + user information back to client
        ctx.json(returnMap);
    };
}

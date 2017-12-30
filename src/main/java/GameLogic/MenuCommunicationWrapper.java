package GameLogic;

import Utils.HandleWebSocketType;
import Utils.ReflectionUtil;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class MenuCommunicationWrapper {

    // map of all menu websocket type methods
    private static Map<String, Method> webSocketTypeMethods
            = ReflectionUtil.findWebSocketTypeMethods(MenuCommunicationWrapper.class, HandleWebSocketType.class);

    public static final String TYPE = "type";
    public static final String ERROR = "error";
    public static final String ECHO = "echo";
    public static final String PLAY_REQUEST = "play_request";
    public static final String ACCEPT_REQUEST = "accept_request"; // true or false
    public static final String USERNAME = "username";

    public static void handleMenuChoice(JSONObject jsonObject, Session session) throws IOException {
        String type = jsonObject.getString(TYPE);
        try {
            webSocketTypeMethods.get(type).invoke(null, jsonObject, session);
        } catch (Exception e) {
            handleError(e, jsonObject, session);
        }
    }

    private static void handleError(Exception e, JSONObject jsonObject, Session session) throws IOException {
        System.out.println("Error occurred when handling menu choice");
        String json = new JSONObject()
                .put(TYPE, ERROR)
                .put(ECHO, jsonObject)
                .put(ERROR, e)
                .toString();
        session.getRemote().sendString(json);
    }

    @HandleWebSocketType(ERROR)
    public static void handleErrorType(JSONObject jsonObject, Session session) {
        System.out.println("Error type sent from client");
    }

    @HandleWebSocketType(PLAY_REQUEST)
    public static void handlePlayRequest(JSONObject jsonObject, Session session) {
        // TODO: Request player for game
        // Accept / Deny request -> send response and init game
        System.out.println("Play request initiated");

    }

    @HandleWebSocketType(ACCEPT_REQUEST)
    public static void handleAcceptRequest(JSONObject jsonObject, Session session) {
        System.out.println("Request accepted");
    }

    // Used for testing purposes
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, IOException {
        JSONObject json = new JSONObject()
                .put(TYPE, ERROR)
                .put(ERROR, "error");
        Session session = null;
        String type = "play_request";
        try {
            webSocketTypeMethods.get(type).invoke(null, json, session);
        } catch (Exception e) {
            handleError(null, json, session);
        }
    }
}

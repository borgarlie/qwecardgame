package GameLogic;


import Utils.HandleWebSocketType;
import Utils.ReflectionUtil;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

public class GameCommunicationWrapper {

    // map of all menu websocket type methods
    private static Map<String, Method> webSocketTypeMethods
            = ReflectionUtil.findWebSocketTypeMethods(GameCommunicationWrapper.class, HandleWebSocketType.class);

    // These are messages received from the players
    public static final String TYPE = "type";
    public static final String END_TURN = "end_turn";
    public static final String ERROR = "error";
    public static final String ECHO = "echo";

    public static void handleGameMove(
            JSONObject jsonObject,
            MainGameLoop gameLoop,
            MainGameLoop.Player player,
            Session session) throws IOException {
        String type = jsonObject.getString(TYPE);
        try {
            webSocketTypeMethods.get(type).invoke(null, jsonObject, gameLoop, player);
        } catch (Exception e) {
            handleError(e, jsonObject, session);
        }
    }

    private static void handleError(Exception e, JSONObject jsonObject, Session session) throws IOException {
        System.out.println("Error occurred when handling menu choice. Exception: " + e);
        String json = new JSONObject()
                .put(TYPE, ERROR)
                .put(ECHO, jsonObject)
                .put(ERROR, e)
                .toString();
        session.getRemote().sendString(json);
    }

    @HandleWebSocketType(ERROR)
    public static void handleErrorType(JSONObject jsonObject, MainGameLoop gameLoop, MainGameLoop.Player player) {
        System.out.println("Error type sent from client");
    }

    @HandleWebSocketType(END_TURN)
    public static void handleEndTurn(JSONObject jsonObject, MainGameLoop gameLoop, MainGameLoop.Player player)
            throws IOException, GameError {
        System.out.println("End turn");
        gameLoop.endTurn(player);
    }
}

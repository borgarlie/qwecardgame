package GameLogic;


import Utils.HandleWebSocketType;
import Utils.ReflectionUtil;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

public class GameCommunicationWrapper {

    // map of all in game websocket type methods
    private static Map<String, Method> webSocketTypeMethods
            = ReflectionUtil.findWebSocketTypeMethods(GameCommunicationWrapper.class, HandleWebSocketType.class);

    // These are messages received from the players
    public static final String TYPE = "type";
    public static final String END_TURN = "end_turn";
    public static final String ERROR = "error";
    public static final String ECHO = "echo";
    public static final String PLACE_MANA = "place_mana";
    public static final String ADD_TO_BATTLEZONE = "add_to_battlezone";
    public static final String HAND_POSITION = "hand_position";
    public static final String USE_SHIELD_TRIGGER = "use_shield_trigger";
    public static final String USE_BLOCKER = "use_blocker";

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
        System.out.println("Error occurred when handling in game choice. Exception: " + e);
        if (e.getCause() instanceof GameError) {
            GameError gameError = (GameError) e.getCause();
            String json = new JSONObject()
                    .put(TYPE, ERROR)
                    .put(ECHO, jsonObject)
                    .put(ERROR, gameError.toJson())
                    .toString();
            session.getRemote().sendString(json);
        } else {
            String json = new JSONObject()
                    .put(TYPE, ERROR)
                    .put(ECHO, jsonObject)
                    .put(ERROR, e)
                    .toString();
            session.getRemote().sendString(json);
        }
        // Print stack trace
        e.printStackTrace();
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

    @HandleWebSocketType(PLACE_MANA)
    public static void handlePlaceMana(JSONObject jsonObject, MainGameLoop gameLoop, MainGameLoop.Player player)
            throws IOException, GameError {
        System.out.println("Place mana");
        int handPosition = jsonObject.getInt(HAND_POSITION);
        gameLoop.placeMana(player, handPosition);
    }

    @HandleWebSocketType(ADD_TO_BATTLEZONE)
    public static void handleAddToBattleZone(JSONObject jsonObject, MainGameLoop gameLoop, MainGameLoop.Player player)
            throws IOException, GameError {
        System.out.println("Add to battle zone");
        int handPosition = jsonObject.getInt(HAND_POSITION);
        gameLoop.addToBattleZone(player, handPosition);
    }

    @HandleWebSocketType(USE_SHIELD_TRIGGER)
    public static void handleShieldTrigger(JSONObject jsonObject, MainGameLoop gameLoop, MainGameLoop.Player player)
            throws IOException, GameError {
        System.out.println("Shield interaction");
        boolean useShieldTrigger = jsonObject.getBoolean(USE_SHIELD_TRIGGER);
        gameLoop.shieldTriggerInteraction(player, useShieldTrigger);
    }

    @HandleWebSocketType(USE_BLOCKER)
    public static void handleBlockerInteraction(JSONObject jsonObject, MainGameLoop gameLoop, MainGameLoop.Player player)
            throws IOException, GameError {
        System.out.println("Blocker interaction");
        int handPosition = jsonObject.getInt(HAND_POSITION);
        gameLoop.blockerInteraction(player, handPosition);
    }
}

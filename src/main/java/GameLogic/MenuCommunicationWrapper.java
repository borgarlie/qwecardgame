package GameLogic;

import API.Game.WebSocketEndpoint;
import Pojos.GameIdAndPlayerId;
import Pojos.UsernameAndDeckId;
import Utils.HandleWebSocketType;
import Utils.ReflectionUtil;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
    public static final String DECK_ID = "deck_id";

    // game requests
    // Assuming that one player can only send one request, but receive multiple
    private static Map<String, UsernameAndDeckId> requests = new ConcurrentHashMap<>();
    // if request is "accepted" -> remove from request map and automaticly start game -> send "game start" to both players

    public static void handleMenuChoice(JSONObject jsonObject, Session session) throws IOException {
        String type = jsonObject.getString(TYPE);
        try {
            webSocketTypeMethods.get(type).invoke(null, jsonObject, session);
        } catch (Exception e) {
            handleError(e, jsonObject, session);
        }
    }

    private static void handleError(Exception e, JSONObject jsonObject, Session session) throws IOException {
        System.out.println("Error occurred when handling menu choice. Exception: " + e);
        e.printStackTrace();
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
    public static void handlePlayRequest(JSONObject jsonObject, Session session) throws IOException {
        System.out.println("Play request initiated");
        String initiatingUser = WebSocketEndpoint.waitingPlayersSessions.get(session);

        // TODO: Make sure that a user can only have one active request

        String requestedUser = jsonObject.getString(USERNAME);
        int deckId = jsonObject.getInt(DECK_ID);
        UsernameAndDeckId usernameAndDeckId = new UsernameAndDeckId(requestedUser, deckId);
        requests.put(initiatingUser, usernameAndDeckId);
        Session requestedUserSession = WebSocketEndpoint.waitingPlayers.get(requestedUser);
        String json = new JSONObject()
                .put(TYPE, PLAY_REQUEST)
                .put(USERNAME, initiatingUser)
                .toString();
        requestedUserSession.getRemote().sendString(json);
    }

    // Is it ok to include deck id here?
    @HandleWebSocketType(ACCEPT_REQUEST)
    public static void handleAcceptRequest(JSONObject jsonObject, Session session) throws IOException {
        System.out.println("Accept or deny request initiated");
        boolean accepted = jsonObject.getBoolean(ACCEPT_REQUEST);
        String acceptedRequestFromUser = jsonObject.getString(USERNAME);
        // Make sure that the user actually has sent a request, and it is still valid-
        String thisUser = WebSocketEndpoint.waitingPlayersSessions.get(session);
        String expectedUser = requests.get(acceptedRequestFromUser).getUsername();
        if (expectedUser == null || !thisUser.equals(expectedUser)) {
            System.out.println("Error while accepting request");
            // TODO: Handle error!
        }
        // Send game denied or accepted to requesting user
        Session requestUser = WebSocketEndpoint.waitingPlayers.get(acceptedRequestFromUser);
        String json = new JSONObject()
                .put(TYPE, ACCEPT_REQUEST)
                .put(ACCEPT_REQUEST, accepted)
                .put(USERNAME, thisUser)
                .toString();
        requestUser.getRemote().sendString(json);
        // If game was accepted -> start a new game
        if (accepted) {
            System.out.println("Request accepted");
            int player1deckId = requests.get(acceptedRequestFromUser).getDeckId();
            int player2deckId = jsonObject.getInt(DECK_ID);
            try {
                MainGameLoop mainGameLoop = new MainGameLoop(
                        requestUser,
                        acceptedRequestFromUser,
                        player1deckId,
                        session,
                        thisUser,
                        player2deckId);
                String gameId = mainGameLoop.gameId;
                GameIdAndPlayerId idPlayer1 = new GameIdAndPlayerId(gameId, MainGameLoop.Player.PLAYER1);
                GameIdAndPlayerId idPlayer2 = new GameIdAndPlayerId(gameId, MainGameLoop.Player.PLAYER2);
                // Requesting user is always player 1 and accepting user is always player 2.
                // Who starts is randomized in MainGameLoop init.
                WebSocketEndpoint.sessions.put(requestUser, idPlayer1);
                WebSocketEndpoint.sessions.put(session, idPlayer2);
                WebSocketEndpoint.games.put(gameId, mainGameLoop);
            } catch (GameError gameError) {
                gameError.printStackTrace();
                // TODO: Handle game setup error
            }
        } else {
            System.out.println("Request denied");
        }
        // Remove request
        requests.remove(acceptedRequestFromUser);
    }

    public static void endGame(String gameId) throws GameError {
        MainGameLoop game = WebSocketEndpoint.games.get(gameId);
        if (game.getTurn() != MainGameLoop.Player.NONE) {
            System.out.println("Bad state. Trying to end game while Player != NONE");
            throw new GameError(GameError.ErrorCode.BAD_STATE,
                    "Should not be possible to end game while a player has a turn");
        }
        // If we are going to save history of games, statistics, etc., we should do that here.
        // deleting game
        Session player1Session = game.getCurrentPlayerState(MainGameLoop.Player.PLAYER1).session;
        Session player2Session = game.getCurrentPlayerState(MainGameLoop.Player.PLAYER2).session;
        WebSocketEndpoint.sessions.remove(player1Session);
        WebSocketEndpoint.sessions.remove(player2Session);
        WebSocketEndpoint.games.remove(gameId);
    }

    // TODO: Implement withdraw request

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

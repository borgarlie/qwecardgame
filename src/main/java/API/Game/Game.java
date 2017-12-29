package API.Game;

import GameLogic.GameCommunicationWrapper;
import GameLogic.GameError;
import GameLogic.MainGameLoop;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONObject;
import org.json.JSONTokener;
//import org.json.simple.parser.JSONParser;
//import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@WebSocket(maxTextMessageSize = 64 * 1024)
public class Game {

    // These are messages used in the menu
    public static final String TYPE = "type";
    public static final String ERROR = "error";
    public static final String IN_GAME = "in_game";

    // Used for storing the id of the game and the player number (1 or 2)
    class GameIdAndPlayerId {
        String gameId;
        MainGameLoop.Player playerId;
    }

    // Map containing the game id and player number (1 or 2) for the given session
    private static Map<Session, GameIdAndPlayerId> sessions = new ConcurrentHashMap<>();
    // Map containing the game state for the given game id
    private static Map<String, MainGameLoop> games = new ConcurrentHashMap<>();

    // session -> username
    private static Map<Session, String> waitingPlayersSessions = new ConcurrentHashMap<>();
    // username -> session
    private static Map<String, Session> waitingPlayers = new ConcurrentHashMap<>();

    // game requests
    private static Map<String, String> requests = new ConcurrentHashMap<>();
    // if request is "accepted" -> remove from request map and automaticly start game -> send "game start" to both players

    private static int nextUserNumber = 1; // Assign to username for next connecting user

    private static GameCommunicationWrapper communicationWrapper = new GameCommunicationWrapper();

    @OnWebSocketConnect
    public void onConnection(Session session) {
        System.out.println("Connection established");
        String username = "User" + nextUserNumber++;
        waitingPlayersSessions.put(session, username);
        waitingPlayers.put(username, session);
        // Broadcast that a new player has joined and is available
        broadcastNewPlayer(username);
    }

    @OnWebSocketClose
    public void onClose(Session session, int status, String reason) {
        System.out.println("Connection closed with status: " + status + ", and reason: " + reason);
        String username = waitingPlayersSessions.get(session);
        waitingPlayersSessions.remove(session);
        waitingPlayers.remove(username);
        broadcastPlayerLeft(username);
    }

    @OnWebSocketMessage
    public void onString(Session session, String message) throws IOException {
        System.out.println("Message receieved as string");
        onText(session, message);
    }

    // Currently ignoring offset
    @OnWebSocketMessage
    public void onBytes(Session session, byte buf[], int offset, int length) throws IOException {
        System.out.println("Message received as byte");
        String message = new String(buf);
        onText(session, message);
    }

    private void onText(Session session, String message) throws IOException {
        if (session.isOpen()) {
            JSONObject jsonObject = new JSONObject(message);
            Boolean inGame = jsonObject.getBoolean(IN_GAME);
            // if false -> is in menu
            if (inGame) {
                GameIdAndPlayerId gameIdAndPlayerId = sessions.get(session);
                MainGameLoop gameLoop = games.get(gameIdAndPlayerId.gameId);
                try {
                    communicationWrapper.handleGameMove(jsonObject, gameLoop, gameIdAndPlayerId.playerId);
                } catch (GameError gameError) {
                    gameError.printStackTrace();
                    String json = new JSONObject()
                            .put(TYPE, ERROR)
                            .put(ERROR, gameError)
                            .toString();
                    session.getRemote().sendString(json);
                }
            } else {
                handleMenuChoice(jsonObject, session);
            }
        }
    }

    private static void handleMenuChoice(JSONObject jsonObject, Session session) throws IOException {
        String type = jsonObject.getString(TYPE);
        System.out.println(type);
        String json = new JSONObject()
                .put(TYPE, "test")
                .put("something", "something too")
                .toString();
        session.getRemote().sendString(json);
        // TODO: Request player for game
        // Accept / Deny request -> send response and init game
    }

    private static void broadcastNewPlayer(String username) {
        String sendMessage = new JSONObject()
                .put("type", "new_player")
                .put("username", username)
                .toString();
        System.out.println("Broadcasting message: " + sendMessage);
        waitingPlayersSessions.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(sendMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void broadcastPlayerLeft(String username) {
        String sendMessage = new JSONObject()
                .put("type", "player_left")
                .put("username", username)
                .toString();
        System.out.println("Broadcasting message: " + sendMessage);
        waitingPlayersSessions.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(sendMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.out.println("Error occured: " + error);
    }
}
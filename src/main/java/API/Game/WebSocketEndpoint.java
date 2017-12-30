package API.Game;

import GameLogic.GameCommunicationWrapper;
import GameLogic.GameError;
import GameLogic.MainGameLoop;
import GameLogic.MenuCommunicationWrapper;
import Pojos.GameIdAndPlayerId;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@WebSocket(maxTextMessageSize = 64 * 1024)
public class WebSocketEndpoint {

    // Messages used in the menu
    public static final String TYPE = "type";
    public static final String ERROR = "error";
    public static final String IN_GAME = "in_game";
    public static final String CONNECTED_PLAYERS = "connected_players";

    // Map containing the game id and player number (1 or 2) for the given session
    public static Map<Session, GameIdAndPlayerId> sessions = new ConcurrentHashMap<>();
    // Map containing the game state for the given game id
    public static Map<String, MainGameLoop> games = new ConcurrentHashMap<>();

    // session -> username
    public static Map<Session, String> waitingPlayersSessions = new ConcurrentHashMap<>();
    // username -> session
    public static Map<String, Session> waitingPlayers = new ConcurrentHashMap<>();

    private static int nextUserNumber = 1; // Assign to username for next connecting user

    private static GameCommunicationWrapper communicationWrapper = new GameCommunicationWrapper();

    @OnWebSocketConnect
    public void onConnection(Session session) throws IOException {
        System.out.println("Connection established");
        sendPlayerList(session);
        String username = "User" + nextUserNumber++;
        waitingPlayersSessions.put(session, username);
        waitingPlayers.put(username, session);
        // Broadcast that a new player has joined and is available
        broadcastNewPlayer(username);
    }

    private void sendPlayerList(Session session) throws IOException {
        ArrayList<String> users = new ArrayList<>();
        waitingPlayers.keySet().forEach(users::add);
        String json = new JSONObject()
                .put(TYPE, CONNECTED_PLAYERS)
                .put(CONNECTED_PLAYERS, users)
                .toString();
        session.getRemote().sendString(json);
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
            if (inGame) {
                GameIdAndPlayerId gameIdAndPlayerId = sessions.get(session);
                MainGameLoop gameLoop = games.get(gameIdAndPlayerId.getGameId());
                try {
                    communicationWrapper.handleGameMove(jsonObject, gameLoop, gameIdAndPlayerId.getPlayerId());
                } catch (GameError gameError) {
                    gameError.printStackTrace();
                    String json = new JSONObject()
                            .put(TYPE, ERROR)
                            .put(ERROR, gameError)
                            .toString();
                    session.getRemote().sendString(json);
                }
            } else {
                MenuCommunicationWrapper.handleMenuChoice(jsonObject, session);
            }
        }
    }

    // Should it be sent to the player itself?
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

package API.Game;

import GameLogic.GameCommunicationWrapper;
import GameLogic.MainGameLoop;
import GameLogic.MenuCommunicationWrapper;
import Pojos.GameIdAndPlayerId;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler {

    // Messages used in the menu
    public static final String TYPE = "type";
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

    public static ConnectHandler onConnect = session -> {
        System.out.println("Connection established");
        sendPlayerList(session);
        String username = "User" + nextUserNumber++;
        waitingPlayersSessions.put(session, username);
        waitingPlayers.put(username, session);
        // Broadcast that a new player has joined and is available
        broadcastNewPlayer(username);
    };

    public static CloseHandler onClose = (session, statusCode, reason) -> {
        System.out.println("Connection closed with status: " + statusCode + ", and reason: " + reason);
        String username = waitingPlayersSessions.get(session);
        waitingPlayersSessions.remove(session);
        waitingPlayers.remove(username);
        broadcastPlayerLeft(username);
    };

    private static void sendPlayerList(Session session) throws IOException {
        ArrayList<String> users = new ArrayList<>();
        waitingPlayers.keySet().forEach(users::add);
        String json = new JSONObject()
                .put(TYPE, CONNECTED_PLAYERS)
                .put(CONNECTED_PLAYERS, users)
                .toString();
        session.getRemote().sendString(json);
    }

    public static MessageHandler onMessage = (session, message) -> {
        System.out.println("Message receieved as string");
        onText(session, message);
    };

    // Currently ignoring offset
    public static BinaryMessageHandler onBinaryMessage = (session, bytes, offset, length) -> {
        System.out.println("Message received as byte");
//        String message = new String(bytes);
        // TODO: Fix bytes
        String message = "";

        // TODO: Why does not BinaryMessageHandler throw exception?
        try {
            onText(session, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    private static void onText(Session session, String message) throws IOException {
        if (session.isOpen()) {
            JSONObject jsonObject = new JSONObject(message);
            boolean inGame = jsonObject.getBoolean(IN_GAME);
            if (inGame) {
                GameIdAndPlayerId gameIdAndPlayerId = sessions.get(session);
                MainGameLoop gameLoop = games.get(gameIdAndPlayerId.getGameId());
                GameCommunicationWrapper.handleGameMove(jsonObject, gameLoop, gameIdAndPlayerId.getPlayerId(), session);
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

    public static ErrorHandler onError = (session, throwable) -> {
        System.out.println("Error occured: " + throwable);
    };

}

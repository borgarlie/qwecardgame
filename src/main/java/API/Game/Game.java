package API.Game;

import GameLogic.GameError;
import GameLogic.MainGameLoop;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@WebSocket(maxTextMessageSize = 64 * 1024)
public class Game {

    // Used for storing the id of the game and the player number (1 or 2)
    class GameIdAndPlayerId {
        String gameId;
        MainGameLoop.Player playerId;
    }

    // Map containing the game id and player number (1 or 2) for the given session
    private static Map<Session, GameIdAndPlayerId> sessions = new ConcurrentHashMap<>();
    // Map containing the game state for the given game id
    private static Map<String, MainGameLoop> games = new ConcurrentHashMap<>();

    // username -> session
    private static Map<String, Session> waitingPlayers = new ConcurrentHashMap<>();

    // game requests
    private static Map<String, String> requests = new ConcurrentHashMap<>();

    // if request is "accepted" -> remove from request map and automaticly start game -> send "game start" to both players

    // TODO: Use these maps.. implement the communication / wrapper layer


    private static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    private static int nextUserNumber = 1; // Assign to username for next connecting user

    @OnWebSocketConnect
    public void onConnection(Session session) {
        System.out.println("Connection established");
        String username = "User" + nextUserNumber++;
        userUsernameMap.put(session, username);
        broadcastMessage("Server", (username + " is ready for game"));
    }

    @OnWebSocketClose
    public void onClose(Session session, int status, String reason) {
        System.out.println("Connection closed with status: " + status + ", and reason: " + reason);
        String username = userUsernameMap.get(session);
        userUsernameMap.remove(session);
        broadcastMessage("Server", (username + " left the chat"));
    }

    @OnWebSocketMessage
    public void onString(Session session, String message)
    {
        System.out.println("Message receieved as string");
        onText(session, message);
    }

    // Currently ignoring offset
    @OnWebSocketMessage
    public void onBytes(Session session, byte buf[], int offset, int length)
    {
        System.out.println("Message received as byte");
        String message = new String(buf);
        onText(session, message);
    }

    private void onText(Session session, String message) {
        if (session.isOpen()) {

            GameIdAndPlayerId gameIdAndPlayerId = sessions.get(session);
            MainGameLoop gameLoop = games.get(gameIdAndPlayerId.gameId);

//            broadcastMessage(userUsernameMap.get(session), message);
//            String json = new JSONObject().toString();
            String json = message;
            // json.type == "endturn"
            if (json.equals("end_turn")) {
                try {
                    gameLoop.endTurn(gameIdAndPlayerId.playerId);
                } catch (GameError | IOException gameError) {
                    gameError.printStackTrace();
                }
            }
        }
    }

    private static void broadcastMessage(String sender, String message) {
        String sendMessage = new JSONObject()
                .put("username", sender)
                .put("message", message)
                .toString();
        System.out.println("Broadcasting message: " + sendMessage);
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
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
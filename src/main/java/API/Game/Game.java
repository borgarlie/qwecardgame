package API.Game;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@WebSocket(maxTextMessageSize = 64 * 1024)
public class Game
{

    private static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    private static int nextUserNumber = 1; // Assign to username for next connecting user

    @OnWebSocketConnect
    public void onConnection(Session session) {
        System.out.println("Connection established");
        String username = "User" + nextUserNumber++;
        userUsernameMap.put(session, username);
        broadcastMessage("Server", (username + " joined the chat"));
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
            broadcastMessage(userUsernameMap.get(session), message);
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
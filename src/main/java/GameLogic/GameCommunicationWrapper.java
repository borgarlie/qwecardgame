package GameLogic;


import org.json.JSONObject;

import java.io.IOException;

public class GameCommunicationWrapper {

    // These are messages received from the players
    public static final String TYPE = "type";
    public static final String END_TURN = "end_turn";

    // Here we know that the type is something in the game
    public void handleGameMove(JSONObject jsonObject, MainGameLoop gameLoop, MainGameLoop.Player player)
            throws IOException, GameError {
        String type = (String) jsonObject.get(TYPE);
        switch(type) {
            case END_TURN:
                gameLoop.endTurn(player);
                break;
            default:
                System.out.println("Type did not match anything");
        }
    }
}

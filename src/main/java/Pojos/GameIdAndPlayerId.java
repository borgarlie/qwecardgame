package Pojos;

import GameLogic.MainGameLoop;
import lombok.Getter;

// Used for storing the id of the game and the player number (1 or 2)
@Getter
public class GameIdAndPlayerId {
    String gameId;
    MainGameLoop.Player playerId;
}

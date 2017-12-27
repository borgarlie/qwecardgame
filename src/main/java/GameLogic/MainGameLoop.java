package GameLogic;


/*
    The main game loop. This is one single game state
 */
public class MainGameLoop {

    public enum Player {
        PLAYER1, PLAYER2
    }

    String gameId;
    PlayerState player1state;
    PlayerState player2state;
    Player turn;

    public MainGameLoop(String player1Username, int player1DeckId, String player2Username, int player2DeckId)
            throws GameError {
        initGame(player1Username, player1DeckId, player2Username, player2DeckId);
    }

    public void initGame(String player1Username, int player1DeckId, String player2Username, int player2DeckId)
            throws GameError {
        this.gameId = "123abc"; // TODO: Randomize this
        this.player1state = new PlayerState(MainGameLoop.Player.PLAYER1, player1Username, player1DeckId);
        this.player2state = new PlayerState(MainGameLoop.Player.PLAYER2, player2Username, player2DeckId);
        this.turn = Player.PLAYER1; // TODO: Randomize this
    }
}

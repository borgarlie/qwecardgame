package GameLogic;


import Pojos.Card;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;

import static GameLogic.GameError.ErrorCode.NOT_ALLOWED;

/*
    The main game loop. This is one single game state
 */
public class MainGameLoop {

    // These are messages going OUT from the server to players

    // Type = what kind of message is it.
    // Examples: new_turn, card_drawn, error, ???
    public static final String TYPE = "type";
    public static final String GAME_START = "game_start";
    public static final String NEW_TURN = "new_turn";
    public static final String ERROR = "error";

    public static final String START_HAND = "start_hand";
    public static final String DRAWN_CARD = "drawn_card";
    public static final String PLACED_MANA = "placed_mana";

    public enum Player {
        PLAYER1, PLAYER2
    }

    String gameId;
    PlayerState player1state;
    PlayerState player2state;
    Player turn;

    public MainGameLoop(Session player1sessision, String player1Username, int player1DeckId,
                        Session player2session, String player2Username, int player2DeckId)
            throws GameError, IOException {
        initGame(player1sessision, player1Username, player1DeckId, player2session, player2Username, player2DeckId);
    }

    private void initGame(Session player1session, String player1Username, int player1DeckId,
                         Session player2session, String player2Username, int player2DeckId)
            throws GameError, IOException {
        this.gameId = "123abc"; // TODO: Randomize this
        this.player1state = new PlayerState(MainGameLoop.Player.PLAYER1, player1session, player1Username, player1DeckId);
        this.player2state = new PlayerState(MainGameLoop.Player.PLAYER2, player2session, player2Username, player2DeckId);
        this.turn = Player.PLAYER1; // TODO: Randomize this
        // sending game start to both players with initial hand
        String gameStartPlayer1 = new JSONObject()
                .put(TYPE, GAME_START)
                .put(START_HAND, player1state.hand)
                .toString();
        player1session.getRemote().sendString(gameStartPlayer1);
        String gameStartPlayer2 = new JSONObject()
                .put(TYPE, GAME_START)
                .put(START_HAND, player2state.hand)
                .toString();
        player2session.getRemote().sendString(gameStartPlayer2);
        // manually sending new turn to player1, but no card (should be sent to "turn" player)
        String json = new JSONObject()
                .put(TYPE, NEW_TURN)
                .toString();
        player1session.getRemote().sendString(json);
    }

    public boolean isAllowedToMakeAMove(Player player) {
        return player == turn;
    }

    public void endTurn(Player player) throws GameError, IOException {
        // TODO: Make this check somewhere else?
        // Problem is that sometimes the other player needs to make a decision. E.g. whether or not to block an attack.
        if (!isAllowedToMakeAMove(player)) {
            throw new GameError(NOT_ALLOWED, "Not allowed to end turn when it is not your turn");
        }
        if (player == Player.PLAYER1) {
            turn = Player.PLAYER2;
        } else {
            turn = Player.PLAYER1;
        }
        startNewTurnForPlayer(turn);
    }

    private void startNewTurnForPlayer(Player player) throws IOException {
        PlayerState currentPlayerState = getCurrentPlayerState(player);
        Card drawn_card = currentPlayerState.startNewTurn();
        String json = new JSONObject()
            .put(TYPE, NEW_TURN)
            .put(DRAWN_CARD, drawn_card.toJson())
            .toString();
        currentPlayerState.session.getRemote().sendString(json);
    }

    private PlayerState getCurrentPlayerState(Player player) {
        if (player == Player.PLAYER1) {
            return player1state;
        }
        return player2state;
    }

    private PlayerState getOtherPlayerState(Player player) {
        if (player == Player.PLAYER1) {
            return player2state;
        }
        return player1state;
    }

    public void placeMana(Player player, int handPosition) throws GameError, IOException {
        if (!isAllowedToMakeAMove(player)) {
            throw new GameError(NOT_ALLOWED, "Not allowed to end turn when it is not your turn");
        }
        PlayerState currentPlayerState = getCurrentPlayerState(player);
        Card newManaCard = currentPlayerState.addMana(handPosition);
        String json = new JSONObject()
                .put(TYPE, PLACED_MANA)
                .put(PLACED_MANA, newManaCard.toJson())
                .toString();
        PlayerState otherPlayerState = getOtherPlayerState(player);
        otherPlayerState.session.getRemote().sendString(json);
    }
}

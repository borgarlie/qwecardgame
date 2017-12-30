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
    public static final String TYPE = "type";
    public static final String GAME_START = "game_start";
    public static final String NEW_TURN = "new_turn";
    public static final String START_HAND = "start_hand";
    public static final String DRAWN_CARD = "drawn_card";
    public static final String PLACED_MANA = "placed_mana";
    public static final String ADD_TO_BATTLEZONE = "add_to_battlezone";
    public static final String CHECK_FOR_SHIELD_TRIGGER = "check_for_shield_trigger";
    public static final String CARD = "card";
    public static final String ATTACK_WITH_POSITION = "attack_with_position";
    public static final String BLOCKER_INTERACTION = "blocker_interaction";
    public static final String SHIELD_TRIGGER = "shield_trigger";
    public static final String USE_BLOCKER = "USE_BLOCKER";

    public enum Player {
        PLAYER1, PLAYER2
    }

    String gameId;
    PlayerState player1state;
    PlayerState player2state;
    Player turn;

    boolean shieldTriggerInteractionActive = false;
    boolean blockerInteractionActive = false;

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
            throw new GameError(NOT_ALLOWED, "Not allowed to place mana when it is not your turn");
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

    public void addToBattleZone(Player player, int handPosition) throws GameError, IOException {
        if (!isAllowedToMakeAMove(player)) {
            throw new GameError(NOT_ALLOWED, "Not allowed to summon creature when it is not your turn");
        }
        PlayerState currentPlayerState = getCurrentPlayerState(player);
        Card summonedCreatureCard = currentPlayerState.addToBattleZone(handPosition);
        String json = new JSONObject()
                .put(TYPE, ADD_TO_BATTLEZONE)
                .put(ADD_TO_BATTLEZONE, summonedCreatureCard.toJson())
                .toString();
        PlayerState otherPlayerState = getOtherPlayerState(player);
        otherPlayerState.session.getRemote().sendString(json);
    }

    /*
        Attack another player. If the other player has a blocker, a new routine starts
        where the other player choose creature to block with or not to block.

        If the other player has no blockers, a shield is destroyed and added to the other players hand
        The other player has to say that he has received the shield and whether to use a shield trigger if present.
     */
    public void attackPlayer(Player player, int battleZonePosition) throws GameError, IOException {
        if (!isAllowedToMakeAMove(player)) {
            throw new GameError(NOT_ALLOWED, "Not allowed to attack player when it is not your turn");
        }
        PlayerState currentPlayerState = getCurrentPlayerState(player);
        Card attackCard = currentPlayerState.canAttackPlayer(battleZonePosition);
        PlayerState otherPlayerState = getOtherPlayerState(player);
        if (otherPlayerState.has_blocker()) {
            // Requires additional interaction before moving on
            String json = new JSONObject()
                    .put(TYPE, BLOCKER_INTERACTION)
                    .toString();
            currentPlayerState.session.getRemote().sendString(json);
            otherPlayerState.session.getRemote().sendString(json);
            blockerInteractionActive = true;
            return;
        }
        Card shield = otherPlayerState.attackThisPlayer();
        // Requires interaction before moving on
        String json = new JSONObject()
                .put(TYPE, CHECK_FOR_SHIELD_TRIGGER)
                .put(CHECK_FOR_SHIELD_TRIGGER, shield.isShield_trigger())
                .put(CARD, shield.toJson())
                .put(ATTACK_WITH_POSITION, battleZonePosition) // Need to send position back, since there can be multiple of the same card
                .toString();
        otherPlayerState.session.getRemote().sendString(json);
        String json = new JSONObject()
                .put(TYPE, CHECK_FOR_SHIELD_TRIGGER)
                .toString();
        currentPlayerState.session.getRemote().sendString(json);
        shieldTriggerInteractionActive = true;
        // Problem if we do not force this is that the initiating player KNOW that there is a shield trigger present.
        // Maybe we should FORCE interaction ? E.g. force the other player to press OK or something after getting a
        // card in his hand
        // and if it is a shield trigger, choose between "use" and "not use"
    }

    public void blockerInteraction(Player player, int battleZonePosition) throws GameError, IOException {
        if (!blockerInteractionActive) {
            throw new GameError(NOT_ALLOWED, "No shield trigger interaction active");
        }
        // TODO: Implement this
        PlayerState otherPlayerState = getOtherPlayerState(player);
        if (battleZonePosition < 0) {
            // Not using a blocker
            String json = new JSONObject()
                    .put(TYPE, BLOCKER_INTERACTION)
                    .put(USE_BLOCKER, false)
                    .toString();
            otherPlayerState.session.getRemote().sendString(json);
        }
        // using blocker
        // TODO: Need to include outcome... who wins the fight between attacking creature and blocker
        // Where to get attacking creature from? Hand position of attacking creature? save it?
        PlayerState currentPlayerState = getCurrentPlayerState(player);
        Card usedBlocker = currentPlayerState.useBlocker(battleZonePosition);
        blockerInteractionActive = false;
    }

    public void shieldTriggerInteraction(Player player, boolean useShieldTrigger)
            throws GameError, IOException {
        if (!shieldTriggerInteractionActive) {
            throw new GameError(NOT_ALLOWED, "No shield trigger interaction active");
        }
        PlayerState currentPlayerState = getCurrentPlayerState(player);
        PlayerState otherPlayerState = getOtherPlayerState(player);
        Card card = currentPlayerState.getLastCardAddedToHand();
        // Can not just send error if not shield trigger.
        // Need to send reply to attacking player that no shield trigger will be used
        if (!useShieldTrigger || !card.isShield_trigger()) {
            String json = new JSONObject()
                    .put(TYPE, SHIELD_TRIGGER)
                    .put(SHIELD_TRIGGER, false)
                    .toString();
            otherPlayerState.session.getRemote().sendString(json);
        }
        currentPlayerState.useShieldTrigger();
        shieldTriggerInteractionActive = false;
        String json = new JSONObject()
                .put(TYPE, SHIELD_TRIGGER)
                .put(SHIELD_TRIGGER, true)
                .put(CARD, card.toJson())
                .toString();
        otherPlayerState.session.getRemote().sendString(json);
    }
}

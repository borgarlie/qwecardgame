package GameLogic;


import Pojos.Card;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

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
    public static final String CARD_DIES = "card_dies";
    public static final String ATTACKING_WITH_POSITION = "attacking_with_position";
    public static final String ATTACKING_POSITION = "attacking_position";
    public static final String ATTACK_CREATURE = "attack_creature";

    public enum Player {
        PLAYER1, PLAYER2
    }

    public String gameId;

    private PlayerState player1state;
    private PlayerState player2state;
    private Player turn;

    private boolean shieldTriggerInteractionActive = false;
    private boolean blockerInteractionActive = false;
    private int attackingCreatureBattleZonePosition = -1;
    private int attackedCreatureBattleZonePosition = -1;

    public MainGameLoop(Session player1sessision, String player1Username, int player1DeckId,
                        Session player2session, String player2Username, int player2DeckId)
            throws GameError, IOException {
        initGame(player1sessision, player1Username, player1DeckId, player2session, player2Username, player2DeckId);
    }

    private void initGame(Session player1session, String player1Username, int player1DeckId,
                         Session player2session, String player2Username, int player2DeckId)
            throws GameError, IOException {
        this.gameId = this.getRandomGameId();
        this.player1state = new PlayerState(MainGameLoop.Player.PLAYER1, player1session, player1Username, player1DeckId);
        this.player2state = new PlayerState(MainGameLoop.Player.PLAYER2, player2session, player2Username, player2DeckId);
        this.turn = this.getRandomPlayer();
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
        // Send new turn without drawing card to start player
        String json = new JSONObject()
                .put(TYPE, NEW_TURN)
                .toString();
        Session currentPlayerSession = getCurrentTurnPlayerSession();
        currentPlayerSession.getRemote().sendString(json);
    }

    private Session getCurrentTurnPlayerSession() {
        if (turn == Player.PLAYER1) {
            return player1state.session;
        }
        return player2state.session;
    }

    private String getRandomGameId() {
        return UUID.randomUUID().toString();
    }

    private Player getRandomPlayer() {
        boolean random = ThreadLocalRandom.current().nextBoolean();
        if (random) {
            return Player.PLAYER2;
        }
        return Player.PLAYER1;
    }

    public boolean isAllowedToMakeAMove(Player player) {
        return player == turn;
    }

    // TODO: some effects on cards are triggered when turn ends, e.g. "untap creature at end of turn"
    // ^ These effects do probably not need to be sent to the players..
    // they require no interaction and can be handled automatically here and on client side separetely for both players.
    // Temporal effects needs to be removed at this point.
    public void endTurn(Player player) throws GameError, IOException {
        if (!isAllowedToMakeAMove(player)) {
            throw new GameError(NOT_ALLOWED, "Not allowed to end turn when it is not your turn");
        }
        // If a summoned creature has "must attack" and has not attacked then the player is not allowed to end the turn
        // Unless the creature is already tapped or has summoning creature
        PlayerState currentPlayerState = getCurrentPlayerState(player);
        Optional<Card> mustAttackCard = currentPlayerState.hasUntappedMustAttackCreature();
        if (mustAttackCard.isPresent()) {
            throw new GameError(
                    NOT_ALLOWED,
                    "Not allowed to end turn without attacking with " + mustAttackCard.get().getName());
        }
        // handle end of turn variables
        currentPlayerState.handleEndOfTurnVariables();
        // Change who's turn it is, and start new turn
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

    private Player getOtherPlayer(Player player) {
        if (player == Player.PLAYER1) {
            return Player.PLAYER2;
        }
        return Player.PLAYER1;
    }

    // package-private
    PlayerState getCurrentPlayerState(Player player) {
        if (player == Player.PLAYER1) {
            return player1state;
        }
        return player2state;
    }

    // package-private
    PlayerState getOtherPlayerState(Player player) {
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
        // Handle "When you put this creature into the battle zone" effects
        handleSummonCreatureEffects(player, summonedCreatureCard);
    }

    private void handleSummonCreatureEffects(Player player, Card summonedCreatureCard) {
        // TODO: Implement this
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
        currentPlayerState.tapCreature(battleZonePosition); // tap attacking creature
        attackingCreatureBattleZonePosition = battleZonePosition;
        PlayerState otherPlayerState = getOtherPlayerState(player);
        if (attackCard.canBeBlocked() && otherPlayerState.has_blocker()) {
            // Requires additional interaction before moving on
            String json = new JSONObject()
                    .put(TYPE, BLOCKER_INTERACTION)
                    .put(ATTACKING_WITH_POSITION, battleZonePosition)
                    .put(ATTACKING_POSITION, -1) // -1 = player in this case
                    .toString();
            currentPlayerState.session.getRemote().sendString(json);
            otherPlayerState.session.getRemote().sendString(json);
            blockerInteractionActive = true;
            return;
        }
        continueAttackingPlayer(player);
    }

    public void attackCreature(Player player, int battleZonePosition, int attackCreatureInPosition)
            throws GameError, IOException {
        if (!isAllowedToMakeAMove(player)) {
            throw new GameError(NOT_ALLOWED, "Not allowed to attack player when it is not your turn");
        }
        PlayerState otherPlayerState = getOtherPlayerState(player);
        Card attackedCreatureCard = otherPlayerState.getCardInBattleZonePosition(attackCreatureInPosition);
        PlayerState currentPlayerState = getCurrentPlayerState(player);
        Card attackCard = currentPlayerState.canAttackCreature(battleZonePosition, attackedCreatureCard);
        currentPlayerState.tapCreature(battleZonePosition); // tap attacking creature
        if (attackCard.canBeBlocked() && otherPlayerState.has_blocker()) {
            attackingCreatureBattleZonePosition = battleZonePosition;
            attackedCreatureBattleZonePosition = attackCreatureInPosition;
            // Requires additional interaction before moving on
            String json = new JSONObject()
                    .put(TYPE, BLOCKER_INTERACTION)
                    .put(ATTACKING_WITH_POSITION, battleZonePosition)
                    .put(ATTACKING_POSITION, attackCreatureInPosition)
                    .toString();
            currentPlayerState.session.getRemote().sendString(json);
            otherPlayerState.session.getRemote().sendString(json);
            blockerInteractionActive = true;
            return;
        }
        continueAttackingCreature(player, battleZonePosition, attackCreatureInPosition);
    }

    private void continueAttackingCreature(Player player, int battleZonePosition, int attackCreatureInPosition)
            throws IOException {
        PlayerState currentPlayerState = getCurrentPlayerState(player);
        PlayerState otherPlayerState = getOtherPlayerState(player);
        Card attackedCreatureCard = otherPlayerState.getCardInBattleZonePosition(attackCreatureInPosition);
        Card attackingCard = currentPlayerState.getCardInBattleZonePosition(battleZonePosition);
        int outcome = attackingCard.fight(attackedCreatureCard);
        String cardLivesJson = new JSONObject()
                .put(TYPE, ATTACK_CREATURE)
                .put(CARD_DIES, false)
                .toString();
        String cardDiesJson = new JSONObject()
                .put(TYPE, ATTACK_CREATURE)
                .put(CARD_DIES, true)
                .toString();
        switch (outcome) {
            case 1:
                // Attacker wins
                otherPlayerState.killCardInBattlezone(attackCreatureInPosition);
                currentPlayerState.session.getRemote().sendString(cardLivesJson);
                otherPlayerState.session.getRemote().sendString(cardDiesJson);
                break;
            case -1:
                // Attacked Creature wins
                currentPlayerState.killCardInBattlezone(battleZonePosition);
                currentPlayerState.session.getRemote().sendString(cardDiesJson);
                otherPlayerState.session.getRemote().sendString(cardLivesJson);
                break;
            default:
                // Draw - both dies
                currentPlayerState.killCardInBattlezone(battleZonePosition);
                otherPlayerState.killCardInBattlezone(attackCreatureInPosition);
                currentPlayerState.session.getRemote().sendString(cardDiesJson);
                otherPlayerState.session.getRemote().sendString(cardDiesJson);
                break;
        }
    }

    private void continueAttackingPlayer(Player player) throws IOException {
        PlayerState currentPlayerState = getCurrentPlayerState(player);
        PlayerState otherPlayerState = getOtherPlayerState(player);
        Card attackingCard = currentPlayerState.getCardInBattleZonePosition(attackingCreatureBattleZonePosition);
        Card shield = otherPlayerState.attackThisPlayer();
        // TODO: if attackingCard destroys more than 1 shield, then we need to loop this and return multiple shields..
        // TODO: What if there are multiple shield triggers?
        int battleZonePosition = this.attackingCreatureBattleZonePosition;
        // Requires interaction before moving on
        String json = new JSONObject()
                .put(TYPE, CHECK_FOR_SHIELD_TRIGGER)
                .put(CHECK_FOR_SHIELD_TRIGGER, shield.isShield_trigger())
                .put(CARD, shield.toJson())
                .put(ATTACK_WITH_POSITION, battleZonePosition)
                // Need to send position to other player,
                // since there can be multiple of the same card
                .toString();
        otherPlayerState.session.getRemote().sendString(json);
        String json2 = new JSONObject()
                .put(TYPE, CHECK_FOR_SHIELD_TRIGGER)
                .toString();
        currentPlayerState.session.getRemote().sendString(json2);
        shieldTriggerInteractionActive = true;
        // Problem if we do not force this is that the initiating player KNOW that there is a shield trigger present.
        // Maybe we should FORCE interaction ? E.g. force the other player to press OK or something after getting a
        // card in his hand
        // and if it is a shield trigger, choose between "use" and "not use"
    }

    public void blockerInteraction(Player player, int battleZonePosition) throws GameError, IOException {
        if (!blockerInteractionActive) {
            throw new GameError(NOT_ALLOWED, "No blocker interaction active");
        }
        PlayerState otherPlayerState = getOtherPlayerState(player);
        if (battleZonePosition < 0) {
            // Not using a blocker
            // We do not need to send information that the player is not blocking
            // Because when "check for shield trigger" is sent, we know it has not been blocked
            //
            // Continue with breaking shield
            // Need to switch the player, as it is not the blocking player,
            // but the other player that initiated the attack
            player = getOtherPlayer(player);
            // Check if we are attacking creature or player
            if (this.attackedCreatureBattleZonePosition > -1) {
                // attacking creature
                continueAttackingCreature(player, attackingCreatureBattleZonePosition, attackedCreatureBattleZonePosition);
                attackingCreatureBattleZonePosition = -1; // reset global value
                attackedCreatureBattleZonePosition = -1; // reset global value
            }
            else {
                // attacking player
                continueAttackingPlayer(player);
            }
            blockerInteractionActive = false; // reset global value
            return;
        }
        // using blocker
        PlayerState currentPlayerState = getCurrentPlayerState(player);
        Card blockingCard = currentPlayerState.getCardInBattleZonePosition(battleZonePosition);
        if (!blockingCard.isBlocker()) {
            throw new GameError(NOT_ALLOWED, "This card is not a blocker");
        }
        Card attackingCard = otherPlayerState.getCardInBattleZonePosition(attackingCreatureBattleZonePosition);
        int outcome = blockingCard.fight(attackingCard, true);
        currentPlayerState.tapCreature(battleZonePosition); // tap blocker

        String cardLivesJson = new JSONObject()
                .put(TYPE, USE_BLOCKER)
                .put(CARD_DIES, false)
                .toString();
        String cardDiesJson = new JSONObject()
                .put(TYPE, USE_BLOCKER)
                .put(CARD_DIES, true)
                .toString();
        switch (outcome) {
            case 1:
                // Blocker wins
                otherPlayerState.killCardInBattlezone(attackingCreatureBattleZonePosition);
                currentPlayerState.session.getRemote().sendString(cardLivesJson);
                otherPlayerState.session.getRemote().sendString(cardDiesJson);
                break;
            case -1:
                // Attacker wins
                currentPlayerState.killCardInBattlezone(battleZonePosition);
                currentPlayerState.session.getRemote().sendString(cardDiesJson);
                otherPlayerState.session.getRemote().sendString(cardLivesJson);
                break;
            default:
                // Draw - both dies
                currentPlayerState.killCardInBattlezone(battleZonePosition);
                otherPlayerState.killCardInBattlezone(attackingCreatureBattleZonePosition);
                currentPlayerState.session.getRemote().sendString(cardDiesJson);
                otherPlayerState.session.getRemote().sendString(cardDiesJson);
                break;
        }
        // TODO: What to do about effects on blocker cards and attacking cards?
        attackingCreatureBattleZonePosition = -1; // reset global value
        attackedCreatureBattleZonePosition = -1; // reset global value
        blockerInteractionActive = false; // reset global value
    }

    // TODO: Should be possible to call this twice. e.g use "num_shield_triggers"
    // Also need to make it possible to choose which one to use first and last if there are multiple
    // (could be 0, 1, 2 or 3)
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
        shieldTriggerInteractionActive = false; // reset global value
        String json = new JSONObject()
                .put(TYPE, SHIELD_TRIGGER)
                .put(SHIELD_TRIGGER, true)
                .put(CARD, card.toJson())
                .toString();
        otherPlayerState.session.getRemote().sendString(json);
        attackingCreatureBattleZonePosition = -1; // reset global value
    }

    public void useSpellCard(
            Player player,
            int handPosition,
            List<Integer> useOnOpponentCards,
            List<Integer> useOnOwnCards) throws GameError {
        if (!isAllowedToMakeAMove(player)) {
            throw new GameError(NOT_ALLOWED, "Not allowed to use spell card when it is not your turn");
        }
        PlayerState currentPlayerState = getCurrentPlayerState(player);
        Card spellCard = currentPlayerState.canUseSpellCard(handPosition);
        SpellHandler.handleSpell(player, this, spellCard);
    }

}

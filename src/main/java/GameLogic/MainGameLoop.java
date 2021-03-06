package GameLogic;


import Pojos.Card;
import Pojos.DestroyCreatureEffect;
import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static GameLogic.GameError.ErrorCode.NOT_ALLOWED;

/*
    The main game loop. This is one single game state
 */
public class MainGameLoop {

    // These are messages going OUT from the server to players
    private static final String TYPE = "type";
    private static final String GAME_START = "game_start";
    private static final String NEW_TURN = "new_turn";
    private static final String START_HAND = "start_hand";
    private static final String DRAWN_CARD = "drawn_card";
    private static final String PLACED_MANA = "placed_mana";
    private static final String ADD_TO_BATTLEZONE = "add_to_battlezone";
    private static final String CHECK_FOR_SHIELD_TRIGGER = "check_for_shield_trigger";
    private static final String NUM_SHIELD_TRIGGERS = "num_shield_triggers";
    private static final String CARD = "card";
    private static final String CARDS = "cards";
    private static final String ATTACK_WITH_POSITION = "attack_with_position";
    private static final String BLOCKER_INTERACTION = "blocker_interaction";
    private static final String SHIELD_TRIGGER = "shield_trigger";
    private static final String USE_BLOCKER = "USE_BLOCKER";
    private static final String ATTACKING_WITH_POSITION = "attacking_with_position";
    private static final String BLOCKING_WITH_POSITION = "blocking_with_position";
    private static final String ATTACKING_POSITION = "attacking_position";
    private static final String ATTACKER_POSITION = "attacker_position";
    private static final String ATTACK_CREATURE = "attack_creature";
    private static final String USE_ON_OWN_CARDS = "use_on_own_cards"; // list
    private static final String USE_ON_OPPONENT_CARDS = "use_on_opponent_cards"; // list
    private static final String ATTACKER_DIES = "attacker_dies";
    private static final String BLOCKER_DIES = "blocker_dies";
    private static final String ATTACKED_CREATURE_DIES = "attacked_creature_dies";
    private static final String GAME_END = "game_end";
    private static final String WINNER = "winner";

    public enum Player {
        PLAYER1, PLAYER2, NONE
    }

    public enum Step {
        MANA, SUMMON, BATTLE
    }

    public String gameId;

    private PlayerState player1state;
    private PlayerState player2state;
    private Player turn;

    public Step currentStep = Step.MANA;

    private boolean shieldTriggerInteractionActive = false;
    private boolean blockerInteractionActive = false;
    private int attackingCreatureBattleZonePosition = -1;
    private int attackedCreatureBattleZonePosition = -1;
    private int shield_triggers = 0;

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

    public Player getTurn() {
        return turn;
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

    // A player is only allowed to make a move when it is that players turn,
    // and there is no shield trigger or blocker interaction active
    public boolean isAllowedToMakeAMove(Player player) {
        return player == turn && !shieldTriggerInteractionActive && !blockerInteractionActive;
    }

    // Some effects on cards are triggered when turn ends, e.g. "untap creature at end of turn"
    // ^ These effects do probably not need to be sent to the players.
    // they require no interaction and can be handled automatically here and on client side separetely for both players.
    // Temporal effects are removed at this point.
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

    public void addToBattleZone(
            Player player,
            int handPosition,
            List<Integer> useOnOpponentCards,
            List<Integer> useOnOwnCards) throws GameError, IOException {
        if (!isAllowedToMakeAMove(player)) {
            throw new GameError(NOT_ALLOWED, "Not allowed to summon creature when it is not your turn");
        }
        // Check if chosen cards exists (in case of effects used)
        PlayerState currentPlayerState = getCurrentPlayerState(player);
        PlayerState otherPlayerState = getOtherPlayerState(player);
        for (int battleZonePosition : useOnOpponentCards) {
            otherPlayerState.getCardInBattleZonePosition(battleZonePosition);
        }
        for (int battleZonePosition : useOnOwnCards) {
            currentPlayerState.getCardInBattleZonePosition(battleZonePosition);
        }
        // Add card to battle zone if allowed
        Card summonedCreatureCard = currentPlayerState.addToBattleZone(handPosition);
        // Send message to other player
        String json = new JSONObject()
                .put(TYPE, ADD_TO_BATTLEZONE)
                .put(ADD_TO_BATTLEZONE, summonedCreatureCard.toJson())
                .put(USE_ON_OPPONENT_CARDS, useOnOpponentCards)
                .put(USE_ON_OWN_CARDS, useOnOwnCards)
                .toString();
        otherPlayerState.session.getRemote().sendString(json);
        // Handle "When you put this creature into the battle zone" effects
        SummonCreatureEffectHandler.handleEffect(player, this, summonedCreatureCard, useOnOpponentCards, useOnOwnCards);
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
        // trigger temp on attack effects
        TempOnAttackEffectHandler.handleEffect(player, this, battleZonePosition, -1);
        // check for possible blocking
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
        // trigger temp on attack effects for attacker
        TempOnAttackEffectHandler.handleEffect(player, this, battleZonePosition, attackCreatureInPosition);
        // trigger temp on attack effects for attacked creature
        Player otherPlayer = getOtherPlayer(player);
        TempOnAttackEffectHandler.handleEffect(otherPlayer, this, attackCreatureInPosition, battleZonePosition);
        // check for possible blocking
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
            throws IOException, GameError {
        PlayerState currentPlayerState = getCurrentPlayerState(player);
        PlayerState otherPlayerState = getOtherPlayerState(player);
        Card attackedCreatureCard = otherPlayerState.getCardInBattleZonePosition(attackCreatureInPosition);
        Card attackingCard = currentPlayerState.getCardInBattleZonePosition(battleZonePosition);
        // initiate fight
        int outcome = attackingCard.fight(attackedCreatureCard);
        switch (outcome) {
            case 1: // Attacker wins
                DestroyCreatureEffectHandler.handleEffect(otherPlayerState, attackCreatureInPosition);
                break;
            case -1: // Attacked Creature wins
                DestroyCreatureEffectHandler.handleEffect(currentPlayerState, battleZonePosition);
                break;
            default: // Draw - both dies
                DestroyCreatureEffectHandler.handleEffect(otherPlayerState, attackCreatureInPosition);
                DestroyCreatureEffectHandler.handleEffect(currentPlayerState, battleZonePosition);
                break;
        }
        // The attacked person still do not know any details about the fight (unless there was a blocker interaction).
        // Sending the information about attacker position and attacked position as well as the outcome
        boolean attacker_dies = (outcome == -1 || outcome == 0);
        boolean attacked_creature_dies = (outcome == 1 || outcome == 0);
        String fightOutcomeJson = new JSONObject()
                .put(TYPE, ATTACK_CREATURE)
                .put(ATTACKING_WITH_POSITION, battleZonePosition)
                .put(ATTACKING_POSITION, attackCreatureInPosition)
                .put(ATTACKER_DIES, attacker_dies)
                .put(ATTACKED_CREATURE_DIES, attacked_creature_dies)
                .toString();
        currentPlayerState.session.getRemote().sendString(fightOutcomeJson);
        otherPlayerState.session.getRemote().sendString(fightOutcomeJson);
        // remove temp on attack effects
        currentPlayerState.removeAllTempOnAttackEffects();
        otherPlayerState.removeAllTempOnAttackEffects();
    }

    private void continueAttackingPlayer(Player player) throws IOException, GameError {
        PlayerState currentPlayerState = getCurrentPlayerState(player);
        PlayerState otherPlayerState = getOtherPlayerState(player);
        Card attackingCard = currentPlayerState.getCardInBattleZonePosition(attackingCreatureBattleZonePosition);
        ArrayList<Card> shields = otherPlayerState.attackThisPlayer(attackingCard);
        if (shields.isEmpty()) {
            initiateWinGame(player); // attacking player has won
            return;
        }
        int numShieldTriggers = (int) shields.stream().filter(Card::isShield_trigger).count();
        ArrayList<JSONObject> shieldsAsJson
                = shields.stream().map(Card::toJson).collect(Collectors.toCollection(ArrayList::new));
        // Requires interaction before moving on
        String json = new JSONObject()
                .put(TYPE, CHECK_FOR_SHIELD_TRIGGER)
                .put(NUM_SHIELD_TRIGGERS, numShieldTriggers)
                .put(CARDS, shieldsAsJson)
                .put(ATTACK_WITH_POSITION, attackingCreatureBattleZonePosition)
                // Need to send position to other player,
                // since there can be multiple of the same card
                .toString();
        otherPlayerState.session.getRemote().sendString(json);
        String json2 = new JSONObject()
                .put(TYPE, CHECK_FOR_SHIELD_TRIGGER)
                .toString();
        currentPlayerState.session.getRemote().sendString(json2);
        // Have to force shield trigger interaction regardless of whether or not it is a shield trigger.
        // Otherwise the other player would know that it was a shield trigger.
        this.shield_triggers = shields.size();
        shieldTriggerInteractionActive = true;
        // remove temp on attack effects
        currentPlayerState.removeAllTempOnAttackEffects();
        otherPlayerState.removeAllTempOnAttackEffects();
    }

    private void initiateWinGame(Player player) throws IOException, GameError {
        System.out.println(player + " wins.");
        PlayerState winner = getCurrentPlayerState(player);
        PlayerState looser = getOtherPlayerState(player);
        String winnerMessage = new JSONObject()
                .put(TYPE, GAME_END)
                .put(WINNER, true)
                .toString();
        String looserMessage = new JSONObject()
                .put(TYPE, GAME_END)
                .put(WINNER, false)
                .toString();
        winner.session.getRemote().sendString(winnerMessage);
        looser.session.getRemote().sendString(looserMessage);
        // Set turn = NONE, so that we have a way to know that a game has ended
        this.turn = Player.NONE;
        MenuCommunicationWrapper.endGame(this.gameId);
    }

    public void blockerInteraction(Player player, int battleZonePosition) throws GameError, IOException {
        // Only the attacked player can use blocker
        if (player == turn || !blockerInteractionActive) {
            throw new GameError(NOT_ALLOWED, "No blocker interaction active");
        }
        PlayerState otherPlayerState = getOtherPlayerState(player);
        if (battleZonePosition < 0) {
            // Not using a blocker
            // We do not need to send information that the player is not blocking
            // Because when "check for shield trigger" is sent, we know it has not been blocked

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
        // trigger temp on attack effects for blocker
        TempOnAttackEffectHandler.handleEffect(player, this, battleZonePosition, attackingCreatureBattleZonePosition);
        // initiate fight
        Card attackingCard = otherPlayerState.getCardInBattleZonePosition(attackingCreatureBattleZonePosition);
        int outcome = blockingCard.fight(attackingCard, true);
        currentPlayerState.tapCreature(battleZonePosition); // tap blocker
        switch (outcome) {
            case 1: // Blocker wins
                DestroyCreatureEffectHandler.handleEffect(otherPlayerState, attackingCreatureBattleZonePosition);
                break;
            case -1: // Attacker wins (destroys blocker)
                DestroyCreatureEffectHandler.handleEffect(currentPlayerState, battleZonePosition);
                break;
            default: // Draw - both dies
                DestroyCreatureEffectHandler.handleEffect(otherPlayerState, attackingCreatureBattleZonePosition);
                DestroyCreatureEffectHandler.handleEffect(currentPlayerState, battleZonePosition);
                break;
        }
        // Sending the information about blocker position and attacker position (blocked card) as well as the outcome
        boolean blocker_dies = (outcome == -1 || outcome == 0);
        boolean attacker_dies = (outcome == 1 || outcome == 0);
        String fightOutcomeJson = new JSONObject()
                .put(TYPE, USE_BLOCKER)
                .put(BLOCKING_WITH_POSITION, battleZonePosition)
                .put(ATTACKER_POSITION, attackingCreatureBattleZonePosition)
                .put(BLOCKER_DIES, blocker_dies)
                .put(ATTACKER_DIES, attacker_dies)
                .toString();
        currentPlayerState.session.getRemote().sendString(fightOutcomeJson);
        otherPlayerState.session.getRemote().sendString(fightOutcomeJson);
        // reset global values
        attackingCreatureBattleZonePosition = -1; // reset global value
        attackedCreatureBattleZonePosition = -1; // reset global value
        blockerInteractionActive = false; // reset global value
        // remove temp on attack effects
        currentPlayerState.removeAllTempOnAttackEffects();
        otherPlayerState.removeAllTempOnAttackEffects();
    }

    // This function needs to be called shield_triggers times.
    // It does not matter which way the triggers are called.
    // The attacking player will get noticed whether or not a shield trigger will be used
    // the same number of times as this function needs to be called, and need to wait for these notices.
    public void shieldTriggerInteraction(
            Player player,
            boolean useShieldTrigger,
            int handPosition,
            List<Integer> useOnOpponentCards,
            List<Integer> useOnOwnCards) throws GameError, IOException {
        // Only the attacked player can use shield triggers
        if (player == turn || !shieldTriggerInteractionActive) {
            throw new GameError(NOT_ALLOWED, "No shield trigger interaction active");
        }
        PlayerState currentPlayerState = getCurrentPlayerState(player);
        PlayerState otherPlayerState = getOtherPlayerState(player);
        if (handPosition < currentPlayerState.hand.size() - this.shield_triggers) {
            throw new GameError(GameError.ErrorCode.NOT_ALLOWED,
                    "Can only use shield triggers from shields that were just destroyed");
        }
        if (!useShieldTrigger) {
            // Need to send reply to attacking player that no shield trigger will be used
            String json = new JSONObject()
                    .put(TYPE, SHIELD_TRIGGER)
                    .put(SHIELD_TRIGGER, false)
                    .toString();
            otherPlayerState.session.getRemote().sendString(json);
        }
        Card card = currentPlayerState.getCardInHandPosition(handPosition);
        if (!card.isShield_trigger()) {
            // Need to send reply to attacking player that no shield trigger will be used
            String json = new JSONObject()
                    .put(TYPE, SHIELD_TRIGGER)
                    .put(SHIELD_TRIGGER, false)
                    .toString();
            otherPlayerState.session.getRemote().sendString(json);
        }
        // Use shield trigger
        if (useShieldTrigger && card.isShield_trigger()) {
            String json = new JSONObject()
                    .put(TYPE, SHIELD_TRIGGER)
                    .put(SHIELD_TRIGGER, true)
                    .put(CARD, card.toJson())
                    .toString();
            otherPlayerState.session.getRemote().sendString(json);
            if (card.is_spell()) {
                System.out.println("Spell shield trigger used.");
                SpellHandler.handleSpell(player, this, card, useOnOpponentCards, useOnOwnCards);
            }
            else {
                System.out.println("Creature shield trigger used");
                // TODO: Implement creature shield trigger (Not present in DM-01)
            }
        }
        // reduce number of shield triggers left to check
        this.shield_triggers = this.shield_triggers - 1;
        if (this.shield_triggers == 0) {
            this.shieldTriggerInteractionActive = false;
            this.attackingCreatureBattleZonePosition = -1; // reset global value
        }
    }

    public void useSpellCard(
            Player player,
            int handPosition,
            List<Integer> useOnOpponentCards,
            List<Integer> useOnOwnCards) throws GameError, IOException {
        if (!isAllowedToMakeAMove(player)) {
            throw new GameError(NOT_ALLOWED, "Not allowed to use spell card when it is not your turn");
        }
        PlayerState currentPlayerState = getCurrentPlayerState(player);
        Card spellCard = currentPlayerState.canUseSpellCard(handPosition);
        // check that all opponent cards and own cards exists in the battle zone
        PlayerState otherPlayerState = getOtherPlayerState(player);
        for (int battleZonePosition : useOnOpponentCards) {
            otherPlayerState.getCardInBattleZonePosition(battleZonePosition);
        }
        for (int battleZonePosition : useOnOwnCards) {
            currentPlayerState.getCardInBattleZonePosition(battleZonePosition);
        }
        SpellHandler.handleSpell(player, this, spellCard, useOnOpponentCards, useOnOwnCards);
    }

}

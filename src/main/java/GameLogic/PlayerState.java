package GameLogic;

import Database.DeckDatabase;
import Pojos.Card;
import Pojos.Deck;
import org.eclipse.jetty.websocket.api.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static GameLogic.GameError.ErrorCode.*;

public class PlayerState {

    // Global game config
    private static final int STARTSHIELDS = 5;
    private static final int STARTCARDS = 5;

    // Non-changing variables for the player state
    MainGameLoop.Player player;
    Session session;
    String username;
    Deck initialDeck;

    // These 6 lists should always add up to initialDeck.cards.size() after a move is completed

    // Last card is "top" card
    ArrayList<Card> deck;
    ArrayList<Card> hand = new ArrayList<>();
    ArrayList<Card> shields = new ArrayList<>();
    ArrayList<Card> manazone = new ArrayList<>();
    ArrayList<Card> graveyard = new ArrayList<>();
    ArrayList<Card> battlezone = new ArrayList<>();

    // Other interactive variables
    boolean has_added_mana = false;

    public PlayerState(MainGameLoop.Player player, Session session, String username, int deck_id) throws GameError {
        this.player = player;
        this.session = session;
        // TODO: User userId instead of username?
        this.username = username;
        Optional<Deck> optDeck = DeckDatabase.get(deck_id);
        if (!optDeck.isPresent()) {
            throw new GameError(INIT_ERROR, "Deck with id: " + deck_id + " not found");
        }
        initialDeck = optDeck.get();
        if (!initialDeck.getUserId().equals(username) && !initialDeck.getUserId().equals("1234567890qwerty")) {
            throw new GameError(INIT_ERROR, "Deck does not belong to this user");
        }
        initPlayerState();
    }

    private void initPlayerState() {
        // Initialise deck
        this.deck = initialDeck.initialiseDeck();
        // TODO: Add check that deck size is correct (40 exact?)
        // Shuffle deck
        this.shuffleDeck();
        // Initialize shields
        for (int i = 0; i < STARTSHIELDS; i++) {
            this.addShieldFromDeck();
        }
        // Initialise hand
        for (int i = 0; i < STARTCARDS; i++) {
            this.drawCard();
        }
    }

    // Should not be called if it is the first turn of the starting player,
    // as that player do not get to draw card the first round
    public Card startNewTurn() {
        // reset turn variables
        this.has_added_mana = false;
        // untap all tapped cards
        untapBattlezone();
        untapManazone();
        // remove summoning sickness
        removeSummoningSicknessFromBattleZone();
        // draw new card
        return this.drawCard();
    }

    public void handleEndOfTurnVariables() {
        this.battlezone.forEach(card -> {
            // untap cards with effect "untap at end of turn"
            if (card.isUntap_at_end()) {
                card.setTapped(false);
            }
            // reset temporal variables
            card.removeTemporalEffects();
        });
    }

    private void removeSummoningSicknessFromBattleZone() {
        this.battlezone.forEach(card -> card.setSummoningSickness(false));
    }

    private void untapBattlezone() {
        this.battlezone.forEach(card -> card.setTapped(false));
    }

    private void untapManazone() {
        this.manazone.forEach(card -> card.setTapped(false));
    }

    public void shuffleDeck() {
        Collections.shuffle(this.deck);
    }

    public void addShieldFromDeck() {
        Card card = getAndRemoveTopDeckCard();
        this.shields.add(card);
    }

    private Card getAndRemoveTopDeckCard() {
        Card card = this.deck.get(this.deck.size() - 1);
        this.deck.remove(this.deck.size() - 1);
        // create deep copy
        return card.toBuilder().build();
    }

    /*
        Returns the drawn card
     */
    public Card drawCard() {
        Card card = getAndRemoveTopDeckCard();
        this.hand.add(card);
        return card;
    }

    /*
        Places a card from the hand, given by card_id in the mana_zone
     */
    public Card addMana(int hand_position) throws GameError {
        if (this.has_added_mana) {
            throw new GameError(NOT_ALLOWED, "Not allowed to add more than 1 mana each turn");
        }
        if (hand_position >= this.hand.size()) {
            throw new GameError(NOT_ENOUGH_CARDS, "You do not have that many cards in your hand");
        }
        this.has_added_mana = true;
        Card card = this.hand.get(hand_position).toBuilder().build();
        this.hand.remove(hand_position);
        this.manazone.add(card);
        return card;
    }

    public Card addToBattleZone(int hand_position) throws GameError {
        if (hand_position >= this.hand.size()) {
            throw new GameError(NOT_ENOUGH_CARDS, "You do not have that many cards in your hand");
        }
        Card card = this.hand.get(hand_position).toBuilder().build();
        if (card.is_spell()) {
            throw new GameError(WRONG_CARD_TYPE, "Can not add spell card to the battle zone");
        }
        if (card.getMana_cost() > getRemainingMana()) {
            throw new GameError(NOT_ENOUGH_MANA, "Not enough mana");
        }
        this.hand.remove(hand_position);
        this.tapMana(card.getMana_cost());
        // Speed attackers do not get summoning sickness
        if (!card.isSpeed_attacker()) {
            card.setSummoningSickness(true);
        }
        this.battlezone.add(card);
        return card;
    }

    private void tapMana(int cost) {
        int tapped = 0;
        for(int i=0; i < manazone.size(); i++) {
            Card card = manazone.get(i);
            if (!card.isTapped()) {
                card.setTapped(true);
                tapped += 1;
            }
            if (tapped == cost) {
                break;
            }
        }
    }

    private int getRemainingMana() {
        return (int) this.manazone.stream().filter(card -> !card.isTapped()).count();
    }

    public Card canAttackPlayer(int battleZonePosition) throws GameError {
        if (battleZonePosition >= this.battlezone.size()) {
            throw new GameError(NOT_ENOUGH_CARDS, "You do not have that many cards in the battle zone");
        }
        Card card = this.battlezone.get(battleZonePosition).toBuilder().build();
        if (!card.isCan_attack_player()) {
            throw new GameError(NOT_ALLOWED, "That card can not attack players");
        }
        if (card.isSummoningSickness()) {
            throw new GameError(SUMMONING_SICKNESS, "That card has summoning sickness");
        }
        if (card.isTapped()) {
            throw new GameError(ALREADY_TAPPED, "That card is already tapped");
        }
        return card;
    }

    // Removes shield(s) from this player and places the shield(s) in their hand
    // If the player has 0 shields, then the attacking player wins and this function returns empty list.
    // Otherwise the function returns the shields placed in the hand
    // It will destroy as many shields as possible up to numBreakShields.
    // If a double breaker attacks and there is only 1 shield left, then only this shield is destroyed.
    public ArrayList<Card> attackThisPlayer(Card attackingCreature) {
        ArrayList<Card> shields = new ArrayList<>();
        int numBreakShields = attackingCreature.getBreak_shields();
        for (int i=0; i < numBreakShields; i++) {
            if (this.shields.size() == 0) {
                break;
            }
            shields.add(removeOneShield());
        }
        return shields;
    }

    private Card removeOneShield() {
        int lastShield = this.shields.size() - 1;
        Card shield = this.shields.get(lastShield).toBuilder().build();
        this.shields.remove(lastShield);
        this.hand.add(shield);
        return shield;
    }

    public boolean has_blocker() {
        return this.battlezone.stream().filter(Card::isBlocker).findFirst().isPresent();
    }

    public Card getCardInHandPosition(int handPosition) throws GameError {
        if (this.hand.size() <= handPosition) {
            throw new GameError(NOT_ENOUGH_CARDS, "There are not that many cards in your hand.");
        }
        return this.hand.get(handPosition).toBuilder().build();
    }

    public Card getCardInBattleZonePosition(int battleZonePosition) throws GameError {
        if (this.battlezone.size() <= battleZonePosition) {
            throw new GameError(NOT_ENOUGH_CARDS, "There are not that many cards in the battle zone.");
        }
        return this.battlezone.get(battleZonePosition).toBuilder().build();
    }

    public void killCardInBattlezone(int battleZonePosition) throws GameError {
        Card card = getCardInBattleZonePosition(battleZonePosition);
        card.setTapped(false); // Card should be untapped when added to graveyard
        this.battlezone.remove(battleZonePosition);
        this.graveyard.add(card);
    }

    public void tapCreature(int battleZonePosition) {
        Card card = this.battlezone.get(battleZonePosition);
        card.setTapped(true);
    }

    // If you have a card with "must attack", and it may attack
    // (If it does not have summoning sickness, and is not tapped)
    // Then the player can not end the turn without attacking with this card
    public Optional<Card> hasUntappedMustAttackCreature() {
        return this.battlezone.stream()
                .filter(Card::isMust_attack)
                .filter(card -> !card.isSummoningSickness())
                .filter(card -> !card.isTapped())
                .findFirst();
    }

    public void returnCardFromBattlezoneToHand(int battleZonePosition) throws GameError {
        Card card = getCardInBattleZonePosition(battleZonePosition);
        card.setTapped(false); // Card should be untapped when added to hand
        this.battlezone.remove(battleZonePosition);
        this.hand.add(card);
    }

    public Card canAttackCreature(int battleZonePosition, Card attackedCreature) throws GameError {
        // Can only attack tapped creatures unless the attacking creature
        // has the spell effect "can attack untapped creatures"
        if (battleZonePosition >= this.battlezone.size()) {
            throw new GameError(NOT_ENOUGH_CARDS, "You do not have that many cards in the battle zone");
        }
        Card card = this.battlezone.get(battleZonePosition).toBuilder().build();
        if (!card.isCan_attack_creature()) {
            throw new GameError(NOT_ALLOWED, "That card can not attack players");
        }
        if (card.isSummoningSickness()) {
            throw new GameError(SUMMONING_SICKNESS, "That card has summoning sickness");
        }
        if (card.isTapped()) {
            throw new GameError(ALREADY_TAPPED, "That card is already tapped");
        }
        // check attackedCreature is tapped or attacker can attack untapped creature
        if (!attackedCreature.isTapped() && !card.canAttackUntappedCreature()) {
            throw new GameError(NOT_ALLOWED, "Can not attack untapped creatures");
        }
        return card;
    }

    public Card canUseSpellCard(int handPosition) throws GameError {
        if (handPosition >= this.hand.size()) {
            throw new GameError(NOT_ENOUGH_CARDS, "You do not have that many cards in your hand");
        }
        Card card = this.hand.get(handPosition).toBuilder().build();
        if (!card.is_spell()) {
            throw new GameError(WRONG_CARD_TYPE, "This is not a spell card");
        }
        if (card.getMana_cost() > getRemainingMana()) {
            throw new GameError(NOT_ENOUGH_MANA, "Not enough mana");
        }
        this.hand.remove(handPosition);
        this.tapMana(card.getMana_cost());
        return card;
    }

    public boolean hasCreatureRaceInBattlezone(String race) {
        String lowerCaseRace = race.toLowerCase().trim();
        for (Card card : this.battlezone) {
            if (card.getRace().equals(lowerCaseRace)) {
                return true;
            }
        }
        return false;
    }

    public void removeAllTempOnAttackEffects() {
        this.battlezone.forEach(Card::removeTempOnAttackEffects);
    }

    public static void main(String[] args) throws GameError {
        PlayerState newPlayer = new PlayerState(MainGameLoop.Player.PLAYER1, null, "TestPlayer1", 12);
        System.out.println("Deck size: " + newPlayer.deck.size());
        System.out.println("Hand size: " + newPlayer.hand.size());
        System.out.println("Shields size: " + newPlayer.shields.size());

//        System.out.println("Hand: ");
//        System.out.println(newPlayer.hand);

        System.out.println("Remaining mana: " + newPlayer.getRemainingMana());
        newPlayer.addMana(1);
        newPlayer.startNewTurn();
        newPlayer.addMana(1);
        newPlayer.startNewTurn();
        newPlayer.addMana(1);
        newPlayer.startNewTurn();
        newPlayer.addMana(1);
        newPlayer.startNewTurn();
        newPlayer.addMana(1);
        System.out.println("Remaining mana: " + newPlayer.getRemainingMana());
        newPlayer.addToBattleZone(2);
        System.out.println("Remaining mana: " + newPlayer.getRemainingMana());
        newPlayer.addToBattleZone(2);
        System.out.println("Remaining mana: " + newPlayer.getRemainingMana());


    }
}

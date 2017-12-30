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
        this.username = username;
        Optional<Deck> optDeck = DeckDatabase.get(deck_id);
        if (!optDeck.isPresent()) {
            throw new GameError(INIT_ERROR, "Deck with id: " + deck_id + " not found");
        }
        initialDeck = optDeck.get();
        if (!initialDeck.getUsername().equals(username) && !initialDeck.getUsername().equals("standard")) {
            throw new GameError(INIT_ERROR, "Deck does not belong to this user");
        }
        initPlayerState();
    }

    private void initPlayerState() {
        // Initialise deck
        this.deck = initialDeck.initialiseDeck();
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
        // draw new card
        return this.drawCard();
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
        this.has_added_mana = true;
        Card card = this.hand.get(hand_position).toBuilder().build();
        this.hand.remove(hand_position);
        this.manazone.add(card);
        return card;
    }

    public Card addToBattleZone(int hand_position) throws GameError {
        if (hand_position >= this.hand.size()) {
            throw new GameError(NOT_ENOUGH_CARDS, "You do not have that many cards");
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

    public Card useSpellCard(int hand_position) {
        // TODO: Implement this
        // Probably gonna involve some complex logic
        return null;
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

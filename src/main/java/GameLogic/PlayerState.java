package GameLogic;

import Database.DeckDatabase;
import Pojos.Card;
import Pojos.Deck;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

public class PlayerState {

    // Global game config
    private static final int STARTSHIELDS = 5;
    private static final int STARTCARDS = 5;

    MainGameLoop.Player player;
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

    public PlayerState(MainGameLoop.Player player, String username, int deck_id) throws GameError {
        this.player = player;
        this.username = username;
        Optional<Deck> optDeck = DeckDatabase.get(deck_id);
        if (!optDeck.isPresent()) {
            throw new GameError(GameError.ErrorCode.INIT_ERROR, "Deck with id: " + deck_id + " not found");
        }
        initialDeck = optDeck.get();
        if (!initialDeck.getUsername().equals(username)) {
            throw new GameError(GameError.ErrorCode.INIT_ERROR, "Deck does not belong to this user");
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
        Returns the card drawn
     */
    public Card drawCard() {
        Card card = getAndRemoveTopDeckCard();
        this.hand.add(card);
        return card;
    }

    public void addMana() {

    }

    public static void main(String[] args) throws GameError {
        PlayerState newPlayer = new PlayerState(MainGameLoop.Player.PLAYER1, "TestPlayer1", 12);
        System.out.println("Deck size: " + newPlayer.deck.size());
        System.out.println("Hand size: " + newPlayer.hand.size());
        System.out.println("Shields size: " + newPlayer.shields.size());

        System.out.println("Hand: ");
        System.out.println(newPlayer.hand);
    }
}

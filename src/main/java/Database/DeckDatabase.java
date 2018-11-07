package Database;

import Pojos.Card;
import Pojos.CardIdWithAmount;
import Pojos.CardWithAmount;
import Pojos.Deck;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static Database.DatabaseUtils.closeConnection;
import static Database.DatabaseUtils.getConnection;

public class DeckDatabase {

    // TODO: This whole thing should be one atomic operation
    /*
        Returns the id of the created deck
     */
    public static int create(Deck deck) {
        String sql = "INSERT INTO decks(deck_name, username) VALUES(?, ?)";
        Connection conn = DatabaseUtils.getConnection();
        PreparedStatement pstmt;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, deck.getName());
            pstmt.setString(2, deck.getUsername());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            // TODO: Should handle error somehow
            // Just throw SQL exceptions ?
            return -1;
        }
        int deck_id;
        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                deck_id = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        } catch (SQLException e) {
            // TODO: Should handle error somehow
            // Just throw SQL exceptions ?
            e.printStackTrace();
            return -1;
        }
        deck.getCardIds().forEach(cardIdWithAmount
                -> insertCard(deck_id, cardIdWithAmount.getId(), cardIdWithAmount.getAmount()));
        return deck_id;
    }

    private static void updateDeckName(Deck deck) {
        String sql = "UPDATE decks SET deck_name = ? WHERE deck_id = ?";
        Connection conn = getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, deck.getName());
            pstmt.setInt(2, deck.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Insert a new card to a deck
    private static void insertCard(int deck_id, int card_id, int amount) {
        String sql = "INSERT INTO deck_card(deck_id, card_id, amount) VALUES(?, ?, ?)";
        Connection conn = getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, deck_id);
            pstmt.setInt(2, card_id);
            pstmt.setInt(3, amount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void removeAllCardsFromDeck(int deck_id) {
        String sql = "DELETE FROM deck_card WHERE deck_id = ?";
        Connection conn = getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, deck_id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // What about errors when updating?
    // TODO: This should also be an atomic operation
    /*
        Replaces the whole deck with new cards
     */
    public static void update(Deck deck) {
        int deck_id = deck.getId();
        // update name of deck
        updateDeckName(deck);
        // delete all old cards from deck
        removeAllCardsFromDeck(deck_id);
        // add new cards to deck
        deck.getCardIds().forEach(cardIdWithAmount
                -> insertCard(deck_id, cardIdWithAmount.getId(), cardIdWithAmount.getAmount()));
    }

    private static List<CardIdWithAmount> getCardIdsWithAmount(int deck_id) {
        String sql = "SELECT * FROM deck_card WHERE deck_id = ?";
        ArrayList<CardIdWithAmount> cardIdsWithAmount = new ArrayList<>();
        Connection conn = getConnection();
        try {
            PreparedStatement pstmt  = conn.prepareStatement(sql);
            pstmt.setInt(1, deck_id);
            ResultSet rs  = pstmt.executeQuery();
            while (rs.next()) {
                CardIdWithAmount cardIdWithAmount = CardIdWithAmount.builder()
                        .id(rs.getInt("card_id"))
                        .amount(rs.getInt("amount"))
                        .build();
                cardIdsWithAmount.add(cardIdWithAmount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cardIdsWithAmount;
    }

    private static int getAmountOfCardById(List<CardIdWithAmount> cardIdsWithAmount, int id) {
        for (CardIdWithAmount card : cardIdsWithAmount) {
            if (card.getId() == id) {
                return card.getAmount();
            }
        }
        return 0;
    }

    private static List<CardWithAmount> mergeCardsAndAmount(List<CardIdWithAmount> cardIdsWithAmount, List<Card> cards) {
        List<CardWithAmount> cardsWithAmount = new ArrayList<>();
        cards.forEach(card -> {
            int amount = getAmountOfCardById(cardIdsWithAmount, card.getCard_id());
            // Currently only returning the cards with amount > 0
            if (amount > 0) {
                CardWithAmount cardWithAmount = new CardWithAmount(card, amount);
                cardsWithAmount.add(cardWithAmount);
            }
        });
        return cardsWithAmount;
    }

    public static List<Deck> getAllForUsername(String username) {
        String sql = "SELECT * FROM decks WHERE username = ?";
        ArrayList<Deck> decks = new ArrayList<>();
        Connection conn = getConnection();
        try {
            PreparedStatement pstmt  = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs  = pstmt.executeQuery();
            while (rs.next()) {
                int deckId = rs.getInt("deck_id");
                List<CardIdWithAmount> cardIdsWithAmount = getCardIdsWithAmount(deckId);
                List<Card> cards = CardDatabase.getByIds(cardIdsWithAmount);
                List<CardWithAmount> cardsWithAmount = mergeCardsAndAmount(cardIdsWithAmount, cards);
                Deck deck = buildDeckFromResultSet(rs, deckId, cardsWithAmount);
                decks.add(deck);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return decks;
    }

    public static Optional<Deck> get(int id) {
        String sql = "SELECT * FROM decks WHERE deck_id = ?";
        Connection conn = getConnection();
        try {
            PreparedStatement pstmt  = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs  = pstmt.executeQuery();
            if (rs.next()) {
                List<CardIdWithAmount> cardIdsWithAmount = getCardIdsWithAmount(id);
                List<Card> cards = CardDatabase.getByIds(cardIdsWithAmount);
                List<CardWithAmount> cardsWithAmount = mergeCardsAndAmount(cardIdsWithAmount, cards);
                Deck deck = buildDeckFromResultSet(rs, id, cardsWithAmount);
                return Optional.of(deck);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private static Deck buildDeckFromResultSet(ResultSet rs, int deckId, List<CardIdWithAmount> cardsWithAmount) throws SQLException {
        return Deck.builder()
                .id(deckId)
                .name(rs.getString("deck_name"))
                .username(rs.getString("username"))
                .cards(cardsWithAmount)
                .build();
    }
}

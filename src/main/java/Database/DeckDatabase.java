package Database;

import Pojos.Deck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DeckDatabase {

    public static void create(Deck deck) {
        String sql = "INSERT INTO decks(deck_name, username) VALUES(?, ?)";
        Connection conn = DatabaseUtils.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, deck.getName());
            pstmt.setString(2, deck.getUsername());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        deck.getCardIds().forEach(cardIdWithAmount
                -> insertCard(conn, deck.getId(), cardIdWithAmount.getId(), cardIdWithAmount.getAmount()));
        DatabaseUtils.closeConnection(conn);
    }

    // Insert a new card to a deck
    public static void insertCard(Connection conn, int deck_id, int card_id, int amount) {
        String sql = "INSERT INTO deck_card(deck_id, card_id, amount) VALUES(?, ?, ?)";
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

    public static boolean update(Deck deck) {
        // delete all old cards from deck
        // add new cards to deck
        return false;
    }

    public static List<Deck> get() {
        return null;
    }
}

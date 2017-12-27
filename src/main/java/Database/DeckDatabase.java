package Database;

import Pojos.Card;
import Pojos.Deck;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                -> insertCard(conn, deck_id, cardIdWithAmount.getId(), cardIdWithAmount.getAmount()));
        DatabaseUtils.closeConnection(conn);

        return deck_id;
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

    // TODO: This should also be an atomic operation
    public static boolean update(Deck deck) {
        // delete all old cards from deck
        // add new cards to deck
        return false;
    }

    public static Deck get() {
//        String sql = "SELECT * FROM cards";
//        ArrayList<Card> cards = new ArrayList<>();
//        Connection conn = getConnection();
//        try (
//                Statement stmt  = conn.createStatement();
//                ResultSet rs    = stmt.executeQuery(sql)){
//            while (rs.next()) {
//
//                boolean has_effects = rs.getBoolean("has_effects");
//
//                Card card = Card.builder()
//                        .card_id(rs.getInt("card_id"))
//                        .name(rs.getString("name"))
//                        .type(Card.Type.valueOf(rs.getString("type")))
//                        .is_spell(rs.getBoolean("is_spell_card"))
//                        .has_effects(has_effects)
//                        .mana_cost(rs.getInt("mana_cost"))
//                        .power(rs.getInt("power"))
//                        .power_attacker(rs.getInt("power_attacker"))
//                        .blocker(rs.getBoolean("blocker"))
//                        .speed_attacker(rs.getBoolean("speed_attacker"))
//                        .slayer(rs.getBoolean("slayer"))
//                        .shield_trigger(rs.getBoolean("shield_trigger"))
//                        .can_attack_player(rs.getBoolean("can_attack_player"))
//                        .can_attack_creature(rs.getBoolean("can_attack_creature"))
//                        .break_shields(rs.getInt("break_shields"))
//                        .must_attack(rs.getBoolean("must_attack"))
//                        .build();
//
//                if (has_effects) {
//                    System.out.println("Card has effects");
//                    // TODO: Figure out what to do then.
//                }
//
//                cards.add(card);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        closeConnection(conn);
//        return cards;
        return null;
    }
}

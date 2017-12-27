package Database;

import Pojos.Card;
import Pojos.CardIdWithAmount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static Database.DatabaseUtils.closeConnection;
import static Database.DatabaseUtils.getConnection;


public class CardDatabase {

    public static List<Card> getAll() {
        String sql = "SELECT * FROM cards";
        ArrayList<Card> cards = new ArrayList<>();
        Connection conn = getConnection();
        try {
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql);
            while (rs.next()) {
                boolean has_effects = rs.getBoolean("has_effects");
                Card card = Card.builder()
                        .card_id(rs.getInt("card_id"))
                        .name(rs.getString("name"))
                        .type(Card.Type.valueOf(rs.getString("type")))
                        .is_spell(rs.getBoolean("is_spell_card"))
                        .has_effects(has_effects)
                        .mana_cost(rs.getInt("mana_cost"))
                        .power(rs.getInt("power"))
                        .power_attacker(rs.getInt("power_attacker"))
                        .blocker(rs.getBoolean("blocker"))
                        .speed_attacker(rs.getBoolean("speed_attacker"))
                        .slayer(rs.getBoolean("slayer"))
                        .shield_trigger(rs.getBoolean("shield_trigger"))
                        .can_attack_player(rs.getBoolean("can_attack_player"))
                        .can_attack_creature(rs.getBoolean("can_attack_creature"))
                        .break_shields(rs.getInt("break_shields"))
                        .must_attack(rs.getBoolean("must_attack"))
                        .build();
                if (has_effects) {
                    System.out.println("Card has effects");
                    // TODO: Figure out what to do then.
                }
                cards.add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }

    public static Optional<Card> get(int id) {
        String sql = "SELECT * FROM cards WHERE card_id = ?";
        Connection conn = getConnection();
        try {
            PreparedStatement pstmt  = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs  = pstmt.executeQuery();
            if (rs.next()) {
                boolean has_effects = rs.getBoolean("has_effects");
                Card card = Card.builder()
                        .card_id(rs.getInt("card_id"))
                        .name(rs.getString("name"))
                        .type(Card.Type.valueOf(rs.getString("type")))
                        .is_spell(rs.getBoolean("is_spell_card"))
                        .has_effects(has_effects)
                        .mana_cost(rs.getInt("mana_cost"))
                        .power(rs.getInt("power"))
                        .power_attacker(rs.getInt("power_attacker"))
                        .blocker(rs.getBoolean("blocker"))
                        .speed_attacker(rs.getBoolean("speed_attacker"))
                        .slayer(rs.getBoolean("slayer"))
                        .shield_trigger(rs.getBoolean("shield_trigger"))
                        .can_attack_player(rs.getBoolean("can_attack_player"))
                        .can_attack_creature(rs.getBoolean("can_attack_creature"))
                        .break_shields(rs.getInt("break_shields"))
                        .must_attack(rs.getBoolean("must_attack"))
                        .build();
                if (has_effects) {
                    System.out.println("Card has effects");
                    // TODO: Figure out what to do then.
                }
                return Optional.of(card);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // These should probably be named
    private static String createListOfUnknownsForPreparedStatement(List anyList) {
        if (anyList.isEmpty()) {
            return "()";
        }
        String temp = "(";
        for (int i = 0; i < anyList.size()-1; i++) {
            temp += "?,";
        }
        temp += "?)";
        return temp;
    }

    // This should probably be generalized
    private static void setPreparedStatementListItems(int start, PreparedStatement pstmt, List<CardIdWithAmount> ints)
            throws SQLException {
        for (int i=0; i<ints.size(); i++) {
            pstmt.setInt(i+start, ints.get(i).getId());
        }
    }

    public static List<Card> getByIds(List <CardIdWithAmount> cardIdsWithAmount) {
        String sql = "SELECT * FROM cards WHERE card_id IN " + createListOfUnknownsForPreparedStatement(cardIdsWithAmount);
        ArrayList<Card> cards = new ArrayList<>();
        Connection conn = getConnection();
        try {
            PreparedStatement pstmt  = conn.prepareStatement(sql);
            setPreparedStatementListItems(1, pstmt, cardIdsWithAmount);
            ResultSet rs  = pstmt.executeQuery();
            while (rs.next()) {
                boolean has_effects = rs.getBoolean("has_effects");
                Card card = Card.builder()
                        .card_id(rs.getInt("card_id"))
                        .name(rs.getString("name"))
                        .type(Card.Type.valueOf(rs.getString("type")))
                        .is_spell(rs.getBoolean("is_spell_card"))
                        .has_effects(has_effects)
                        .mana_cost(rs.getInt("mana_cost"))
                        .power(rs.getInt("power"))
                        .power_attacker(rs.getInt("power_attacker"))
                        .blocker(rs.getBoolean("blocker"))
                        .speed_attacker(rs.getBoolean("speed_attacker"))
                        .slayer(rs.getBoolean("slayer"))
                        .shield_trigger(rs.getBoolean("shield_trigger"))
                        .can_attack_player(rs.getBoolean("can_attack_player"))
                        .can_attack_creature(rs.getBoolean("can_attack_creature"))
                        .break_shields(rs.getInt("break_shields"))
                        .must_attack(rs.getBoolean("must_attack"))
                        .build();
                if (has_effects) {
                    System.out.println("Card has effects");
                    // TODO: Figure out what to do then.
                }
                cards.add(card);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        List<Card> cards = CardDatabase.getAll();
//        cards.forEach(System.out::println);

//        Card card = get(1).get();
//        System.out.println(card);

        List<CardIdWithAmount> cardIdsWithAmount = new ArrayList<>();
        cardIdsWithAmount.add(new CardIdWithAmount(1, 1));
        cardIdsWithAmount.add(new CardIdWithAmount(2, 3));
        cardIdsWithAmount.add(new CardIdWithAmount(3, 2));
        List<Card> cards = CardDatabase.getByIds(cardIdsWithAmount);
        cards.forEach(System.out::println);

        closeConnection();
    }
}

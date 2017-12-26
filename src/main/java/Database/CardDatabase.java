package Database;

import Pojos.Card;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static Database.DatabaseUtils.closeConnection;
import static Database.DatabaseUtils.getConnection;


public class CardDatabase {

    public static List<Card> getAll() {
        String sql = "SELECT * FROM cards";
        ArrayList<Card> cards = new ArrayList<>();
        Connection conn = getConnection();
        try (
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
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
        closeConnection(conn);
        return cards;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        List<Card> cards = CardDatabase.getAll();
        cards.forEach(System.out::println);
    }
}

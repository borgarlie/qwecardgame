package Pojos;

import lombok.*;
import org.json.JSONObject;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
@Builder(toBuilder=true)
public class Card {

    public enum Type {
        fire, light, nature, darkness, water
    }

    int card_id;

    String name;
    Type type;

    boolean is_spell;
    boolean has_effects;

    // TODO: How to implement effects? Save a list of effects and register them on cards?
    // If we do this, we can check for effect ENUM and manually handle each effect
    // This also applies for spells, as all spells are compositions of 1 or more effects

    // should cards have "temporal effects" that get reset every new turn? or end of turn

    int mana_cost;
    int power;
    int power_attacker;
    int break_shields;

    boolean blocker;
    boolean speed_attacker;
    boolean slayer;
    boolean shield_trigger;
    boolean can_attack_player;
    boolean can_attack_creature;
    boolean must_attack;

    // All cards are untapped by default
    boolean isTapped = false;
    // When a card is summoned to the battle zone, it receives summoning sickness
    boolean summoningSickness = false;

    public int getTotalAttackingPower() {
        if (this.isTapped()) {
            // Tapped creatures do not "power attacker"
            // Could also implement this as only getting total on attacking creature and not the one getting attacked
            return this.power;
        }
        return this.power + this.power_attacker;
    }

    /*
        +1 = this player wins, -1 = this player looses, 0 is draw (both dies).
        if either card is a slayer and it looses, the other card will also die and return is 0.
     */
    public int fight(Card otherPlayersCard) {
        if (this.getTotalAttackingPower() > otherPlayersCard.getTotalAttackingPower()) {
            if (otherPlayersCard.isSlayer()) {
                return 0;
            }
            return 1;
        }
        else if (this.getTotalAttackingPower() < otherPlayersCard.getTotalAttackingPower()) {
            if (this.isSlayer()) {
                return 0;
            }
            return -1;
        }
        return 0;
    }

    public JSONObject toJson() {
        return new JSONObject(this);
    }

}

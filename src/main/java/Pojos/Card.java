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
        return this.power + this.power_attacker;
    }

    public JSONObject toJson() {
        return new JSONObject(this);
    }

}

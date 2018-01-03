package Pojos;

import lombok.*;
import org.json.JSONObject;

import java.util.List;
import java.util.Optional;

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

    // TODO: How to implement effects? Save a list of effects and register them on cards?
    // If we do this, we can check for effect ENUM and manually handle each effect
    // This also applies for spells, as all spells are compositions of 1 or more effects

    // Using NONE enums instead of actual "null" to remove the need for null checks.
    SpellEffect spellEffect;
    SummonCreatureEffect summonCreatureEffect;
    DestroyCreatureEffect destroyCreatureEffect;
    TempOnAttackEffect tempOnAttackEffect;

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

    // TODO: Use these in MainGameLoop and PlayerState
    boolean can_not_be_blocked;
    boolean can_attack_untapped_creatures;
    boolean untap_at_end;
    boolean destroy_on_win;

    // All cards are untapped by default
    boolean isTapped = false;
    // When a card is summoned to the battle zone, it receives summoning sickness
    boolean summoningSickness = false;

    // Temporal effects
    private int temp_power;
    private boolean temp_double_breaker;
    private boolean temp_can_attack_untapped_creatures;
    private boolean temp_can_not_be_blocked;
    private boolean temp_slayer_when_blocked;

    // TODO: Call this at end of turn
    public void removeTemporalEffects() {
        this.temp_power = 0;
        this.temp_double_breaker = false;
        this.temp_can_not_be_blocked = false;
        this.temp_can_attack_untapped_creatures = false;
        this.temp_slayer_when_blocked = false;
    }
    // TODO: Use these temp things
    // TODO: How to activate temp power and temp double breaker, etc?

    public int getTotalAttackingPower() {
        if (this.isTapped()) {
            // Tapped creatures do not "power attacker"
            // Could also implement this as only getting total on attacking creature and not the one getting attacked
            return this.power + this.temp_power;
        }
        return this.power + this.power_attacker + this.temp_power;
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
            if (this.isSlayer()) { // TODO: add slayer when blocked, but how do we know?
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

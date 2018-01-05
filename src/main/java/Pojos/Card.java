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
        FIRE, LIGHT, NATURE, DARKNESS, WATER
    }

    int card_id;
    String name;
    Type type;
    String race;
    boolean is_spell;

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

    boolean can_not_be_blocked;
    boolean can_attack_untapped_creatures;
    boolean untap_at_end;
    boolean destroy_on_win;

    // All cards are untapped by default
    boolean isTapped = false;
    // When a card is summoned to the battle zone, it receives summoning sickness
    boolean summoningSickness = false;

    // Temporal effects
    private int temp_power = 0;
    private boolean temp_double_breaker = false; // TODO Use
    private boolean temp_can_attack_untapped_creatures = false;
    private boolean temp_can_not_be_blocked = false;
    private boolean temp_slayer_when_blocked = false;

    public void removeTemporalEffects() {
        this.temp_power = 0;
        this.temp_double_breaker = false;
        this.temp_can_not_be_blocked = false;
        this.temp_can_attack_untapped_creatures = false;
        this.temp_slayer_when_blocked = false;
    }

    private int temp_on_attack_power = 0;
    private int temp_on_attack_power_attacker = 0;
    private boolean temp_on_attack_can_not_be_blocked = false; // TODO: How to use this?

    public void removeTempOnAttackEffects() {
        this.temp_on_attack_power = 0;
        this.temp_on_attack_power_attacker = 0;
        this.temp_on_attack_can_not_be_blocked = false;
    }

    public int getTotalAttackingPower(boolean isAttacking) {
        int power = this.power + this.temp_power + this.temp_on_attack_power;
        if (isAttacking) {
            return power + this.power_attacker + this.temp_on_attack_power_attacker;
        }
        return power;
    }

    public boolean canBeBlocked() {
        return !this.can_not_be_blocked && !this.temp_can_not_be_blocked;
    }

    public boolean canAttackUntappedCreature() {
        return this.can_attack_untapped_creatures || this.temp_can_attack_untapped_creatures;
    }

    /*
        +1 = this player wins, -1 = this player looses, 0 is draw (both dies).
        if either card is a slayer and it looses, the other card will also die and return is 0.
     */
    public int fight(Card otherPlayersCard, boolean isBlocking) {
        // The attacker always get power attacker. Blockers do not get it
        int thisCardTotalPower = this.getTotalAttackingPower(true);
        int otherCardTotalPower = otherPlayersCard.getTotalAttackingPower(false);
        if (isBlocking) {
            thisCardTotalPower = this.getTotalAttackingPower(false);
            // The other card only gets power attacker if this card is blocking, which means the other card is attacking
            otherCardTotalPower = otherPlayersCard.getTotalAttackingPower(true);
        }
        if (thisCardTotalPower > otherCardTotalPower) {
            if (otherPlayersCard.isSlayer() || (otherPlayersCard.isTemp_slayer_when_blocked() && isBlocking)) {
                return 0;
            }
            if (this.destroy_on_win) {
                return 0;
            }
            return 1;
        }
        else if (thisCardTotalPower < otherCardTotalPower) {
            if (this.isSlayer()) {
                return 0;
            }
            if (otherPlayersCard.destroy_on_win) {
                return 0;
            }
            return -1;
        }
        return 0;
    }

    public int fight(Card otherPlayersCard) {
        return fight(otherPlayersCard, false);
    }

    public JSONObject toJson() {
        return new JSONObject(this);
    }

}

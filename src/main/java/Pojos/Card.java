package Pojos;

import lombok.*;

@Getter
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

}

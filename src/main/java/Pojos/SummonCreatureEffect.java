package Pojos;


public enum SummonCreatureEffect {

    NONE ("This card has no summoning effect"),
    RETURN_TWO_CARDS_TO_HAND ("When you put this creature into the battle zone, " +
            "choose up to 2 creatures in the battle zone and return them to their owners' hands."),
    DESTROY_ALL_BLOCKERS ("When you put this creature into the battle zone, " +
            "destroy all creatures that have 'blocker'"),
    TAP_ONE_CREATURE ("When you put this creature into the battle zone, " +
            "you may choose 1 of your opponent’s creatures in the battle zone and tap it."),
    DRAW_CARD_EFFECT ("When you put this creature into the battle zone, you may draw a card.");


    private final String description;

    SummonCreatureEffect(String description) {
        this.description = description;
    }

    public static SummonCreatureEffect valueOfOrNone(String value) {
        if (value == null || value.isEmpty()) {
            return NONE;
        }
        return SummonCreatureEffect.valueOf(value);
    }

}

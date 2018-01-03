package Pojos;


public enum SummonCreatureEffect {

    NONE ("This card has no summoning effect"),
    RETURN_TWO_OPPONENT_CARDS_TO_HAND ("When you put this creature into the battle zone, " +
            "choose up to 2 creatures in the battle zone and return them to their owners’ hands."),
    TEST2 ("TESTTEST 2");

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

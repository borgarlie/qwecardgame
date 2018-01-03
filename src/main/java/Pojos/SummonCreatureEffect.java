package Pojos;


public enum SummonCreatureEffect {

    NONE ("This card has no summoning effect"),
    TEST1 ("THIS IS A TEST"),
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
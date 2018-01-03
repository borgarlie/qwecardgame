package Pojos;

public enum DestroyCreatureEffect {
    NONE ("This card has no destroy effect"),
    TEST1 ("THIS IS A TEST"),
    TEST2 ("TESTTEST 2");

    private final String description;

    DestroyCreatureEffect(String description) {
        this.description = description;
    }

    public static DestroyCreatureEffect valueOfOrNone(String value) {
        if (value == null || value.isEmpty()) {
            return NONE;
        }
        return DestroyCreatureEffect.valueOf(value);
    }
}

package Pojos;

public enum TempOnAttackEffect {
    NONE ("This card has no temporal on attack effect"),
    TEST1 ("THIS IS A TEST"),
    TEST2 ("TESTTEST 2");

    private final String description;

    TempOnAttackEffect(String description) {
        this.description = description;
    }

    public static TempOnAttackEffect valueOfOrNone(String value) {
        if (value == null || value.isEmpty()) {
            return NONE;
        }
        return TempOnAttackEffect.valueOf(value);
    }
}
package Pojos;

public enum TempOnAttackEffect {
    NONE ("This card has no temporal on attack effect"),
    PLUS_POWER_ANGEL_COMMAND ("While you have at least 1 Angel Command in the battle zone, " +
            "this creature gets +2000 power.");

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

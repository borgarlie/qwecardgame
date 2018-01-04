package Pojos;

public enum DestroyCreatureEffect {
    NONE ("This card has no destroy effect"),
    RETURN_TO_HAND ("When this creature would be destroyed, return it to your hand instead.");

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

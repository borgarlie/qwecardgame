package Pojos;

public enum SpellEffect {

    NONE ("This card has no spell effect"),
    TAP_ALL_OPPONENT_CREATURES ("Tap all your opponent’s creatures in the battle zone.");

    private final String description;

    SpellEffect(String description) {
        this.description = description;
    }

    public SpellEffect valueOfOrNone(String value) {
        if (value == null || value.isEmpty()) {
            return NONE;
        }
        return SpellEffect.valueOf(value);
    }

}

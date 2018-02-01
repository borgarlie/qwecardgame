package Pojos;

public enum SpellEffect {

    NONE ("This card has no spell effect"),
    TAP_ALL_OPPONENT_CREATURES ("Tap all your opponent’s creatures in the battle zone."),
    TEMP_CANT_BLOCK_X2 ("Choose up to 2 of your creatures in the battle zone. They can’t be blocked this turn."),
    TAP_TWO_OPPONENT_CREATURES ("Choose up to 2 of your opponent’s creatures in the battle zone and tap them."),
    TAP_ONE_OPPONENT_CREATURE ("Choose one of your opponent's creatures in the battle zone and tap it.");

    private final String description;

    SpellEffect(String description) {
        this.description = description;
    }

    public static SpellEffect valueOfOrNone(String value) {
        if (value == null || value.isEmpty()) {
            return NONE;
        }
        return SpellEffect.valueOf(value);
    }

}

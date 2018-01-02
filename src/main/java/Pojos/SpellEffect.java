package Pojos;

public enum SpellEffect {

    NONE ("This card has no spell effect"),
    TEST1 ("THIS IS A TEST"),
    TEST2 ("TESTTEST 2");

    private final String description;

    SpellEffect(String description) {
        this.description = description;
    }

}

DROP TABLE IF EXISTS cards;
DROP TABLE IF EXISTS decks;
DROP TABLE IF EXISTS deck_card;
DROP TABLE IF EXISTS user;

CREATE TABLE cards (
    card_id integer PRIMARY KEY,
    name text NOT NULL UNIQUE,
    type text NOT NULL,
    race text NOT NULL,
    is_spell_card integer NOT NULL,
    mana_cost integer NOT NULL,
    power integer NOT NULL,
    power_attacker integer,
    blocker integer NOT NULL,
    speed_attacker integer NOT NULL,
    slayer integer NOT NULL,
    shield_trigger integer NOT NULL,
    can_attack_player integer NOT NULL,
    can_attack_creature integer NOT NULL,
    break_shields integer NOT NULL,
    must_attack integer NOT NULL,
    can_not_be_blocked integer NOT NULL,
    can_attack_untapped_creatures integer NOT NULL,
    untap_at_end integer NOT NULL,
    destroy_on_win integer NOT NULL,
    spell_effect text NOT NULL,
    summon_creature_effect text NOT NULL,
    destroy_creature_effect text NOT NULL,
    temp_on_attack_effect text NOT NULL
);

CREATE TABLE decks (
    deck_id integer PRIMARY KEY,
    deck_name text NOT NULL,
    user_id text NOT NULL,
    UNIQUE (deck_name, user_id)
);

CREATE TABLE deck_card (
    deck_id integer NOT NULL,
    card_id integer NOT NULL,
    amount integer NOT NULL,
    FOREIGN KEY (deck_id) REFERENCES decks(deck_id),
    FOREIGN KEY (card_id) REFERENCES cards(card_id),
    PRIMARY KEY (deck_id, card_id)
);

CREATE TABLE users (
    user_id integer PRIMARY KEY,
    email text NOT NULL,
    name text NOT NULL,
    username text NOT NULL UNIQUE,
    role text NOT NULL
);

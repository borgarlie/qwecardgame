DROP TABLE IF EXISTS cards;
DROP TABLE IF EXISTS decks;
DROP TABLE IF EXISTS deck_card;

CREATE TABLE cards (
    card_id integer PRIMARY KEY,
    name text NOT NULL UNIQUE,
    type text NOT NULL,
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
    username text NOT NULL,
    UNIQUE (deck_name, username)
);

CREATE TABLE deck_card (
    deck_id integer NOT NULL,
    card_id integer NOT NULL,
    amount integer NOT NULL,
    FOREIGN KEY (deck_id) REFERENCES decks(deck_id),
    FOREIGN KEY (card_id) REFERENCES cards(card_id),
    PRIMARY KEY (deck_id, card_id)
);

INSERT INTO cards
    (name, type, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Immortal baron, Vorg', "fire", 0,
    2, 2000, 0, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

INSERT INTO cards
    (name, type, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Brawler Zyler', "fire", 0,
    2, 1000, 2000, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

INSERT INTO cards
    (name, type, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('La Ura Giga', "light", 0,
    1, 2000, 0, 1,
    0, 0, 0, 0,
    1, 0, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

INSERT INTO cards
    (name, type, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Stonesaur', "fire", 0,
    5, 4000, 2000, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

INSERT INTO cards
    (name, type, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Hanusa, Radiance Elemental', "light", 0,
    7, 9500, 0, 0,
    0, 0, 0, 1,
    1, 2, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

INSERT INTO cards
    (name, type, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Deadly Fighter Braid Claw', "fire", 0,
    1, 1000, 0, 0,
    0, 0, 0, 1,
    1, 0, 1,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

INSERT INTO cards
    (name, type, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Phantom Fish', "water", 0,
    3, 4000, 0, 1,
    0, 0, 0, 0,
    0, 0, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

INSERT INTO decks
    (deck_name, username)
    VALUES
    ('Standard 1', "standard");

INSERT INTO decks
    (deck_name, username)
    VALUES
    ('Standard 2', "standard");

INSERT INTO deck_card
    (deck_id, card_id, amount)
    VALUES
    (1, 1, 4);

INSERT INTO deck_card
    (deck_id, card_id, amount)
    VALUES
    (1, 2, 4);

INSERT INTO deck_card
    (deck_id, card_id, amount)
    VALUES
    (1, 3, 4);

INSERT INTO deck_card
    (deck_id, card_id, amount)
    VALUES
    (1, 4, 4);

INSERT INTO deck_card
    (deck_id, card_id, amount)
    VALUES
    (1, 5, 4);

INSERT INTO deck_card
    (deck_id, card_id, amount)
    VALUES
    (1, 6, 4);

INSERT INTO deck_card
    (deck_id, card_id, amount)
    VALUES
    (2, 1, 4);

INSERT INTO deck_card
    (deck_id, card_id, amount)
    VALUES
    (2, 2, 4);

INSERT INTO deck_card
    (deck_id, card_id, amount)
    VALUES
    (2, 3, 4);

INSERT INTO deck_card
    (deck_id, card_id, amount)
    VALUES
    (2, 4, 4);

INSERT INTO deck_card
    (deck_id, card_id, amount)
    VALUES
    (2, 5, 4);

INSERT INTO deck_card
    (deck_id, card_id, amount)
    VALUES
    (2, 7, 4);

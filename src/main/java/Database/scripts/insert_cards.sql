
-- DM-01 Base set

--card id =
INSERT INTO cards
    (name, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Immortal baron, Vorg', "FIRE", "Human", 0,
    2, 2000, 0, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

INSERT INTO cards
    (name, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Brawler Zyler', "FIRE", "Human", 0,
    2, 1000, 2000, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

INSERT INTO cards
    (name, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('La Ura Giga', "LIGHT", "Guardian", 0,
    1, 2000, 0, 1,
    0, 0, 0, 0,
    1, 0, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

INSERT INTO cards
    (name, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Stonesaur', "FIRE", "Rock Beast", 0,
    5, 4000, 2000, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

INSERT INTO cards
    (name, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Deadly Fighter Braid Claw', "FIRE", "	Dragonoid", 0,
    1, 1000, 0, 0,
    0, 0, 0, 1,
    1, 0, 1,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

INSERT INTO cards
    (name, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Phantom Fish', "WATER", "Gel Fish", 0,
    3, 4000, 0, 1,
    0, 0, 0, 0,
    0, 0, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');


-- Special cards

-- S1 / S10
INSERT INTO cards
    (name, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Hanusa, Radiance Elemental', "LIGHT", "Angel Command", 0,
    7, 9500, 0, 0,
    0, 0, 0, 1,
    1, 2, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- S2 / S10
INSERT INTO cards
    (name, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Urth, Purifying Elemental', "LIGHT", "Angel Command", 0,
    6, 6000, 0, 0,
    0, 0, 0, 1,
    1, 2, 0,
    0, 0, 1, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- S3 / S10
INSERT INTO cards
    (name, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Aqua Sniper', "WATER", "Liquid People", 0,
    8, 5000, 0, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'RETURN_TWO_OPPONENT_CARDS_TO_HAND', 'NONE', 'NONE');

-- S4 / S10
INSERT INTO cards
    (name, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('King Depthcon', "WATER", "Leviathan", 0,
    7, 6000, 0, 0,
    0, 0, 0, 1,
    1, 2, 0,
    1, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- S5 / S10
INSERT INTO cards
    (name, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Deathliger, Lion of Chaos', "DARKNESS", "Demon Command", 0,
    7, 9000, 0, 0,
    0, 0, 0, 1,
    1, 2, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- S6 / S10
INSERT INTO cards
    (name, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Zagaan, Knight of Darkness', "DARKNESS", "Demon Command", 0,
    6, 7000, 0, 0,
    0, 0, 0, 1,
    1, 2, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- S7 / S10
INSERT INTO cards
    (name, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Astrocomet Dragon', "FIRE", "Armored Dragon", 0,
    7, 6000, 4000, 0,
    0, 0, 0, 1,
    1, 2, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- S8 / S10
INSERT INTO cards
    (name, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Scarlet Skyterror', "FIRE", "Armored Wyvern", 0,
    8, 3000, 0, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'DESTROY_ALL_BLOCKERS', 'NONE', 'NONE');

-- S9 / S10
INSERT INTO cards
    (name, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Deathblade Beetle', "NATURE", "Giant Insect", 0,
    5, 3000, 4000, 0,
    0, 0, 0, 1,
    1, 2, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- S10 / S10
INSERT INTO cards
    (name, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Roaring Great-Horn', "NATURE", "Horned Beast", 0,
    7, 8000, 2000, 0,
    0, 0, 0, 1,
    1, 2, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');


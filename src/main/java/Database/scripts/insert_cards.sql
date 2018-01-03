
-- DM-01 Base set

--card id =
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


-- Special cards

-- S1 / S10
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

-- S2 / S10
INSERT INTO cards
    (name, type, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Urth, Purifying Elemental', "light", 0,
    6, 6000, 0, 0,
    0, 0, 0, 1,
    1, 2, 0,
    0, 0, 1, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- S3 / S10
INSERT INTO cards
    (name, type, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Aqua Sniper', "water", 0,
    8, 5000, 0, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'RETURN_TWO_OPPONENT_CARDS_TO_HAND', 'NONE', 'NONE');

-- S4 / S10
INSERT INTO cards
    (name, type, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('King Depthcon', "water", 0,
    7, 6000, 0, 0,
    0, 0, 0, 1,
    1, 2, 0,
    1, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

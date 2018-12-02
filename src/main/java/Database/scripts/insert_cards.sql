
-- DM-01 Base set

-- 1 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Chilias, the Oracle', 'ChiliastheOracle.jpg', 'LIGHT', 'Light Bringer', 0,
    4, 2500, 0, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'RETURN_TO_HAND', 'NONE');

-- 2 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Dia Nork, Moonlight Guardian', 'Dianorkmoonlightguardian.jpg', 'LIGHT', 'Guardian', 0,
    4, 5000, 0, 1,
    0, 0, 0, 0,
    1, 0, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- 3 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Emerald Grass', 'EmeraldGrass.jpg', 'LIGHT', 'Starlight Tree', 0,
    2, 3000, 0, 1,
    0, 0, 0, 0,
    1, 0, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- 4 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Frei, Vizier of Air', 'Freivizierofair.jpg', 'LIGHT', 'Initiate', 0,
    4, 3000, 0, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 1, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- 5 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Gran Gure, Space Guardian', 'Grangurespaceguardian.jpg', 'LIGHT', 'Guardian', 0,
    6, 9000, 0, 1,
    0, 0, 0, 0,
    1, 0, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');


-- 6 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Holy Awe', 'HolyAwe.jpg', 'LIGHT', 'NONE', 1,
    6, 0, 0, 0,
    0, 0, 1, 0,
    0, 0, 0,
    0, 0, 0, 0,
    'TAP_ALL_OPPONENT_CREATURES', 'NONE', 'NONE', 'NONE');

-- 7 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Iere, Vizier of Bullets', 'Ierevizierofbullets.jpg', 'LIGHT', 'Initiate', 0,
    3, 3000, 0, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- 8 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Iocant, the Oracle', 'IocantTheOracle.jpg', 'LIGHT', 'Light Bringer', 0,
    2, 2000, 0, 1,
    0, 0, 0, 0,
    1, 0, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'PLUS_POWER_ANGEL_COMMAND');

-- 9 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('La Ura Giga', 'La_Ura_Giga,_Sky_Guardian.jpg', 'LIGHT', 'Guardian', 0,
    1, 2000, 0, 1,
    0, 0, 0, 0,
    1, 0, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- 10 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Lah, Purification Enforcer', 'LahPurificationEnforcer.jpg', 'LIGHT', 'Berserker', 0,
    5, 5500, 0, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- 11 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Laser Wing', 'LaserWing.jpg', 'LIGHT', 'NONE', 1,
    5, 0, 0, 0,
    0, 0, 0, 0,
    0, 0, 0,
    0, 0, 0, 0,
    'TEMP_CANT_BLOCK_X2', 'NONE', 'NONE', 'NONE');

-- 12 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Lok, Vizier of Hunting', 'Lokvizierofhunting.jpg', 'LIGHT', 'Initiate', 0,
    4, 4000, 0, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- 13 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Miele, Vizier of Lightning', 'MieleVizierofLightning.jpg', 'LIGHT', 'Initiate', 0,
    3, 1000, 0, 0,
    0, 0, 0, 0,
    1, 0, 0,
    0, 0, 0, 0,
    'NONE', 'TAP_ONE_CREATURE', 'NONE', 'NONE');

-- 14 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Moonlight Flash', 'MoonlightFlash.jpg', 'LIGHT', 'NONE', 1,
    4, 0, 0, 0,
    0, 0, 0, 0,
    0, 0, 0,
    0, 0, 0, 0,
    'TAP_TWO_OPPONENT_CREATURES', 'NONE', 'NONE', 'NONE');

-- 15 / 110. Skipped. search your deck effect.
--INSERT INTO cards
--    (name, image_file, type, race, is_spell_card,
--    mana_cost, power, power_attacker, blocker,
--    speed_attacker, slayer, shield_trigger, can_attack_player,
--    can_attack_creature, break_shields, must_attack,
--    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
--    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
--    VALUES
--    ('Rayla, Truth Enforcer', 'unknown.jpg', 'LIGHT', 'Berserker', 0,
--    6, 3000, 0, 0,
--    0, 0, 0, 1,
--    1, 1, 0,
--    0, 0, 0, 0,
--    'Something..', 'NONE', 'NONE', 'NONE');

-- 16 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Reusol, the Oracle', 'ReusoltheOracle.jpg', 'LIGHT', 'Light Bringer', 0,
    2, 2000, 0, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- 17 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Ruby Grass', 'RubyGrass.jpg', 'LIGHT', 'Starlight Tree', 0,
    3, 3000, 0, 1,
    0, 0, 0, 0,
    1, 0, 0,
    0, 0, 1, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- 18 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Senatine Jade Tree', 'SenatineJadeTree.jpg', 'LIGHT', 'Starlight Tree', 0,
    3, 4000, 0, 1,
    0, 0, 0, 0,
    1, 0, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- 19 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Solar Ray', 'SolarRay(DM10).jpg', 'LIGHT', 'NONE', 1,
    2, 0, 0, 0,
    0, 0, 1, 0,
    0, 0, 0,
    0, 0, 0, 0,
    'TAP_ONE_OPPONENT_CREATURE', 'NONE', 'NONE', 'NONE');

-- 20 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Sonic Wing', 'SonicWing.jpg', 'LIGHT', 'NONE', 1,
    3, 0, 0, 0,
    0, 0, 0, 0,
    0, 0, 0,
    0, 0, 0, 0,
    'TEMP_CANT_BLOCK', 'NONE', 'NONE', 'NONE');

-- 21 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Szubs Kin, Twilight Guardian', 'SzubsKinTwilightGuardian.jpg', 'LIGHT', 'Guardian', 0,
    5, 6000, 0, 1,
    0, 0, 0, 0,
    1, 0, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- 22 / 110
-- Toel, Vizier of Hope
-- Skipped. End of turn effect that propagates on other cards.

-- 23 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Aqua Hulcus', 'AquaHulcus.jpg', 'WATER', 'Liquid People', 0,
    3, 2000, 0, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'DRAW_CARD_EFFECT', 'NONE', 'NONE');

-- 24 / 110
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Aqua Knight, Twilight Guardian', 'AquaKnight.jpg', 'WATER', 'Liquid People', 0,
    5, 4000, 0, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'RETURN_TO_HAND', 'NONE');

-- Immortal baron, Vorg
-- Brawler zyler
-- stonesaur
-- phantom fish

--card id =
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Immortal baron, Vorg', 'unknown.jpg', 'FIRE', 'Human', 0,
    2, 2000, 0, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Brawler Zyler', 'unknown.jpg', 'FIRE', 'Human', 0,
    2, 1000, 2000, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Stonesaur', 'unknown.jpg', 'FIRE', 'Rock Beast', 0,
    5, 4000, 2000, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Deadly Fighter Braid Claw', 'unknown.jpg', 'FIRE', 'Dragonoid', 0,
    1, 1000, 0, 0,
    0, 0, 0, 1,
    1, 0, 1,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Phantom Fish', 'unknown.jpg', 'WATER', 'Gel Fish', 0,
    3, 4000, 0, 1,
    0, 0, 0, 0,
    0, 0, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');


-- Special cards

-- S1 / S10
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Hanusa, Radiance Elemental', 'HanusaRadianceElemental.jpg', 'LIGHT', 'Angel Command', 0,
    7, 9500, 0, 0,
    0, 0, 0, 1,
    1, 2, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- S2 / S10
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Urth, Purifying Elemental', 'UrthPurifyingElemental.jpg', 'LIGHT', 'Angel Command', 0,
    6, 6000, 0, 0,
    0, 0, 0, 1,
    1, 2, 0,
    0, 0, 1, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- S3 / S10
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Aqua Sniper', 'AquaSniper.jpg', 'WATER', 'Liquid People', 0,
    8, 5000, 0, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'RETURN_TWO_CARDS_TO_HAND', 'NONE', 'NONE');

-- S4 / S10
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('King Depthcon', 'KingDepthcon.jpg', 'WATER', 'Leviathan', 0,
    7, 6000, 0, 0,
    0, 0, 0, 1,
    1, 2, 0,
    1, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- S5 / S10
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Deathliger, Lion of Chaos', 'DeathligerLionofChaos.jpg', 'DARKNESS', 'Demon Command', 0,
    7, 9000, 0, 0,
    0, 0, 0, 1,
    1, 2, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- S6 / S10
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Zagaan, Knight of Darkness', 'ZagaanKnightofDarkness.jpg', 'DARKNESS', 'Demon Command', 0,
    6, 7000, 0, 0,
    0, 0, 0, 1,
    1, 2, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- S7 / S10
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Astrocomet Dragon', 'AstrocometDragon.jpg', 'FIRE', 'Armored Dragon', 0,
    7, 6000, 4000, 0,
    0, 0, 0, 1,
    1, 2, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- S8 / S10
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Scarlet Skyterror', 'ScarletSkyterror.jpg', 'FIRE', 'Armored Wyvern', 0,
    8, 3000, 0, 0,
    0, 0, 0, 1,
    1, 1, 0,
    0, 0, 0, 0,
    'NONE', 'DESTROY_ALL_BLOCKERS', 'NONE', 'NONE');

-- S9 / S10
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Deathblade Beetle', 'DeathbladeBeetle.jpg', 'NATURE', 'Giant Insect', 0,
    5, 3000, 4000, 0,
    0, 0, 0, 1,
    1, 2, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

-- S10 / S10
INSERT INTO cards
    (name, image_file, type, race, is_spell_card,
    mana_cost, power, power_attacker, blocker,
    speed_attacker, slayer, shield_trigger, can_attack_player,
    can_attack_creature, break_shields, must_attack,
    can_not_be_blocked, can_attack_untapped_creatures, untap_at_end, destroy_on_win,
    spell_effect, summon_creature_effect, destroy_creature_effect, temp_on_attack_effect)
    VALUES
    ('Roaring Great-Horn', 'RoaringGreatHorn.jpg', 'NATURE', 'Horned Beast', 0,
    7, 8000, 2000, 0,
    0, 0, 0, 1,
    1, 2, 0,
    0, 0, 0, 0,
    'NONE', 'NONE', 'NONE', 'NONE');

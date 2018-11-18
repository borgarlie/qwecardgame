
-- Insert no-user user

INSERT INTO users
    (user_id, email, name, username, role)
    VALUES
    ('1234567890qwerty', '1234567890qwerty@no-email.io', 'No User', 'no-user', 'ADMIN');

-- Standard deck 1

INSERT INTO decks
    (deck_name, user_id)
    VALUES
    ('Standard 1', '1234567890qwerty');

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

-- Standard deck 2

INSERT INTO decks
    (deck_name, user_id)
    VALUES
    ('Standard 2', '1234567890qwerty');

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

-- Standard deck 3 - spells only

INSERT INTO decks
    (deck_name, user_id)
    VALUES
    ('Standard 3 - spells only', '1234567890qwerty');

INSERT INTO deck_card
    (deck_id, card_id, amount)
    VALUES
    (3, 6, 10);

INSERT INTO deck_card
    (deck_id, card_id, amount)
    VALUES
    (3, 11, 10);

INSERT INTO deck_card
    (deck_id, card_id, amount)
    VALUES
    (3, 14, 10);

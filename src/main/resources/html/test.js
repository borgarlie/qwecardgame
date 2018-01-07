
// Set up the websocket
var socket = new WebSocket("ws://localhost:1337/game");
console.log(socket);

// Main send function (test)
function sendText() {
    // Construct a msg object containing the data the server needs to process the message from the chat client.
    var msg = {
        in_game: false,
        type: "test"
    };
    // Send the msg object as a JSON-formatted string.
    socket.send(JSON.stringify(msg));
}

// Send end turn
function sendEndTurn() {
    var msg = {
        in_game: true,
        type: "end_turn"
    };
    socket.send(JSON.stringify(msg));
}

// Send a request to play a game to a user
function sendRequest(username, deck_id) {
    var msg = {
        in_game: false,
        type: "play_request",
        username: username,
        deck_id: deck_id
    };
    socket.send(JSON.stringify(msg));
}

// Accept or deny a request
function sendAcceptRequest(username, accept, deck_id) {
    var msg = {
        in_game: false,
        type: "accept_request",
        accept_request: accept,
        username: username,
        deck_id: deck_id
    };
    socket.send(JSON.stringify(msg));
}

var gameState = {
    graveyard_cards: 0,
    deck_cards: 40,
    battle_zone_cards: 0,
    shield_cards: 0,
    mana_zone_cards: 0,
    hand_cards: 0
};

// main receive function
socket.onmessage = function(event) {
    var msg = JSON.parse(event.data);

    console.log(msg);

    switch(msg.type) {
        case "game_start":
            console.log("Game start");
            var start_hand_array = msg.start_hand;
            size = start_hand_array.length;
            gameState.hand_cards += size;
            gameState.deck_cards -= size;
            // TODO: Should probably use angular or something for binding variables
            var deck_element = document.getElementById("deck_cards");
            deck_element.innerHTML = gameState.deck_cards;
            var hand_element = document.getElementById("hand_cards");
            hand_element.innerHTML = gameState.hand_cards;
            break;
        default:
            console.log("Default");
            break;
    }
};

function sendPlaceMana(hand_position) {
    var msg = {
        in_game: true,
        type: "place_mana",
        hand_position: hand_position
    };
    socket.send(JSON.stringify(msg));
}

function sendAddToBattleZone(hand_position, opponent_cards, own_cards) {
    var msg = {
        in_game: true,
        type: "add_to_battlezone",
        hand_position: hand_position,
        use_on_opponent_cards: opponent_cards,
        use_on_own_cards: own_cards
    };
    socket.send(JSON.stringify(msg));
}

function sendAttackCreature(battle_zone_position, attack_creature_in_position) {
    var msg = {
        in_game: true,
        type: "attack_creature",
        battle_zone_position: battle_zone_position,
        attack_creature_in_position, attack_creature_in_position
    };
    socket.send(JSON.stringify(msg));
}

function sendAttackPlayer(battle_zone_position) {
    var msg = {
        in_game: true,
        type: "attack_player",
        battle_zone_position: battle_zone_position
    };
    socket.send(JSON.stringify(msg));
}

// -1 = do not block
function sendUseBlocker(battle_zone_position) {
    var msg = {
        in_game: true,
        type: "use_blocker",
        battle_zone_position: battle_zone_position
    };
    socket.send(JSON.stringify(msg));
}

function sendShieldTriggerResponse(use_shield_trigger) {
    var msg = {
        in_game: true,
        type: "use_shield_trigger",
        use_shield_trigger: use_shield_trigger
    };
    socket.send(JSON.stringify(msg));
}

function sendUseSpellCard(hand_position, opponent_cards, own_cards) {
    var msg = {
        in_game: true,
        type: "use_spell",
        hand_position: hand_position,
        use_on_opponent_cards: opponent_cards,
        use_on_own_cards: own_cards
    };
    socket.send(JSON.stringify(msg));
}

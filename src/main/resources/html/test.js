
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

// Main send function 2 (test)
// Does not work until game is set up
function sendText2() {
    // Construct a msg object containing the data the server needs to process the message from the chat client.
    var msg = {
        in_game: true,
        type: "test"
    };
    // Send the msg object as a JSON-formatted string.
    socket.send(JSON.stringify(msg));
}


// Send a request to play a game to a user
function sendRequest(username) {
    var msg = {
        in_game: false,
        type: "play_request",
        username: username
    };
    socket.send(JSON.stringify(msg));
}


// main receive function
socket.onmessage = function(event) {
    var msg = JSON.parse(event.data);

    console.log(msg)

    switch(msg.type) {
        case "something":
            console.log("Something");
            sendText();
            break;
        default:
            console.log("Default");
            break;
    }
};


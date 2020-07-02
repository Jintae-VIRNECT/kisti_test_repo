//var ws = new SockJS("/Signaling");
var ws = new WebSocket('wss://192.168.6.3:8073/messages');
    var client = Stomp.over(ws);

// RabbitMQ Web-Stomp does not support heartbeats so disable them
client.heartbeat.outgoing = 0;
client.heartbeat.incoming = 0;

client.debug = onDebug;

// Make sure the user has limited access rights
client.connect("guest", "guest", onConnect, onError, "/");

//Start subscribing to the chat queue
function onConnect() {

    var id = client.subscribe("/topic/push.#", function (d) {
        console.log(d.body);
        //var node = document.createTextNode(d.body + '\n');

        // document.getElementById('chat').appendChild(node);

    });
}

//Send a message to the chat queue
function sendMsg() {
    console.log("!sendMsg!");
    var msg = document.getElementById('msg').value;
    client.send('/amq/noti', {"content-type": "text/plain"}, msg);
}

function onError(e) {
    console.log("STOMP ERROR", e);
}

function onDebug(m) {
    console.log("STOMP DEBUG", m);
}

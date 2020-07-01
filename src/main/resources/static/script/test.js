//var ws = new SockJS("/Signaling");
var ws = new WebSocket("ws://localhost:15674/ws");
var client = Stomp.over(ws);

// RabbitMQ Web-Stomp does not support heartbeats so disable them
client.heartbeat.outgoing = 0;
client.heartbeat.incoming = 0;

client.debug = onDebug;

// Make sure the user has limited access rights
client.connect("admin", "admin1234", onConnect, onError, "/");

function onConnect() {

    var id = client.subscribe("amq.topic/testqueue", function (d) {
        var node = document.createTextNode(d.body + '\n');
        document.getElementById('chat').appendChild(node);

    });
}


function onError(e) {
    console.log("STOMP ERROR", e);
}

function onDebug(m) {
    console.log("STOMP DEBUG", m);
}

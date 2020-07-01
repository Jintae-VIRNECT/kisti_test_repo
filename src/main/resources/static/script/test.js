var ws = new WebSocket('ws://127.0.0.1:15674/ws');
var client = Stomp.over(ws);

// RabbitMQ Web-Stomp does not support heartbeats so disable them
client.heartbeat.outgoing = 0;
client.heartbeat.incoming = 0;

client.debug = function() {
    if (window.console && console.log && console.log.apply) {
        console.log.apply(console, arguments);
    }
};

// Make sure the user has limited access rights
client.connect("admin", "admin1234", onConnect, on_error, "/");

//Start subscribing to the chat queue
function onConnect() {
    var id = client.subscribe("amq.topic/testqueue", function (d) {
        var node = document.createTextNode(d.body + '\n');
        document.getElementById('chat').appendChild(node);

    });
}
var on_error = function() {
    console.log('error');
};
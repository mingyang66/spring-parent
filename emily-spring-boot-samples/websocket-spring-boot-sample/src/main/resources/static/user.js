let stompClient

function init(userid) {
    stompClient = new StompJs.Client({
        brokerURL: 'ws://localhost:8080/gs-guide-websocket/' + userid,
        debug: function (str) {
            console.log('debug:' + str);
        }
    });

    stompClient.onConnect = (frame) => {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/queue/chat', (message) => {
            console.log('订阅消息：' + message)
            let user = JSON.parse(message.body)
            $("#greetings").append("<tr><td style='text-align: right'>" + user.sender + " Reply:" + user.content + "</td></tr>");
        });
    };

    stompClient.onWebSocketError = (error) => {
        console.error('Error with websocket', error);
    };

    stompClient.onStompError = (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
    };
}


function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.publish({
        destination: "/app/send",
        body: JSON.stringify({
            'content': $("#content").val(),
            'sender': $("#sender").val(),
            'receiver': $("#receiver").val()
        })
    });
    $("#greetings").append("<tr><td>" + $("#sender").val() + " Send:" + $("#content").val() + "</td></tr>");
}

function login() {
    console.log("login");
    const sender = $("#sender").val();
    const receiver = $("#receiver").val();
    if (!sender || !receiver) {
        alert('Sender or Receiver cannot be empty');
    }
    init(sender)
}

$(function () {
    $("#login").click(() => login())
    $("form").on('submit', (e) => e.preventDefault());
    $("#connect").click(() => connect());
    $("#disconnect").click(() => disconnect());
    $("#send").click(() => sendName());
});

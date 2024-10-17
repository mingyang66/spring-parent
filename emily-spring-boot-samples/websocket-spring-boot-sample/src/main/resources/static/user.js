let stompClient

function init(userid) {
    stompClient = new StompJs.Client({
        brokerURL: 'ws://localhost:8080/chatroom/' + userid,
        debug: function (str) {
            let date = new Date();
            console.log(date.toDateString() + '-debug:' + str);
        },
        reconnectDelay: 5000,
        heartbeatOutgoing: 2000,  //客户端发送到服务器端的心跳间隔（单位：毫秒）
        heartbeatIncoming: 1000 //服务器发送到客户端的心跳间隔（单位：毫秒）
    });

    stompClient.onConnect = (frame) => {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/user/queue/chat', (message) => {
            console.log('订阅消息：' + message)
            let user = JSON.parse(message.body)
            $("#greetings").append("<tr><td style='text-align: right'>" + user.content + " :Reply" + user.sender + "</td></tr>");
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
    const sender = $("#sender").val();
    const receiver = $("#receiver").val();
    if (!sender || !receiver) {
        alert('Sender or Receiver cannot be empty');
    } else {
        $("#sender").prop("disabled", true);
        $("#receiver").prop("disabled", true);
        $("#login").prop("disabled", true);
        alert('Login Success')
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

let stompClient

function init(userid) {
    stompClient = new StompJs.Client({
        brokerURL: 'ws://localhost:8080/chatroom/' + userid,//要连接STOMP代理的URL
        connectionTimeout: 1000,//如果未在指定毫秒内建立Stomp连接，将重试，默认：0 单位：毫秒（官方说0自动关闭自动重新连接，实际是无法关闭的，只是去掉了超时限制）
        reconnectDelay: 5000,//自动重新连接延迟时间，默认：5000 单位：毫秒，设置为0为禁用
        heartbeatOutgoing: 10000,//客户端发送到服务器端的心跳间隔，默认:10000 设置为0为禁用（单位：毫秒）
        heartbeatIncoming: 10000,//服务器端发送到客户端的心跳间隔，默认:10000 设置为0为禁用（单位：毫秒）
        debug: function (str) {
            console.log(printCurrentDateTime() + '-debug:' + str);
        }
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
    stompClient.onDisconnect = (frame) => {
        console.log('Disconnected: ' + frame.body);
        console.log('Disconnected: ' + frame.headers['message']);
    }
    stompClient.onWebSocketError = (error) => {
        console.error('Error with websocket', error);
    };
    // 回调，在从STOMP Broker收到的ERROR帧上调用。符合要求的STOMP Broker将在此类帧之后关闭连接。
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

function printCurrentDateTime() {
    // 创建一个Date对象，它自动设置为当前日期和时间
    const now = new Date();

    // 分别获取年、月、日、时、分、秒
    // 注意：月份是从0开始的，所以需要+1；日期、小时、分钟、秒直接获取即可
    const year = now.getFullYear();
    const month = (now.getMonth() + 1).toString().padStart(2, '0'); // 使用padStart确保月份是两位数
    const day = now.getDate().toString().padStart(2, '0'); // 使用padStart确保日期是两位数
    const hour = now.getHours().toString().padStart(2, '0'); // 使用padStart确保小时是两位数
    const minute = now.getMinutes().toString().padStart(2, '0'); // 使用padStart确保分钟是两位数
    const second = now.getSeconds().toString().padStart(2, '0'); // 使用padStart确保秒是两位数

    // 拼接年月日时分秒字符串
    return `${year}-${month}-${day} ${hour}:${minute}:${second}`;
}

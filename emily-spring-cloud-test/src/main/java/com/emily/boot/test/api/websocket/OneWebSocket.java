package com.emily.boot.test.api.websocket;

import com.emily.infrastructure.autoconfigure.logger.common.LoggerUtils;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.atomic.AtomicInteger;

@ServerEndpoint(value = "/websocket/connect")
@Component
public class OneWebSocket {

    /**
     * 记录当前在线连接数
     */
    private static AtomicInteger onlineCount = new AtomicInteger(0);

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        onlineCount.incrementAndGet(); // 在线数加1
        LoggerUtils.info(OneWebSocket.class, "有新连接加入："+ session.getId()+"，当前在线人数为：" + onlineCount.get());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        onlineCount.decrementAndGet(); // 在线数减1
        LoggerUtils.info(OneWebSocket.class, "有一连接关闭："+session.getId()+"，当前在线人数为：" + onlineCount.get());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        LoggerUtils.info(OneWebSocket.class, "服务端收到客户端的消息:" + session.getId() + message);
        this.sendMessage("Hello, " + message, session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        LoggerUtils.error(OneWebSocket.class, "发生错误");
        error.printStackTrace();
    }

    /**
     * 服务端发送消息给客户端
     */
    private void sendMessage(String message, Session toSession) {
        try {
            LoggerUtils.info(OneWebSocket.class, "服务端给客户端["+toSession.getId()+"]发送消息:" + message);
            toSession.getBasicRemote().sendText(message);
        } catch (Exception e) {
            LoggerUtils.error(OneWebSocket.class, "服务端发送消息给客户端失败：" + e.getMessage());
        }
    }
}


package com.emily.simple.websocket.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * 用于处理websocket握手请求
 *
 * @author :  Emily
 * @since :  2024/10/14 下午4:41
 */
public class ChatHandshakeHandler extends DefaultHandshakeHandler {
    /**
     * websocket建立连接过程中和用户关联起来
     *
     * @param request    the handshake request
     * @param wsHandler  the WebSocket handler that will handle messages
     * @param attributes handshake attributes to pass to the WebSocket session
     * @return 用户主体对象
     */
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String uri = request.getURI().toString();
        String sender = uri.substring(uri.lastIndexOf("/") + 1);
        return () -> sender;
    }
}

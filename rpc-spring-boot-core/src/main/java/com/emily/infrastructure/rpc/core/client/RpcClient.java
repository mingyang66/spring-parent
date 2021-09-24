package com.emily.infrastructure.rpc.core.client;

import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.rpc.core.client.handler.BaseClientHandler;
import com.emily.infrastructure.rpc.core.client.pool.SocketConn;
import com.emily.infrastructure.rpc.core.protocol.RpcRequest;
import com.emily.infrastructure.rpc.core.protocol.RpcResponse;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @program: spring-parent
 * @description: 创建Netty客户端及自定义处理器
 * @author: Emily
 * @create: 2021/09/17
 */
public class RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);
    /**
     * 连接容器
     */
    private static final List<SocketConn> channels = Lists.newArrayList();

    private String host;
    private int port;

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * 启动netty客户端
     */
    public void start() {
        //连接服务器
        for (int i = 0; i < 1; i++) {
            SocketConn conn = new SocketConn(host, port);
            if (conn.createConn()) {
                channels.add(conn);
            }
        }
    }

    /**
     * 发送请求
     *
     * @param request
     */
    public RpcResponse sendRequest(RpcRequest request) throws InterruptedException {
        int index = RandomUtils.nextInt(0, channels.size());
        SocketConn conn = channels.get(index);
        if (!conn.canUse()) {
           /* channels.remove(index);
            conn = new SocketConn(host, port);
            channels.add(conn);*/
            System.out.println("连接不可用...");
        }
        BaseClientHandler handler = ClientResource.handlerMap.get(conn.conn.id().asLongText());
        synchronized (handler.object) {
            conn.conn.writeAndFlush(request);
            handler.object.wait(5000);
        }
        logger.info("RPC请求数据：{}  ", JSONUtils.toJSONString(request));
        return handler.response;
    }

}

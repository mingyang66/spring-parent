package com.emily.infrastructure.rpc.client.channel;

import com.emily.infrastructure.rpc.client.handler.BaseClientHandler;
import com.emily.infrastructure.rpc.core.decoder.RpcDecoder;
import com.emily.infrastructure.rpc.core.encoder.RpcEncoder;
import com.emily.infrastructure.rpc.core.protocol.RpcRequest;
import com.emily.infrastructure.rpc.core.protocol.RpcResponse;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/09/22
 */
public class RpcClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    private BaseClientHandler baseClientHandler;

    public RpcClientChannelInitializer(BaseClientHandler baseClientHandler) {
        this.baseClientHandler = baseClientHandler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                //空闲状态处理器，参数说明：读时间空闲时间，0禁用时间|写事件空闲时间，0则禁用|读或写空闲时间，0则禁用
                .addLast(new IdleStateHandler(20, 0, 0, TimeUnit.SECONDS))
                .addLast(new RpcEncoder(RpcRequest.class))
                .addLast(new RpcDecoder(RpcResponse.class))
                .addLast(baseClientHandler);
    }
}

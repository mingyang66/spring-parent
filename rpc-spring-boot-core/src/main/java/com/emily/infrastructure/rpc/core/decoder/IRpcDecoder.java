package com.emily.infrastructure.rpc.core.decoder;

import com.emily.infrastructure.rpc.core.entity.message.IRBody;
import com.emily.infrastructure.rpc.core.entity.message.IRHead;
import com.emily.infrastructure.rpc.core.entity.message.IRMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/09/23
 */
public class IRpcDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        //包类型
        int packageType = byteBuf.readInt();
        //连接超时时间
        int keepAlive = byteBuf.readInt();

        //读取消息长度
        int length = byteBuf.readInt();
        if (length == 0) {
            return;
        }
        //初始化存储数据字节数组
        byte[] data = new byte[length];
        //将字节流中的数据读入到字节数组
        byteBuf.readBytes(data);

        list.add(new IRMessage(new IRHead(packageType, keepAlive), IRBody.toBody(data)));
        //重置readerIndex和writerIndex为0
        byteBuf.clear();
    }
}

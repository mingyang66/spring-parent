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
        IRHead head = new IRHead();
        //包类型
        head.setPackageType(byteBuf.readInt());
        //连接超时时间
        head.setKeepAlive(byteBuf.readInt());
        //事务唯一标识长度
        int traceIdLen = byteBuf.readInt();
        //初始化存储事务编号数组
        byte[] traceId = new byte[traceIdLen];
        //将事务编号读入到数组
        byteBuf.readBytes(traceId);
        //事务唯一编号
        head.setTraceId(traceId);

        //读取消息长度
        int length = byteBuf.readInt();
        if (length == 0) {
            return;
        }
        //初始化存储数据字节数组
        byte[] data = new byte[length];
        //将字节流中的数据读入到字节数组
        byteBuf.readBytes(data);

        list.add(new IRMessage(head, IRBody.toBody(data)));
        //重置readerIndex和writerIndex为0
        byteBuf.clear();
    }
}

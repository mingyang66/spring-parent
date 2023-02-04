package com.emily.infrastructure.test.disruptor;

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * @Description :  生产者
 * @Author :  Emily
 * @CreateDate :  Created in 2022/11/22 10:30 上午
 */
public class LoggerEventProducer {
    private RingBuffer<LoggerEvent> ringBuffer;

    public LoggerEventProducer(RingBuffer<LoggerEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void sendData(ByteBuffer data){
        //1.在生产者发送消息的时候，首先需要在我们的ringBuffer里面获取一个可用的序号
        long sequence = ringBuffer.next();
        try{
            //2.根据这个序号，找到具体的"TestEvent"元素 注意：此时获取的event对象是一个没有被赋值的空对象。
            LoggerEvent event = ringBuffer.get(sequence);
            //3.进行实际的赋值操作
            event.setValue(data.getLong(0));
        }finally {
            //4.提交发布操作
            ringBuffer.publish(sequence);
        }
    }
}

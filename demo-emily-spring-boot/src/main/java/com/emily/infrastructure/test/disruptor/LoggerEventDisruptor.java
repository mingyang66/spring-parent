package com.emily.infrastructure.test.disruptor;

import com.emily.infrastructure.core.helper.ThreadPoolHelper;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadFactory;

/**
 * @Description :  主方法
 * @Author :  姚明洋
 * @CreateDate :  Created in 2022/11/22 10:33 上午
 */
public class LoggerEventDisruptor {

    private ThreadFactory threadFactory = ThreadPoolHelper.defaultThreadPoolTaskExecutor();

    private int ringBufferSize = 1024 * 1024;

    static {

    }
    public static void main(String[] args) {

        //参数准备工作
        LoggerEventFactory eventFactory = new LoggerEventFactory();
        int ringBufferSize = 1024 * 1024;
        ThreadFactory executor = ThreadPoolHelper.defaultThreadPoolTaskExecutor();

        /**
         * 实例化disruptor对象
         * 1. EventFactory：消息（event）工程对象
         * 2. ringBufferSize：容器的长度
         * 3. executor：线程池（建议使用自定义线程池）
         * 4. ProducerType：单生产者还是多生产者
         * 5. waitStrategy：等待策略
         */
        Disruptor<LoggerEvent> disruptor = new Disruptor<>(
                eventFactory,
                ringBufferSize,
                executor,
                ProducerType.MULTI,
                new YieldingWaitStrategy());

        //2.添加消费者的监听
        disruptor.handleEventsWith(new LoggerEventHandler());

        //3.启动disruptor
        disruptor.start();

        //4.获取实际存储数据的容器：RingBuffer
        RingBuffer<LoggerEvent> ringBuffer = disruptor.getRingBuffer();

        LoggerEventProducer producer = new LoggerEventProducer(ringBuffer);

        ByteBuffer bb = ByteBuffer.allocate(8);

        for (long i = 0; i < 100; i++) {
            bb.putLong(0, i);
            producer.sendData(bb);
        }

        disruptor.shutdown();
    }
}

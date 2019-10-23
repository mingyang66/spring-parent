package com.yaomy.control.rabbitmq.direct;

import com.google.common.collect.Maps;
import com.rabbitmq.client.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: Direct类型交换器消费者
 * @Version: 1.0
 */
@SuppressWarnings("all")
public class Consumer {
    /**
     * 队列名称
     */
    private static final String QUEUE_NAME = "test_queue";

    public static void main(String[] args) throws Exception {
        /**
         * {@link Connection}的工厂类
         */
        ConnectionFactory factory = new ConnectionFactory();
        /**
         * 设置连接的主机
         */
        factory.setHost("127.0.0.1");
        /**
         * 用户名
         */
        factory.setUsername("admin");
        /**
         * 密码
         */
        factory.setPassword("admin");
        /**
         * 创建新的代理连接
         */
        Connection connection = factory.newConnection();
        /**
         * 使用内部分配的通道号创建一个新的频道
         */
        Channel channel = connection.createChannel();
        /**
         * prefetchCount:服务端每次分派给消费者的消息数量
         */
        channel.basicQos(1);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        /**
         * 当一个消息被发送过来时，将会被回调的接口
         * consumerTag：与消费者相关的消费者标签
         * delivery:发送过来的消息
         */
        DeliverCallback deliverCallback = (consumerTag, delivery)->{
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println("消费者优先级为9的消费者标识："+consumerTag);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e){

            }
        };
        Map<String, Object> arguments = Maps.newHashMap();
        arguments.put("x-priority", 9);
        /**
         * String basicConsume(String queue, boolean autoAck, DeliverCallback deliverCallback, CancelCallback cancelCallback)
         * 启动一个消费者，并返回服务端生成的消费者标识
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * deliverCallback： 当一个消息发送过来后的回调接口
         * cancelCallback：当一个消费者取消订阅时的回调接口;取消消费者订阅队列时除了使用{@link Channel#basicCancel}之外的所有方式都会调用该回调方法
         * @return 服务端生成的消费者标识
         */
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
            System.out.println("消费者优先级为9的消费者标识："+consumerTag);
        });
    }
}

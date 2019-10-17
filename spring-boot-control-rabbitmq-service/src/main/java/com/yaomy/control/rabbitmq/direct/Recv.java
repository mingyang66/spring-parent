package com.yaomy.control.rabbitmq.direct;

import com.rabbitmq.client.*;

/**
 * @Description: Description
 * @Version: 1.0
 */
public class Recv {
    /**
     * 队列名称
     */
    private static final String QUEUE_NAME = "hello";

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
         * 声明队列
         * durable: true 如果我们声明一个持久化队列（队列将会在服务重启后任然存在）
         * exclusive: true 如果我们声明一个独占队列（仅限于此链接）
         * autoDelete: true 如果我们声明一个自动删除队列（服务器将在我们不在使用它时删除，即队列的连接数为0）
         * arguments: 队列的其它属性（构造参数）
         */
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        /**
         * prefetchCount:服务端每次分派给消费者的消息数量
         */
        channel.basicQos(1);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        /**
         * 接收到消息后消费者会回调对应接口
         */
        DeliverCallback deliverCallback = (consumerTag, delivery)->{
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
        };
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}

package com.yaomy.control.rabbitmq.direct;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

/**
 * @Description: Direct类型交换器消费者
 * @Version: 1.0
 */
public class Recv {
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
         * 当一个消息被发送过来时，将会被回调的接口
         * consumerTag：与消费者相关的消费者标签
         * delivery:发送过来的消息
         */
        DeliverCallback deliverCallback = (consumerTag, delivery)->{
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
        };
        /**
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * deliverCallback： 当一个消息发送过来后的回调接口
         * cancelCallback：当一个消费者关闭时的回调接口
         */
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
    }
}

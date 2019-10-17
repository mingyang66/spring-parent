package com.yaomy.control.rabbitmq.direct;

import com.rabbitmq.client.*;

import java.util.concurrent.TimeUnit;

/**
 * @Description: 发送消息客户端
 * @Version: 1.0
 */
public class Send {
    /**
     * 队列名称
     */
    private static final String QUEUE_NAME = "test_queue";
    /**
     * 交换器
     */
    public static final String EXCHANGE_NAME = "test_exchange";
    /**
     * 路由
     */
    public static final String ROUTING_KEY  = "test_routing_key";

    public static void main(String[] args) throws Exception{
        /**
         * {@link Connection}的工厂类
         */
        ConnectionFactory factory = new ConnectionFactory();
        /**
         * 设置连接的主机
         */
        factory.setHost("127.0.0.1");
        /**
         * 端口号
         */
        factory.setPort(5672);
        /**
         * 用户名
         */
        factory.setUsername("admin");
        /**
         * 密码
         */
        factory.setPassword("admin");
        /**
         * 可以访问虚拟主机
         */
        factory.setVirtualHost("/");
        /**
         * 创建新的代理连接
         */
        try(Connection connection = factory.newConnection()){
            /**
             * 使用内部分配的通道号创建一个新的频道
             */
            Channel channel = connection.createChannel();
            /**
             * 声明一个交换器（Exchange），通过完整的参数集；
             * exchange: 交换器的名称
             * type: 交换器类型
             * durable: true 如果声明一个持久化的交换器（服务端重启交换器仍然存在）
             * autoDelete: true 如果服务器不在使用交换器时删除它
             * internal: true 如果交换器是内置的，则表示客户端无法直接发送消息到这个交换器中，只能通过交换器路由到交换器的方式
             * arguments: 交换器的其它属性（构造参数）
             */
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true, false, false, null);
            /**
             * 声明队列
             * durable: true 如果我们声明一个持久化队列（队列将会在服务重启后任然存在）
             * exclusive: true 如果我们声明一个独占队列（仅限于此链接）
             * autoDelete: true 声明一个自动删除队列（服务器将在不使用它时删除，即队列的连接数为0）
             * arguments: 队列的其它属性（构造参数）
             */
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            /**
             * queue:队列名称
             * exchange：交换器名称
             * routingKey：用于绑定的路由key
             */
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
            String message = "Hello World";
            while (true) {
                /**
                 * 发布消息
                 * 发布到不存在的交换器将导致信道级协议异常，该协议关闭信道，
                 * exchange: 要将消息发送到的交换器
                 * routingKey: 路由KEY
                 * props: 消息的其它属性，如：路由头等
                 * body: 消息体
                 */
                channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
                System.out.println(" [x] Sent '" + message + "'");
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }
}

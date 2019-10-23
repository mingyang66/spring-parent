package com.yaomy.control.rabbitmq.direct;

import com.google.common.collect.Maps;
import com.rabbitmq.client.*;
import org.apache.commons.lang3.RandomUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Description: Direct类型交换器发送消息客户端
 * @Version: 1.0
 */
@SuppressWarnings("all")
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
            connection.addBlockedListener(new BlockedListener() {
                @Override
                public void handleBlocked(String reason) throws IOException {
                    System.out.println("Blocked:"+reason);
                }

                @Override
                public void handleUnblocked() throws IOException {
                    System.out.println("Unblocked");
                }
            });
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

            //channel.exchangeDeclare("some.exchange.name", BuiltinExchangeType.DIRECT);

            Map<String, Object> arguments = Maps.newHashMap();
            /**
             * 设置消息发送到队列中在被丢弃之前可以存活的时间，单位：毫秒
             */
            //arguments.put("x-message-ttl", 10*60*1000);
            /**
             * 设置一个队列多长时间未被使用将会被删除，单位：毫秒
             */
            //arguments.put("x-expires", 15*60*1000);
            /**
             * queue中可以存储处于ready状态的消息数量
             */
            //arguments.put("x-max-length", 600);
            /**
             * queue中可以存储处于ready状态的消息占用的内存空间
             */
            //arguments.put("x-max-length-bytes", 111024);
            /**
             * queue溢出行为，这将决定当队列达到设置的最大长度或者最大的存储空间时发送到消息队列的消息的处理方式；
             * 有效的值是：drop-head（删除queue头部的消息）、reject-publish（拒绝发送来的消息）、reject-publish-dlx（拒绝发送消息到死信交换器）
             * 类型为quorum 的queue只支持drop-head;
             */
            //arguments.put("x-overflow", "reject-publish");
            /**
             * 死信交换器，消息被拒绝或过期时将会重新发送到的交换器
             */
            //arguments.put("x-dead-letter-exchange", "some.exchange.name");
            /**
             * 当消息是死信时使用的可选替换路由
             */
            //arguments.put("x-dead-letter-routing-key", "some-routing-key");
            /**
             * 设置队列支持的最大优先级为10，如果不设置此参数，队列将不会支持消息优先级
             */
            arguments.put("x-max-priority", 10);
            /**
             * 声明队列
             * durable: true 如果我们声明一个持久化队列（队列将会在服务重启后任然存在）
             * exclusive: true 如果我们声明一个独占队列（仅限于此链接）
             * autoDelete: true 声明一个自动删除队列（服务器将在不使用它时删除，即队列的连接数为0）
             * arguments: 队列的其它属性（构造参数）
             */
            channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);
            //channel.queueDeclare("some.queue.name", true, false, false, null);
            /**
             * mandatory：如果为true,则消息回退，通过basic.return方法退回给发送者
             */
            channel.addReturnListener((returnMessage)-> {
                try {
                    System.out.println("退回的消息是："+returnMessage.getExchange()+","+returnMessage.getRoutingKey()+","+returnMessage.getReplyCode()+","+returnMessage.getReplyText()+","+new String(returnMessage.getBody(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
            /**
             * queue:队列名称
             * exchange：交换器名称
             * routingKey：用于绑定的路由key
             */
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
           // channel.queueBind("some.queue.name", "some.exchange.name", "some-routing-key");
            String message = "Hello World,我们现在做的是测试RabbitMQ消息中间件，这中间我们可能会遇到很多的问题，不怕，一个一个的解决！";
            /*for(int i=0;i<10;i++){
                message = StringUtils.join(message, "Hello World,我们现在做的是测试RabbitMQ消息中间件，这中间我们可能会遇到很多的问题，不怕，一个一个的解决！");
            }
            System.out.println(message.getBytes().length);*/
            int i=0;
            while (true) {
                AMQP.BasicProperties.Builder properties = MessageProperties.PERSISTENT_TEXT_PLAIN.builder();
                //设置消息过期时间，单位：毫秒
                //properties.expiration("600000");
                int priority = RandomUtils.nextInt(0, 15);
                /**
                 * 设置消息的优先级
                 */
                properties.priority(priority);
                /**
                 * 发布消息
                 * 发布到不存在的交换器将导致信道级协议异常，该协议关闭信道，
                 * exchange: 要将消息发送到的交换器
                 * routingKey: 路由KEY
                 * props: 消息的其它属性，如：路由头等
                 * body: 消息体
                 */
                channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, true, properties.build(), (priority+":"+message).getBytes());
                //System.out.println(" [x] Sent '" + priority+":"+message + "'");
                /*TimeUnit.SECONDS.sleep(1);
                if(i++ == 10){
                    break;
                }*/
            }
        }
    }
}

package com.yaomy.control.rabbitmq.cluster;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rabbitmq.client.*;
import org.apache.commons.lang3.RandomUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
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
    public static final String ROUTING_KEY = "test.routing.key";
    /**
     * BindingKey
     */
    public static final String BINDING_KEY = "*.routing.key";

    public static void main(String[] args) throws Exception {
        /**
         * {@link Connection}的工厂类
         */
        ConnectionFactory factory = new ConnectionFactory();
        /**
         * 设置vhost
         */
        factory.setVirtualHost(ConnectionFactory.DEFAULT_VHOST);
        /**
         * 设置连接的主机
         */
        factory.setHost("127.0.0.1");
        /**
         * 设置端口号
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

        List<Address> addresses = Lists.newArrayList();
        addresses.add(new Address("127.0.0.1", 5672));
        addresses.add(new Address("127.0.0.1", 5673));
        addresses.add(new Address("127.0.0.1", 5674));
        /**
         * 创建新的代理连接
         */
        try (Connection connection = factory.newConnection(addresses)) {
           /* char[] keyPassphrase = "MySecretPassword".toCharArray();
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new FileInputStream("/path/to/client/keycert.p12"), keyPassphrase);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, "passphrase".toCharArray());

            char[] trustPassphrase = "rabbitstore".toCharArray();
            KeyStore tks = KeyStore.getInstance("JKS");
            tks.load(new FileInputStream("/path/to/trustStore"), trustPassphrase);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(tks);

            SSLContext c = SSLContext.getInstance("TLSv1.2");
            c.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            factory.useSslProtocol(c);*/
            /**
             * 使用内部分配的通道号创建一个新的频道
             */
            Channel channel = connection.createChannel();
            connection.addBlockedListener(new BlockedListener() {
                @Override
                public void handleBlocked(String reason) throws IOException {
                    System.out.println("Blocked:" + reason);
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
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC, true, false, false, null);

            Map<String, Object> arguments = Maps.newHashMap();

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
            /**
             * mandatory：如果为true,则消息回退，通过basic.return方法退回给发送者
             */
            channel.addReturnListener((returnMessage) -> {
                try {
                    System.out.println("退回的消息是：" + returnMessage.getExchange() + "," + returnMessage.getRoutingKey() + "," + returnMessage.getReplyCode() + "," + returnMessage.getReplyText() + "," + new String(returnMessage.getBody(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
            /**
             * queue:队列名称
             * exchange：交换器名称
             * routingKey：用于绑定的路由key
             */
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, BINDING_KEY);
            String message = "Hello World,我们现在做的是测试RabbitMQ消息中间件，这中间我们可能会遇到很多的问题，不怕，一个一个的解决！";
            /**
             * 在此信道上开启发布者确认（publisher confirms）
             */
            channel.confirmSelect();
            ConcurrentNavigableMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();
            channel.addConfirmListener(new ConfirmListener() {
                @Override
                public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                    String body = outstandingConfirms.get(deliveryTag);
                    System.out.println("发布的消息已经被ack,序列号是：" + deliveryTag + ",multiple:" + multiple + ",message:" + message);
                    if (multiple) {
                        ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(deliveryTag, true);
                        confirmed.clear();
                    } else {
                        outstandingConfirms.remove(deliveryTag);
                    }
                }

                @Override
                public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                    String body = outstandingConfirms.get(deliveryTag);
                    System.out.println("发布的消息已经被nack-ed,序列号是：" + deliveryTag + ",multiple:" + multiple + ",message:" + message);
                    if (multiple) {
                        ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(deliveryTag, true);
                        confirmed.clear();
                    } else {
                        outstandingConfirms.remove(deliveryTag);
                    }
                }
            });
            while (true) {
                AMQP.BasicProperties.Builder properties = MessageProperties.PERSISTENT_TEXT_PLAIN.builder();
                int priority = RandomUtils.nextInt(0, 15);
                /**
                 * 设置消息的优先级
                 */
                properties.priority(priority);
                /**
                 * 发布确认序号和消息映射关系
                 */
                outstandingConfirms.put(channel.getNextPublishSeqNo(), priority + ":" + message);
                /**
                 * 发布消息
                 * 发布到不存在的交换器将导致信道级协议异常，该协议关闭信道，
                 * exchange: 要将消息发送到的交换器
                 * routingKey: 路由KEY
                 * props: 消息的其它属性，如：路由头等
                 * body: 消息体
                 */
                channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, true, properties.build(), (priority + ":" + message).getBytes());
                System.out.println(" [x] Sent '" + priority + ":" + message + "'");
                TimeUnit.MILLISECONDS.sleep(100);
            }
        }
    }

}

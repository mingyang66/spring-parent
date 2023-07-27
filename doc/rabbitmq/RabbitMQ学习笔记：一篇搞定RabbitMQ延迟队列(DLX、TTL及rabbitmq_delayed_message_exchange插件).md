### RabbitMQ学习笔记：一篇搞定RabbitMQ延迟队列(DLX、TTL及rabbitmq_delayed_message_exchange插件)

> 延迟队列存储的对象是对应的延迟消息，所谓的延迟消息是指当消息被发送以后，并不想让消费者立刻拿到消息，而是等待特定时间后，消费者才能拿到这个消息进行消费。

延迟消息使用的场景有很多，比如：

1. 在订单系统中，一个用户下单之后通常有30分钟的时间进行支付，如果30分钟之内没有支付成功，那么这个订单将进行异常处理，这时候就可以使用延迟队列来处理这些订单了。
2. 用户希望通过手机远程遥控家里的智能设备在指定的时间进行工作，这时候就可以将用户指令发送到延迟队列，当指令设定的时间到了再将指令推送到只能设备。

在AMQP协议中，或RabbitMQ本身没有直接支持延迟队列的功能，但是可以通过TTL和DLX模拟出延迟队列的功能；也可以通过rabbitmq_delayed_message_exchange插件来实现。

#### DLX和TTL模拟延迟队列

- 消息变成死信一般由以下几种情况
    1. 消息被拒绝（Basic.Reject/Basic.Nack）,并且设置requeue参数为false;
    2. 消息过期
    3. 队列达到最大长度
- DLX

DLX是一个正常的交换器，和一般的交换器没有区别，它能在任何的队列上被指定，实际上就是设置某个队列的属性。当这个队列中存在死信时，RabbitMQ就会自动地将这个消息重新发布到设置的DLX上去，进而被路由到另一个队列，即死信队列。可以监听这个队列中的消息进行相应的处理，这个特性与将消息的TTL设置为0配合使用可以弥补immediate参数的功能。

- 声明队列、交换器、绑定路由并在容器启动时自动创建，通过在队列的参数上设置x-dead-letter-exchange参数添加死信交换器，设置x-dead-letter-routing-key参数添加死信路由

```java
package com.yaomy.control.rabbitmq.amqp.ttl.config;

import com.google.common.collect.Maps;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 *  RabbitMQ生产者交换器、绑定、队列声明
 * @since 1.0
 */
@SuppressWarnings("all")
@Configuration
public class RabbitConfig {
    public static final String TTL_TOPIC_EXCHANGE = "ttl.topic.exchange";
    public static final String TTL_TOPIC_QUEUE = "ttl_topic_queue";
    public static final String TTL__TOPIC_ROUTING_KEY = "*.topic.*";
    public static final String TTL_DELAY_EXCHANGE = "ttl.dlx.exchange";
    public static final String TTL_DELAY_ROUTING_KEY = "ttl.dlrk.routingkey";
    public static final String TTL_DELAY_QUEUE = "ttl_dlk_queue";
    /**
     * 声明队列
     */
    @Bean
    public Queue topicQueue(){
        Map<String, Object> args = Maps.newHashMap();
        /**
         * 设置消息发送到队列之后多久被丢弃，单位：毫秒
         */
        args.put("x-message-ttl", 10000);
        /**
         * 消息变成死信一般由以下几种情况引起：
         * 1.消息被拒绝，并且设置requeue参数为false
         * 2.消息过期
         * 3.队列达到最大长度
         * x-dead-letter-exchange参数是指消息编程死信之后重新发送的DLX
         */
        args.put("x-dead-letter-exchange", TTL_DELAY_EXCHANGE);
        /**
         * 为DLX指定路由键DLK
         */
        args.put("x-dead-letter-routing-key", TTL_DELAY_ROUTING_KEY);
        /**
         * 定义优先级队列，消息最大优先级为15，优先级范围为0-15，数字越大优先级越高
         */
        args.put("x-max-priority", 15);
        /**
         * 设置持久化队列
         */
        return QueueBuilder.durable(TTL_TOPIC_QUEUE).withArguments(args).build();
    }


    /**
     * 声明Topic类型交换器
     */
    @Bean
    public TopicExchange topicExchange(){
        TopicExchange exchange = new TopicExchange(TTL_TOPIC_EXCHANGE);
        return exchange;
    }

    /**
     * Topic交换器和队列通过bindingKey绑定
     * @return
     */
    @Bean
    public Binding bindingTopicExchangeQueue(){
        return BindingBuilder.bind(topicQueue()).to(topicExchange()).with(TTL__TOPIC_ROUTING_KEY);
    }

    //============================延迟队列及交换器定义=================================
    /**
     * 定义延迟队列
     */
    @Bean
    public Queue ttlQueue(){
        return QueueBuilder.durable(TTL_DELAY_QUEUE).build();
    }

    /**
     * 定义延迟交换器
     */
    @Bean
    public TopicExchange ttlExchange(){
        TopicExchange exchange = new TopicExchange(TTL_DELAY_EXCHANGE);
        return exchange;
    }

    /**
     * 延迟队列交换器绑定
     */
    @Bean
    public Binding bindingTtlDirectExchangeQueue(){
        return BindingBuilder.bind(ttlQueue()).to(ttlExchange()).with(TTL_DELAY_ROUTING_KEY);
    }
}

```

- 创建消息生产者

```java
package com.yaomy.control.rabbitmq.amqp.ttl;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.PublisherCallbackChannel;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 *  RabbitMQ生产者
 * @ProjectName: spring-parent
 * @since 1.0
 */
@SuppressWarnings("all")
@Component
public class RabbitSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 创建一个消息是否投递成功的回调方法
     */
    private final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        /**
         *
         * @param correlationData 消息的附加信息
         * @param ack true for ack, false for nack
         * @param cause 是一个可选的原因，对于nack，如果可用，否则为空。
         */
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            if(!ack){
                //可以进行日志记录、异常处理、补偿处理等
                System.err.println("异常ack-"+ack+",id-"+correlationData.getId()+",cause:"+cause);
            }else {
                //更新数据库，可靠性投递机制
                System.out.println("正常ack-"+ack+",id-"+correlationData.getId());
                try{
                System.out.println(new String(correlationData.getReturnedMessage().getBody()));

                } catch (Exception e){

                }
            }
        }
    };
    /**
     * 创建一个消息是否被队列接收的监听对象，如果没有队列接收发送出的消息，则调用此方法进行后续处理
     */
    private final RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
        /**
         *
         * @param message 被退回的消息
         * @param replyCode 错误编码
         * @param replyText 错误描述
         * @param exchange 交换器
         * @param routingKey 路由
         */
        @Override
        public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
            System.err.println("spring_returned_message_correlation:"+message.getMessageProperties().getHeaders().get(PublisherCallbackChannel.RETURNED_MESSAGE_CORRELATION_KEY)
                                +"return exchange: " + exchange
                                + ", routingKey: "+ routingKey
                                + ", replyCode: " + replyCode
                                + ", replyText: " + replyText
                                + ",message:" + message);
            try {
                System.out.println(new String(message.getBody()));
            } catch (Exception e){

            }
        }
    };
    /**
     * 扩展点，在消息转换完成之后，发送之前调用；可以修改消息属性、消息头信息
     */
    private final MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
        @Override
        public Message postProcessMessage(Message message) throws AmqpException {
            MessageProperties properties = message.getMessageProperties();
            /**
             * 设置消息发送到队列之后多久被丢弃，单位：毫秒
             * 此种方案需要每条消息都设置此属性，比较灵活；
             * 还有一种方案是在声明队列的时候指定发送到队列中的过期时间；
             * * Queue queue = new Queue("test_queue2");
             * * queue.getArguments().put("x-message-ttl", 10000);
             * 这两种方案可以同时存在，以值小的为准
             */
            //properties.setExpiration("10000");
            /**
             * 设置消息的优先级
             */
            properties.setPriority(9);
            /**
             * 设置消息发送到队列中的模式，持久化|非持久化（只存在于内存中）
             */
            properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);

            return message;
        }
    };
    /**
     * 发送消息
     * @param exchange 交换器
     * @param route 路由键
     * @param message 消息
     * @param properties
     */
    public void sendMsg(String exchange, String routingKey, String message, MessageProperties properties){
        /**
         * 设置生产者消息publish-confirm回调函数
         */
        this.rabbitTemplate.setConfirmCallback(confirmCallback);
        /**
         * 设置消息退回回调函数
         */
        this.rabbitTemplate.setReturnCallback(returnCallback);
        /**
         * 新增消息转换完成后、发送之前的扩展点
         */
        this.rabbitTemplate.setBeforePublishPostProcessors(messagePostProcessor);

        try {
            if(null == properties){
                properties = new MessageProperties();
            }
            /**
             * 设置消息唯一标识
             */
            properties.setMessageId(UUID.randomUUID().toString());
            /**
             * 创建消息包装对象
             */
            Message msg = MessageBuilder.withBody(message.getBytes()).andProperties(properties).build();
            /**
             * 将消息主题和属性封装在Message类中
             */
            Message returnedMessage = MessageBuilder.withBody(message.getBytes()).build();
            /**
             * 相关数据
             */
            CorrelationData correlationData = new CorrelationData();
            /**
             * 消息ID，全局唯一
             */
            correlationData.setId(msg.getMessageProperties().getMessageId());

            /**
             * 设置此相关数据的返回消息
             */
            correlationData.setReturnedMessage(returnedMessage);
            /**
             * 如果msg是org.springframework.amqp.core.Message对象的实例，则直接返回，否则转化为Message对象
             */
            this.rabbitTemplate.convertAndSend(exchange, routingKey, msg, correlationData);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

```

- 队列示意图

![在这里插入图片描述](https://img-blog.csdnimg.cn/2020010216025482.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9taW5neWFuZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

### 延迟消息插件rabbitmq_delayed_message_exchange

>
一段时间以来，人们一直在寻找用RabbitMQ实现延迟消息的传递方法，到目前为止，公认的解决方案是混合使用TTL和DLX。而rabbitmq_delayed_message_exchange插件就是基于此来实现的，RabbitMQ延迟消息插件新增了一种新的交换器类型，消息通过这种交换器路由就可以实现延迟发送。

##### 插件安装

插件安装，当前我使用的是3.8.1，一定要找到自己对应的版本来下载，否则会出现异常

- 到 https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/tag/v3.8.0
  上下载 [rabbitmq_delayed_message_exchange-3.8.0.ez](https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/v3.8.0/rabbitmq_delayed_message_exchange-3.8.0.ez)
  或者 [Source code(zip)](https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/archive/v3.8.0.zip)
  可以先下载到本地再上传到/usr/lib/rabbitmq/lib/rabbitmq_server-3.8.1/plugins目录下；源码包要先解压缩；也可以使用wget（wget  https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/v3.8.0/rabbitmq_delayed_message_exchange-3.8.0.ez
  ）直接下载到服务器plugins目录下;
- 启动插件rabbitmq-plugins enable rabbitmq_delayed_message_exchange

首先可以通过rabbitmq-plugins list命令查看插件名：

```
[root@rabbit3 plugins]# rabbitmq-plugins list
Listing plugins with pattern ".*" ...
 Configured: E = explicitly enabled; e = implicitly enabled
 | Status: * = running on rabbit@rabbit3
 |/
[  ] rabbitmq_amqp1_0                  3.8.1
[  ] rabbitmq_auth_backend_cache       3.8.1
[  ] rabbitmq_auth_backend_http        3.8.1
[  ] rabbitmq_auth_backend_ldap        3.8.1
[  ] rabbitmq_auth_backend_oauth2      3.8.1
[  ] rabbitmq_auth_mechanism_ssl       3.8.1
[  ] rabbitmq_consistent_hash_exchange 3.8.1
[  ] rabbitmq_delayed_message_exchange 3.8.0
[  ] rabbitmq_event_exchange           3.8.1
[  ] rabbitmq_federation               3.8.1
[  ] rabbitmq_federation_management    3.8.1
[  ] rabbitmq_jms_topic_exchange       3.8.1
[E*] rabbitmq_management               3.8.1
[e*] rabbitmq_management_agent         3.8.1
[  ] rabbitmq_mqtt                     3.8.1
[  ] rabbitmq_peer_discovery_aws       3.8.1
[  ] rabbitmq_peer_discovery_common    3.8.1
[  ] rabbitmq_peer_discovery_consul    3.8.1
[  ] rabbitmq_peer_discovery_etcd      3.8.1
[  ] rabbitmq_peer_discovery_k8s       3.8.1
[  ] rabbitmq_prometheus               3.8.1
[  ] rabbitmq_random_exchange          3.8.1
[  ] rabbitmq_recent_history_exchange  3.8.1
[  ] rabbitmq_sharding                 3.8.1
[  ] rabbitmq_shovel                   3.8.1
[  ] rabbitmq_shovel_management        3.8.1
[  ] rabbitmq_stomp                    3.8.1
[  ] rabbitmq_top                      3.8.1
[  ] rabbitmq_tracing                  3.8.1
[  ] rabbitmq_trust_store              3.8.1
[e*] rabbitmq_web_dispatch             3.8.1
[  ] rabbitmq_web_mqtt                 3.8.1
[  ] rabbitmq_web_mqtt_examples        3.8.1
[  ] rabbitmq_web_stomp                3.8.1
[  ] rabbitmq_web_stomp_examples       3.8.1
```

启动插件：

```
[root@rabbit3 plugins]# rabbitmq-plugins enable rabbitmq_delayed_message_exchange
Enabling plugins on node rabbit@rabbit3:
rabbitmq_delayed_message_exchange
The following plugins have been configured:
  rabbitmq_delayed_message_exchange
  rabbitmq_management
  rabbitmq_management_agent
  rabbitmq_web_dispatch
Applying plugin configuration to rabbit@rabbit3...
The following plugins have been enabled:
  rabbitmq_delayed_message_exchange

started 1 plugins.

```

关闭插件命令：

```
[root@rabbit3 plugins]# rabbitmq-plugins disable rabbitmq_delayed_message_exchange
Disabling plugins on node rabbit@rabbit3:
rabbitmq_delayed_message_exchange
The following plugins have been configured:
  rabbitmq_management
  rabbitmq_management_agent
  rabbitmq_web_dispatch
Applying plugin configuration to rabbit@rabbit3...
The following plugins have been disabled:
  rabbitmq_delayed_message_exchange

stopped 1 plugins.
```

- 升级RabbitMQ时，必须冲新安装该插件，也就是要安装它们的新版本；或者，可以在升级之前或升级期间禁用它们。

##### 交换器应用

使用延迟消息交换器需要声明一个 x-delayed-message 类型的交换器，示例如下：

```java
// ... elided code ...
Map<String, Object> args = new HashMap<String, Object>();
args.put("x-delayed-type", "direct");
channel.exchangeDeclare("my-exchange", "x-delayed-message", true, false, args);
// ... more code ...
```

上面的示例当我们声明一个交换器时，我们提供了一个x-delayed-type参数，值设置为direct。这是想告诉交换器希望它路由消息的行为、绑定等等像direct类型交换器一样；在上面示例中，我们的交换器就像direct交换器一样。我们也可以传递topic、fanout或者其它插件提供的自定义交换器类型。

##### 发布延迟消息

用户必须使用名为x-delay的特殊header发布延迟消息，该header需要一个整数，表示RabbitMQ应延迟消息的毫秒数。值得注意的是，这里的延迟意味着消息延迟路由到队列或其它交换器。

exhange（交换器）没有消费者的概念。因此，一旦延迟过期，插件将尝试将消息路由到与exchange的路由规则匹配的队列。请注意，如果消息不能路由到任何队列，那么它将被丢弃。

以下是添加x-delay 头（header）到消息并且发布到exchange的示例代码：

```java
// ... elided code ...
byte[] messageBodyBytes = "delayed payload".getBytes();
AMQP.BasicProperties.Builder props = new AMQP.BasicProperties.Builder();
headers = new HashMap<String, Object>();
headers.put("x-delay", 5000);
props.headers(headers);
channel.basicPublish("my-exchange", "", props.build(), messageBodyBytes);
```

上面的示例中，消息在被插件路由之前将被延迟5秒钟。该示例假设你已经建立了到RabbitMQ的连接并获得了一个信道。

##### 延迟消息完整示例

延迟队列、交换器、绑定声明创建：

```java
package com.yaomy.control.rabbitmq.amqp.delay.config;

import com.google.common.collect.Maps;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 *  RabbitMQ生产者交换器、绑定、队列声明
 * @since 1.0
 */
@SuppressWarnings("all")
@Configuration
public class RabbitDelayConfig {
    /**
     * 交换器
     */
    public static final String DELAY_TEST_EXCHANGE = "delay.test.exchange";
    /**
     * 队列
     */
    public static final String DELAY_TEST_QUEUE = "delay_test_queue";
    /**
     * 路由
     */
    public static final String DELAY_TEST_ROUTING_KEY = "delay.test.routing.key";
    /**
     * 声明延时队列
     */
    @Bean
    public Queue delayQueue(){
        Map<String, Object> args = Maps.newHashMap();
        /**
         * 定义优先级队列，消息最大优先级为15，优先级范围为0-15，数字越大优先级越高
         */
        args.put("x-max-priority", 15);
        /**
         * 设置持久化队列
         */
        return QueueBuilder.durable(DELAY_TEST_QUEUE).withArguments(args).build();
    }


    /**
     * 延时队列交换器
     * 注意：
     * 1.交换器类是CustomExchange
     * 2.交换器类型是x-delayed-message
     */
    @Bean
    public CustomExchange delayExchange(){
        Map<String, Object> args = new HashMap<>();
        /**
         * 设置自定义交换器路由消息的类型，direct类似direct交换器路由消息的模式，也可以传递topic、fanout,或者其它插件提供的自定义的交换器类型
         */
        args.put("x-delayed-type", "topic");

        return new CustomExchange(DELAY_TEST_EXCHANGE, "x-delayed-message", true, false, args);
    }

    /**
     * 延迟队列绑定交换器
     */
    @Bean
    public Binding bindingDelayCustomExchangeQueue(){
        return BindingBuilder.bind(delayQueue()).to(delayExchange()).with(DELAY_TEST_ROUTING_KEY).noargs();
    }
}

```

延迟消息生产者：

```java
package com.yaomy.control.rabbitmq.amqp.delay;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.PublisherCallbackChannel;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 *  RabbitMQ生产者（延迟队列）
 * @ProjectName: spring-parent
 * @since 1.0
 */
@SuppressWarnings("all")
@Component
public class RabbitDelaySender {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 创建一个消息是否投递成功的回调方法
     */
    private final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        /**
         *
         * @param correlationData 消息的附加信息
         * @param ack true for ack, false for nack
         * @param cause 是一个可选的原因，对于nack，如果可用，否则为空。
         */
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            if(!ack){
                //可以进行日志记录、异常处理、补偿处理等
                System.err.println("异常ack-"+ack+",id-"+correlationData.getId()+",cause:"+cause);
            }else {
                //更新数据库，可靠性投递机制
                System.out.println("正常ack-"+ack+",id-"+correlationData.getId());
                try{
                System.out.println(new String(correlationData.getReturnedMessage().getBody()));

                } catch (Exception e){

                }
            }
        }
    };
    /**
     * 创建一个消息是否被队列接收的监听对象，如果没有队列接收发送出的消息，则调用此方法进行后续处理
     */
    private final RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
        /**
         *
         * @param message 被退回的消息
         * @param replyCode 错误编码
         * @param replyText 错误描述
         * @param exchange 交换器
         * @param routingKey 路由
         */
        @Override
        public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
            System.err.println("spring_returned_message_correlation:"+message.getMessageProperties().getHeaders().get(PublisherCallbackChannel.RETURNED_MESSAGE_CORRELATION_KEY)
                                +"return exchange: " + exchange
                                + ", routingKey: "+ routingKey
                                + ", replyCode: " + replyCode
                                + ", replyText: " + replyText
                                + ",message:" + message);
            try {
                System.out.println(new String(message.getBody()));
            } catch (Exception e){

            }
        }
    };
    /**
     * 扩展点，在消息转换完成之后，发送之前调用；可以修改消息属性、消息头信息
     */
    private final MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
        @Override
        public Message postProcessMessage(Message message) throws AmqpException {
            MessageProperties properties = message.getMessageProperties();
            /**
             * 设置消息的优先级
             */
            properties.setPriority(9);
            /**
             * 设置消息发送到队列中的模式，持久化|非持久化（只存在于内存中）
             */
            properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
            /**
             * Set the x-delay header.
             */
            properties.setDelay(10000);
            /**
             * 或设置x延迟header
             */
            //properties.getHeaders().put("x-delay", 10000);

            return message;
        }
    };
    /**
     * 发送消息
     * @param exchange 交换器
     * @param route 路由键
     * @param message 消息
     * @param properties
     */
    public void sendMsg(String exchange, String routingKey, String message, MessageProperties properties){
        /**
         * 设置生产者消息publish-confirm回调函数
         */
        this.rabbitTemplate.setConfirmCallback(confirmCallback);
        /**
         * 设置消息退回回调函数
         */
        this.rabbitTemplate.setReturnCallback(returnCallback);
        /**
         * 新增消息转换完成后、发送之前的扩展点
         */
        this.rabbitTemplate.setBeforePublishPostProcessors(messagePostProcessor);

        try {
            if(null == properties){
                properties = new MessageProperties();
            }
            /**
             * 设置消息唯一标识
             */
            properties.setMessageId(UUID.randomUUID().toString());
            /**
             * 创建消息包装对象
             */
            Message msg = MessageBuilder.withBody(message.getBytes()).andProperties(properties).build();
            /**
             * 将消息主题和属性封装在Message类中
             */
            Message returnedMessage = MessageBuilder.withBody(message.getBytes()).build();
            /**
             * 相关数据
             */
            CorrelationData correlationData = new CorrelationData();
            /**
             * 消息ID，全局唯一
             */
            correlationData.setId(msg.getMessageProperties().getMessageId());

            /**
             * 设置此相关数据的返回消息
             */
            correlationData.setReturnedMessage(returnedMessage);
            /**
             * 如果msg是org.springframework.amqp.core.Message对象的实例，则直接返回，否则转化为Message对象
             */
            this.rabbitTemplate.convertAndSend(exchange, routingKey, msg, correlationData);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

```

延迟队列消费者：

```java
package com.yaomy.control.rabbitmq.amqp.delay;

import com.rabbitmq.client.Channel;
import RabbitDelayConfig;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *  RabbitMQ消息消费者（延迟队列）
 * @since 1.0
 */
@SuppressWarnings("all")
@Component
public class RabbitDelayReceiver {
    /**
     *
     * @param channel 信道
     * @param message 消息
     * @throws Exception
     */
    @RabbitListener(queues = RabbitDelayConfig.DELAY_TEST_QUEUE)
    public void onMessage(Channel channel, Message message) throws Exception {
        System.out.println("--------------------------------------");
        System.out.println("消费端Payload: " + message.getPayload()+"-ID:"+message.getHeaders().getId()+"-messageId:"+message.getHeaders());
        Long deliveryTag = (Long)message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        //手工ACK,获取deliveryTag
        channel.basicAck(deliveryTag, false);
    }

    /**
     *
     * @param channel 信道
     * @param message 消息
     * @throws Exception
     */
    @RabbitListener(queues = RabbitDelayConfig.DELAY_TEST_QUEUE)
    public void onMessage(Channel channel, org.springframework.amqp.core.Message message) throws Exception {
        System.out.println("--------------------------------------");
        System.out.println("消费端Payload: " + new String(message.getBody())+"-messageId:"+message.getMessageProperties().getMessageId());
        message.getMessageProperties().getHeaders().forEach((key, value)->{
            System.out.println("header=>>"+key+"="+value);
        });
        Long deliveryTag = message.getMessageProperties().getDeliveryTag();
        //手工ACK,获取deliveryTag
        channel.basicAck(deliveryTag, false);
    }

    /**
     *
     * @param channel 信道
     * @param body 负载
     * @param amqp_messageId 消息唯一标识
     * @param headers 消息header
     * @throws Exception
     */
    //获取特定的消息
    @RabbitListener(queues = RabbitDelayConfig.DELAY_TEST_QUEUE)
    //@RabbitHandler
    public void handleMessage(Channel channel, @Payload byte[] body, @Header String amqp_messageId,  @Headers Map<String, Object> headers) throws Exception{
        System.out.println("====消费消息===amqp_messageId:"+amqp_messageId);
        headers.keySet().forEach((key)->{
            System.out.println("header=>>"+key+"="+headers.get(key));
        });
        System.out.println(new String(body));
        Long deliveryTag = NumberUtils.toLong(headers.get("amqp_deliveryTag").toString());
        /**
         * 手动Ack
         */
        channel.basicAck(deliveryTag, false);
    }

    /**
     *
     * @param channel 信道
     * @param body 负载
     * @param headers 消息header
     * @throws Exception
     */
    @RabbitListener(queues = RabbitDelayConfig.DELAY_TEST_QUEUE)
    //@RabbitHandler
    public void handleMessage(Channel channel, @Payload byte[] body, MessageHeaders headers) throws Exception{
        System.out.println("====消费消息===amqp_messageId:"+headers);
        headers.keySet().forEach((key)->{
            System.out.println("header=>>"+key+"="+headers.get(key));
        });
        System.out.println(new String(body));
        Long deliveryTag = NumberUtils.toLong(headers.get("amqp_deliveryTag").toString());
        /**
         * 手动Ack
         */
        channel.basicAck(deliveryTag, false);
    }
}

```

##### 检查消息是否延期

> To check if a message was delayed, use the `getReceivedDelay()` method on the `MessageProperties`. It is a separate
> property to avoid unintended propagation to an output message generated from an input message.

##### 查看已发送到exchange的延迟消息数量：

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200103112101153.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9taW5neWFuZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200103111944997.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9taW5neWFuZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

##### 延迟消息插件优点

- 不需要为延迟消息单独创建单独的路由、交换器、队列

##### 延迟消息插件的缺点

- 不支持对已发送消息进行管理，只能在Web管理页面查看发送的数量DM
- 集群中只有一个副本（保存在当前节点下的Mnesia表中），如果节点不可用或关闭插件会丢失消息
- 目前该插件只支持disk节点，不支持ram节点
- 性能比原生的差一点（普通的Exchange收到消息后直接路由到队列，而延迟队列需要判断消息是否过期，未过期的需要保存在表中，时间到了再捞出来路由）

参考：

1. https://www.rabbitmq.com/blog/2015/04/16/scheduling-messages-with-rabbitmq/
2. https://www.rabbitmq.com/community-plugins.html
3. https://docs.spring.io/spring-amqp/docs/2.1.7.BUILD-SNAPSHOT/reference/html/#delayed-message-exchange

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service)

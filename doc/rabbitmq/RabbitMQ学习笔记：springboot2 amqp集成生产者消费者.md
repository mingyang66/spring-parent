### RabbitMQ学习笔记：springboot2 amqp集成生产者消费者

##### 1.引入依赖

```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
```

配置application.properties文件：

```
spring.rabbitmq.virtual-host=/
#spring.rabbitmq.host=localhost
spring.rabbitmq.addresses=172.30.67.122:5672,172.30.67.122:5673,172.30.67.122:5674
#spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=admin

#producer
spring.rabbitmq.publisher-confirms=true
spring.rabbitmq.publisher-returns=true
spring.rabbitmq.template.mandatory=true

#consumer
##手工确认消费者消费的消息
spring.rabbitmq.listener.simple.acknowledge-mode=manual
##设置Qos,即RabbitMQ服务器每次推送给消费者未ack消息的个数
spring.rabbitmq.listener.simple.prefetch=1
```

##### 2.定义队列、交换器、路由之间的绑定关系

```java
package com.yaomy.control.rabbitmq.amqp.config;

import com.google.common.collect.Maps;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @Description: RabbitMQ生产者交换器、绑定、队列声明
 * @Version: 1.0
 */
@SuppressWarnings("all")
@Configuration
public class RabbitConfig {
    /**
     * 声明队列
     */
    @Bean
    public Queue topicQueue(){
        Map<String, Object> args = Maps.newHashMap();
        /**
         * 设置消息发送到队列之后多久被丢弃，单位：毫秒
         */
        //args.put("x-message-ttl", 60000);
        /**
         * 定义优先级队列，消息最大优先级为15，优先级范围为0-15，数字越大优先级越高
         */
        args.put("x-max-priority", 15);
        /**
         * 设置持久化队列
         */
        return QueueBuilder.durable("test_queue2").withArguments(args).build();
    }

    /**
     * 声明Topic类型交换器
     */
    @Bean
    public TopicExchange topicExchange(){
        TopicExchange exchange = new TopicExchange("test_exchange2");
        return exchange;
    }

    /**
     * Topic交换器和队列通过bindingKey绑定
     * @return
     */
    @Bean
    public Binding bindingTopicExchangeQueue(){
        return BindingBuilder.bind(topicQueue()).to(topicExchange()).with("*.topic.*");
    }
}

```

##### 3.定义生产者

```java
package com.yaomy.control.rabbitmq.amqp;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.PublisherCallbackChannel;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @Description: RabbitMQ生产者
 * @ProjectName: spring-parent
 * @Version: 1.0
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
     * 发送消息
     * @param exchange 交换器
     * @param route 路由键
     * @param message 消息
     * @param properties
     */
    public void sendMsg(String exchange, String routingKey, String message, MessageProperties properties){
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
             * 设置生产者消息publish-confirm回调函数
             */
            this.rabbitTemplate.setConfirmCallback(confirmCallback);
            /**
             * 设置消息退回回调函数
             */
            this.rabbitTemplate.setReturnCallback(returnCallback);
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
            this.rabbitTemplate.convertAndSend(exchange, routingKey, msg, new MessagePostProcessor() {
                /**
                 * 消息后置处理器，消息在转换成Message对象之后调用，可以用来修改消息中的属性、header
                 */
                @Override
                public Message postProcessMessage(Message message) throws AmqpException {
                    MessageProperties msgProperties = message.getMessageProperties();
                    /**
                     * 设置消息发送到队列之后多久被丢弃，单位：毫秒
                     * 此种方案需要每条消息都设置此属性，比较灵活；
                     * 还有一种方案是在声明队列的时候指定发送到队列中的过期时间；
                     * * Queue queue = new Queue("test_queue2");
                     * * queue.getArguments().put("x-message-ttl", 10000);
                     * 这两种方案可以同时存在，以值小的为准
                     */
                    //msgProperties.setExpiration("10000");
                    /**
                     * 设置消息的优先级
                     */
                    msgProperties.setPriority(9);
                    /**
                     * 设置消息发送到队列中的模式，持久化|非持久化（只存在于内存中）
                     */
                    msgProperties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                    return message;
                }
            }, correlationData);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

```

##### 4.消费者

@RabbitListener注解标注指定的方法监听指定的队列、也可以标注在类上结合@RabbitHandler使用；监听方法可以使用多种参数接收消息，查看源码可以看到是允许六种参数：

```
* Annotated methods are allowed to have flexible signatures similar to what
 * {@link MessageMapping} provides, that is
 * <ul>
 * <li>{@link com.rabbitmq.client.Channel} to get access to the Channel</li>
 * <li>{@link org.springframework.amqp.core.Message} or one if subclass to get access to
 * the raw AMQP message</li>
 * <li>{@link org.springframework.messaging.Message} to use the messaging abstraction
 * counterpart</li>
 * <li>{@link org.springframework.messaging.handler.annotation.Payload @Payload}-annotated
 * method arguments including the support of validation</li>
 * <li>{@link org.springframework.messaging.handler.annotation.Header @Header}-annotated
 * method arguments to extract a specific header value, including standard AMQP headers
 * defined by {@link org.springframework.amqp.support.AmqpHeaders AmqpHeaders}</li>
 * <li>{@link org.springframework.messaging.handler.annotation.Headers @Headers}-annotated
 * argument that must also be assignable to {@link java.util.Map} for getting access to
 * all headers.</li>
 * <li>{@link org.springframework.messaging.MessageHeaders MessageHeaders} arguments for
 * getting access to all headers.</li>
```

示例代码：

```java
package com.yaomy.control.rabbitmq.amqp;

import com.rabbitmq.client.Channel;
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
 * @Description: RabbitMQ消息消费者
 * @Version: 1.0
 */
@SuppressWarnings("all")
@Component
public class RabbitReceiver {
    /**
     *
     * @param channel 信道
     * @param message 消息
     * @throws Exception
     */
    @RabbitListener(queues = "test_queue2")
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
    @RabbitListener(queues = "test_queue2")
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
    @RabbitListener(queues = "test_queue2")
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
    @RabbitListener(queues = "test_queue2")
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

使用MessageHeaders参数类型接收header示例如下：

```
header=>>amqp_receivedDeliveryMode=PERSISTENT
header=>>amqp_receivedExchange=test_exchange2
header=>>amqp_deliveryTag=1
header=>>amqp_consumerQueue=test_queue2
header=>>amqp_redelivered=false
header=>>priority=9
header=>>amqp_receivedRoutingKey=test.topic.key
header=>>number=12345
header=>>spring_listener_return_correlation=34b20ba3-9fb0-49e5-b57e-5484e773e9b3
header=>>send_time=2019-12-27 16:08:55
header=>>spring_returned_message_correlation=cba66847-5c31-4ecb-bf97-07ba65238b9a
header=>>amqp_messageId=cba66847-5c31-4ecb-bf97-07ba65238b9a
header=>>id=b58e6adb-e5b2-9513-45c5-8376311cf82f
header=>>amqp_consumerTag=amq.ctag-hPKHjdTdj0NY4awa4bFkAA
header=>>contentType=application/octet-stream
header=>>timestamp=1577434135312
```

> 其headers中有一个id属性，这个属性是消费者接收消息new MessageHeaders的时候生成的随机UUID,不可以作为整个系统中消息的唯一标识，只可以作为消费端的唯一标识。


GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service)
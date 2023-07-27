### RabbitMQ学习笔记：惰性队列（Lazy Queues）

##### 理论说明

RabbitMQ从3.6.0开始引入了惰性队列（Lazy
Queue）的概念。惰性队列会尽可能的将消息存入磁盘中，而在消费者消费到相应的消息时才会被加载到内存中，它的一个重要设计目标是能够支持更长的队列，即支持更多的消息存储。当消费者由于各种各样的原因（比如消费者下线、跌机、或者由于维护而关闭等）致使长时间不能消费消息而造成堆积时，惰性队列就很必要了。

默认情况下，当生产者将消息发送到RabbitMQ的时候，队列中的消息会尽可能地存储在内存之中，这样可以更加快速地将消息发送给消费者。即使是持久化的消息，在被写入磁盘的同时也会在内存中驻留一份备份。当RabbitMQ需要释放内存的时候，会将内存中的消息换页至磁盘中，这个操作会耗费较长的时间，也会阻塞队列的操作，进而无法接收新的消息。虽然RabbitMQ的开发者们一直在升级相关的算法，但是效果始终不太理想，尤其是在
消息量特别大的时候。

惰性队列会将接收到的消息直接存入文件系统，而不管是持久化的或者是非持久化的，这样可以减少内存的消耗，但是会增加I/O的使用，如果消息是持久化的，那么这样的I/O操作不可避免，惰性队列和持久化的消息可谓是“最佳拍档”。注意如果惰性队列中存储的是非持久化的消息，内存的使用率会一直很稳定，但是重启之后消息一样会丢失。

队列具备两种模式：default和lazy。默认的为default模式，在3.6.0的版本无需做任何变更。lazy模式即为惰性队列的模式，可以通过调用channel.queueDeclare方法的时候在参数中设置，也可以通过Policy的方式设置，如果一个队列同时使用这两种方式设置，那么Policy的方式具备更高的优先级。如果要通过声明的方式改变已有队列的模式，那么只能先删除队列，然后再重新声明一个新的。

下面的示例展示了如何声明一个惰性队列：

```java
  Map<String, Object> args = new HashMap<String, Object>();
  args.put("x-queue-mode", "lazy");
  channel.queueDeclare("myqueue", false, false, false, args);
```

使用Policy设置一个队列为惰性队列：
| rabbitmqctl | rabbitmqctl set_policy Lazy "^lazy-queue$" '{"queue-mode":"lazy"}' --apply-to queues |
| -------------------- | ------------------------------------------------------------ |
| rabbitmqctl(Windows) | rabbitmqctl set_policy Lazy "^lazy-queue$" "{""queue-mode"":""lazy""}" --apply-to queues |

惰性队列和普通队列相比，只有很小的内存开销。这里很难对每种情况给出一个具体的数值，但是我们可以类比一下：发送1千万条消息，每条消息的大小为1KB，并且此时没有任何的消费者，那么普通队列会消耗1.2GB内存，而惰性队列只能消耗1.5MB的内存。

根据官方测试数据显示，对于普通队列，如果要发送1千万条消息，需要耗费801秒，平均发送速度约为13000条/秒。如果使用惰性队列，那么发送同样多的消息时，耗时是421秒，平均发送速度约为24000条/秒。出现性能偏差的原因是普通队列会由于内存不足而不得不将消息换页至磁盘。如果有消费者消费时，惰性队列会耗费将近40MB的空间来发送消息，对于一个
消费者的情况，平均的消费速度约为14000条/秒。

如果要将普通队列转变为惰性队列，我们需要忍受同样的性能损耗，首先需要将缓存中的消息换页至磁盘中，然后才能接收新的消息。反之，当将一个惰性队列转变为一个普通队列的时候，和恢复一个队列执行同样的操作，会将磁盘中的消息批量的导入到内存中。

##### 示例

##### 1.声明队列、交换器、绑定关系

```java
package com.yaomy.control.rabbitmq.amqp.lazy.config;

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
public class RabbitLazyConfig {
    public static final String LAZY_TOPIC_EXCHANGE = "lazy.topic.exchange";
    public static final String LAZY_TOPIC_QUEUE = "lazy_topic_queue";
    public static final String LAZY_TOPIC_ROUTING_KEY = "*.topic.*";
    /**
     * 声明队列
     */
    @Bean
    public Queue topicLazyQueue(){
        Map<String, Object> args = Maps.newHashMap();

        args.put("x-queue-mode", "lazy");
        /**
         * 设置持久化队列
         */
        return QueueBuilder.durable(LAZY_TOPIC_QUEUE).withArguments(args).build();
    }


    /**
     * 声明Topic类型交换器
     */
    @Bean
    public TopicExchange topicLazyExchange(){
        TopicExchange exchange = new TopicExchange(LAZY_TOPIC_EXCHANGE);
        return exchange;
    }

    /**
     * Topic交换器和队列通过bindingKey绑定
     * @return
     */
    @Bean
    public Binding bindingTopicLazyExchangeQueue(){
        return BindingBuilder.bind(topicLazyQueue()).to(topicLazyExchange()).with(LAZY_TOPIC_ROUTING_KEY);
    }

}

```

##### 2.生产者

```java
package com.yaomy.control.rabbitmq.amqp.lazy;

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
public class RabbitLazySender {
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

##### 3.消费者

```java
package com.yaomy.control.rabbitmq.amqp.lazy;

import com.rabbitmq.client.Channel;
import RabbitLazyConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 *  RabbitMQ消息消费者
 * @since 1.0
 */
@SuppressWarnings("all")
@Component
public class RabbitLazyReceiver {
    /**
     *
     * @param channel 信道
     * @param message 消息
     * @throws Exception
     */
    @RabbitListener(queues = RabbitLazyConfig.LAZY_TOPIC_QUEUE)
    public void onMessage(Channel channel, Message message) throws Exception {
        System.out.println("--------------------------------------");
        System.out.println("消费端Payload: " + message.getPayload()+"-ID:"+message.getHeaders().getId()+"-messageId:"+message.getHeaders());
        Long deliveryTag = (Long)message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        //手工ACK,获取deliveryTag
        channel.basicAck(deliveryTag, false);
    }
}

```

参考：https://www.rabbitmq.com/lazy-queues.html
GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service)
### RabbitMQ基本使用详解及示例

#### RabbitMQ简介

AMQP,即Advanced Message Queuing Protocol,高级消息队列协议，是应用层协议的一个开放标准，为面向消息的中间件设计。消息中间件主要用于组件之间的解耦，消息的发送者无需知道消息使用者的存在，反之亦然。AMQP的主要特征是面向消息、队列、路由（包括点对点和发布订阅）、可靠性、安全。RabbitMQ是一个开源的AMQP实现，服务端用Erlang语言编写，支持多种客户端，如：Python、Java、Ruby、PHP、C#、Javascript、Go、Elixir、Objective-C、Swift、Spring AMQP；用于在分布式系统中存储转发消息，在易用性、扩展性、高可用性等方面表现不俗。下面主要介绍RabbitMQ的一些基础概念，了解了这些概念，是使用好RabbitMQ的基础。

#### ConnectionFactory、Connection、Channel

ConnectionFactory、Connection、Channel都是RabbitMQ对外提供的API中最基本的对象，Connection是RabbitMQ的Socket连接，它封装了socket协议相关部分逻辑。ConnectionFactory是Connection的工厂类。Channel是我们与RabbitMQ打交道的最重要的一个接口，我们的大部分业务操作是在Channel这个接口中完成的，包括定义Queue、定义Exchange、绑定Queue与Exchange、发布消息等。

#### 队列Queue

Queue(队列)是RabbitMQ的内部对象，用于存储消息；RabbitMQ中的消息都只能存储在Queue中，生产者生产的消息最终投递到Queue中，消费者可以从Queue中获取消息并消费；多个消费者可以订阅同一个Queue，这时Queue中的消息会被平均分摊给多个消费者进行处理，而不是每个消费者都收到所有的消息并处理。

#### Message acknowledgment

在实际应用中，可能会出现消费者接收到Queue中的消息，但没有处理完成就宕机（或出现其他意外）的情况，这种情况可能会导致消息丢失。为了避免这种情况发生，我们可以要求消费者在消费完成消息后发送一个回执给RabbitMQ,RabbitMQ收到消息回执（Message acknowlegment）后才将该消息从Queue中移除；如果RabbitMQ没有收到回执并检测到消费者的RabbitMQ连接断开，则RabbitMQ会将该消息发送给其它消费者（如果存在多个消费者）进行处理。这里不存在timeout概念，一个消费者处理消息时间再长也不会导致该消息被发送给其它消费者，除非它的RabbitMQ连接断开。

#### Message Durability

如果我们希望即使在RabbitMQ服务重启的情况下，也不丢失消息，我们可以将Queue与Message都设置为可持久化的（durability）,这样可以保证绝大部分情况下我们的RabbitMQ消息不会丢失。但是依然解决不了小概率丢失事件的发生（比如RabbitMQ服务器已经接收到生产者的消息，但还没来的及持久化该消息时RabbitMQ服务器就断电了），如果我们要对这种小概率事件也管理起来，那么我们要用到事务，本节就不对事务做详细讲解。



定义一个持久化队列，但是RabbitMQ不允许你使用不同的参数重新定义一个已经存在的队列，否则会报异常

```
boolean durable = true;
channel.queueDeclare("task_queue", durable, false, false, null);
```

上面我们确定task_queue队列（queue）不会丢失消息，在服务器重启的时候我们需要设置消息持久化来保证消息不会丢失，通过设置 MessageProperties  （ BasicProperties 的子类）的值为PERSISTENT_TEXT_PLAIN；

```
import com.rabbitmq.client.MessageProperties;

channel.basicPublish("", "task_queue",
            MessageProperties.PERSISTENT_TEXT_PLAIN,
            message.getBytes());
```



#### Prefetch count

前面我们讲到如果有多个消费者同时订阅同一个Queue中的消息，Queue中的消息会被平摊给多个消费者。这时如果每个消息的处理时间不同，就有可能导致某些消费者一直在忙，而另外一些消费者很快就处理完手头工作并一直空闲的情况；

我们可以通过设置Prefetch count来限制Queue每次发送给每个消费者的消息数，比如我们设置prefetchCount=1,这就是告诉RabbitMQ不要在同一时间发送多于一个消息给消费者，或者，换句话说，在消费者未处理完成或应答上一个消息之前不要分派下一个新的消息。

```
int prefetchCount = 1;
channel.basicQos(prefetchCount);
```



#### Exchange

之前我们说生产者将消息投递给Queue,实际上这种情况是永远也不会发生的。实际的情况是生产者将消息发送到Exchange(交换器),由Exchange将消息路由到一个或多个Queue中（或者丢弃）。

#### Binding

RabbitMQ中通过Binding将Exchange与Queue关联起来，这样RabbitMQ就知道如何正确地将消息路由到指定的Queue了。

#### Binding Key

在绑定（Binding）Exchange与Queue的同时，一般会指定一个bingding key;消费者将消息发送给Exchange时，一般会指定一个routing key;当binding key与routing key相匹配时，消息将会被路由到对应的Queue中。

#### Exchange Types

RabbitMQ常用的Exchange Type有fanout、direct、topic、headers这四种，而且RabbitMQ客户端上展示的也只有这四种。

- fanout

fanout类型的Exchange路由规则非常简单，它会把所有发送到该Exchange的消息路由到所有的与它绑定的Queue中。

- direct

direct类型的Exchange路由规则也很简单，它会把消息路由到哪些binding key与routing key完全匹配的Queue中。

- topic

direct类型的Exchange路由规则是完全匹配binding key与routing key,但这种严格的匹配方式在很多情况下不能满足实际业务的需求。topic类型的Exchange在匹配规则上进行了扩展，它与direct类型的Exchange相似，也是将消息路由到binding key与routing key相匹配的Queue中，但是匹配规则不同：

routing key是一个句点号"."分割的字符串（我们讲被句点号“.”分割开的每一段独立的字符串成为一个单词），如：stock.user.rabbit；

binding key与routing key一样也是句点号“.”分割的字符串；

binding key中可以存在两种特殊的字符“*”与“#”，用于做模糊匹配，其中“*”用于匹配一个单词，“#”用于匹配多个单词（可以是零个）

- headers

headers类型的Exchange不依赖于routing key与binding key的匹配规则来路由消息，而是根据发送的消息内容中的headers属性进行匹配 在绑定Queue与Exchange时指定一组键值对；当消息发送到Exchange时，RabbitMQ会取到该消息的headers（也是一个键值对的形式），对比其中的键值对是否完全匹配Queue与Exchange绑定时指定的键值对；如果完全匹配则消息会路由到该Queue，否则不会路由到该Queue。 

#### Direct类型Exchange发送消息示例

```
package com.yaomy.control.rabbitmq.direct;

import com.rabbitmq.client.*;

import java.util.concurrent.TimeUnit;

/**
 * @Description: Direct类型交换器发送消息客户端
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

```



#### Direct类型Exchange接收消息

```
package com.yaomy.control.rabbitmq.direct;

import com.rabbitmq.client.*;

import java.util.concurrent.TimeUnit;

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
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e){

            }
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

```


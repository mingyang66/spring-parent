### RabbitMQ之basicConsume、basicCancel、basicPublish等方法详解

> 最新版5.7.3提供了20个basicConsume消费方法，这些方法是由服务端主动PUSH消息过来，方法接收到消息后进行处理；而每个方法处理接收到的消息相差不大，下面详细介绍每个方法的参数详情；

##### 1.String basicConsume(String queue, boolean autoAck, DeliverCallback deliverCallback, CancelCallback cancelCallback)

```java
   /**
         * 启动一个消费者，并返回服务端生成的消费者标识
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * deliverCallback： 当一个消息发送过来后的回调接口
         * cancelCallback：当一个消费者取消订阅时的回调接口;取消消费者订阅队列时除了使用{@link Channel#basicCancel}之外的所有方式都会调用该回调方法
         * @return 服务端生成的消费者标识
         */
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
            System.out.println("调用"+consumerTag);
        });
```

##### 2.String basicConsume(String queue, boolean autoAck, Consumer callback)

```java
  /**
         * 启动一个消费者，并返回服务端生成的消费者标识
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * callback: 消费者对象的回调接口
         * @return 服务端生成的消费者标识
         */
        channel.basicConsume(QUEUE_NAME, true, new DefaultConsumer(channel){});
```

> 使用接口com.rabbitmq.client.Consumer的实现类com.rabbitmq.client.DefaultConsumer实现自定义消息监听器，接口中有多个不同的方法可以根据自己系统的需要实现；

##### 3.String basicConsume(String queue, DeliverCallback deliverCallback, CancelCallback cancelCallback, ConsumerShutdownSignalCallback shutdownSignalCallback)

```java
/**
         * queue:队列名
         * deliverCallback： 当一个消息发送过来后的回调接口
         * cancelCallback：当一个消费者取消订阅时的回调接口;取消消费者订阅队列时除了使用{@link Channel#basicCancel}之外的所有方式都会调用该回调方法
         * shutdownSignalCallback: 当channel/connection 关闭后回调
         * @return 服务端生成的消费者标识
         */
        channel.basicConsume(QUEUE_NAME, deliverCallback, consumerTag -> {}, (consumerTag, sig) -> {});
```

##### 4.String basicConsume(String queue, DeliverCallback deliverCallback, ConsumerShutdownSignalCallback shutdownSignalCallback)

```java
/**
         * queue:队列名
         * deliverCallback： 当一个消息发送过来后的回调接口
         * shutdownSignalCallback: 当channel/connection 关闭后回调
         * @return 服务端生成的消费者标识
         */
        channel.basicConsume(QUEUE_NAME, deliverCallback, (consumerTag, sig) -> {});
```

##### 5.String basicConsume(String queue, DeliverCallback deliverCallback, CancelCallback cancelCallback)

```java
 /**
         * queue:队列名
         * deliverCallback： 当一个消息发送过来后的回调接口
         * cancelCallback：当一个消费者取消订阅时的回调接口;取消消费者订阅队列时除了使用{@link Channel#basicCancel}之外的所有方式都会调用该回调方法
         * @return 服务端生成的消费者标识
         */
        channel.basicConsume(QUEUE_NAME, deliverCallback, consumerTag -> {});
```

##### 6.String basicConsume(String queue, Consumer callback)

```java
  /**
         * queue:队列名
         * callback: 消费者对象的回调接口
         * @return 服务端生成的消费者标识
         */
        channel.basicConsume(QUEUE_NAME, new DefaultConsumer(channel){});
```

##### 7.String basicConsume(String queue, boolean autoAck, DeliverCallback deliverCallback, ConsumerShutdownSignalCallback shutdownSignalCallback)

```java
 /**
         * 启动一个消费者，并返回服务端生成的消费者标识
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * deliverCallback： 当一个消息发送过来后的回调接口
         * shutdownSignalCallback: 当channel/connection 关闭后回调
         * @return 服务端生成的消费者标识
         */
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, (consumerTag, sig)->{
            //consumerTag服务端生成的消费者标识
            //sig(ShutdownSignalException):说明关闭的原因
        });
```

##### 8.String basicConsume(String queue, boolean autoAck, DeliverCallback deliverCallback, CancelCallback cancelCallback, ConsumerShutdownSignalCallback shutdownSignalCallback)

```java
 /**
         * 启动一个消费者，并返回服务端生成的消费者标识
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * deliverCallback： 当一个消息发送过来后的回调接口
         * cancelCallback：当一个消费者取消订阅时的回调接口;取消消费者订阅队列时除了使用{@link Channel#basicCancel}之外的所有方式都会调用该回调方法
         * shutdownSignalCallback: 当channel/connection 关闭后回调
         * @return 服务端生成的消费者标识
         */
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag->{
            //consumerTag:服务端小费制标识
        }, (consumerTag, sig) -> {
            //consumerTag服务端生成的消费者标识
            //sig(ShutdownSignalException):说明关闭的原因
        });
```

##### 9.String basicConsume(String queue, boolean autoAck, Map<String, Object> arguments, Consumer callback)

```java
  /**
         * 启动一个消费者，并返回服务端生成的消费者标识
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * arguments: 消费的一组参数
         * callback：消费者对象接口
         * @return 服务端生成的消费者标识
         */
        channel.basicConsume(QUEUE_NAME, true, arguments, new DefaultConsumer(channel){
          //根据需要实现对应的方法
        });
```

##### 10.String basicConsume(String queue, boolean autoAck, Map<String, Object> arguments, DeliverCallback deliverCallback, CancelCallback cancelCallback)

```java
 /**
         * 启动一个消费者，并返回服务端生成的消费者标识
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * arguments: 消费的一组参数
         * deliverCallback： 当一个消息发送过来后的回调接口
         * cancelCallback：当一个消费者取消订阅时的回调接口;取消消费者订阅队列时除了使用{@link Channel#basicCancel}之外的所有方式都会调用该回调方法
         * @return 服务端生成的消费者标识
         */
        channel.basicConsume(QUEUE_NAME, true, arguments, deliverCallback, consumerTag -> {});
```

##### 11.String basicConsume(String queue, boolean autoAck, Map<String, Object> arguments, DeliverCallback deliverCallback, ConsumerShutdownSignalCallback shutdownSignalCallback)

```java
/**
         * 启动一个消费者，并返回服务端生成的消费者标识
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * arguments: 消费的一组参数
         * deliverCallback： 当一个消息发送过来后的回调接口
         * shutdownSignalCallback: 当channel/connection 关闭后回调
         * @return 服务端生成的消费者标识
         */
        channel.basicConsume(QUEUE_NAME, true, arguments, deliverCallback, (consumerTag, sig) -> {});
```

##### 12.String basicConsume(String queue, boolean autoAck, Map<String, Object> arguments, DeliverCallback deliverCallback, CancelCallback cancelCallback, ConsumerShutdownSignalCallback shutdownSignalCallback)

```java
 /**
         * 启动一个消费者，并返回服务端生成的消费者标识
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * arguments: 消费的一组参数
         * deliverCallback： 当一个消息发送过来后的回调接口
         * cancelCallback：当一个消费者取消订阅时的回调接口;取消消费者订阅队列时除了使用{@link Channel#basicCancel}之外的所有方式都会调用该回调方法
         * shutdownSignalCallback: 当channel/connection 关闭后回调
         * @return 服务端生成的消费者标识
         */
        channel.basicConsume(QUEUE_NAME, true, arguments, deliverCallback, consumerTag -> {}, (consumerTag, sig) -> {});
```

##### 13.String basicConsume(String queue, boolean autoAck, String consumerTag, Consumer callback)

```java
 /**
         * 启动一个消费者，并返回服务端生成的消费者标识
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * consumerTag: 客户端生成的用于建立上线文的使用者标识
         * callback：消费者对象接口
         * @return 与消费者关联的消费者标识
         */
        channel.basicConsume(QUEUE_NAME, true, ctag, new DefaultConsumer(channel){
            //根据需要实现具体的方法
        });
```

##### 14.String basicConsume(String queue, boolean autoAck, String consumerTag, DeliverCallback deliverCallback, CancelCallback cancelCallback)

```java
  /**
         * 启动一个消费者，并返回服务端生成的消费者标识
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * consumerTag:客户端生成的一个消费者标识
         * deliverCallback： 当一个消息发送过来后的回调接口
         * cancelCallback：当一个消费者取消订阅时的回调接口;取消消费者订阅队列时除了使用{@link Channel#basicCancel}之外的所有方式都会调用该回调方法
         * @return 与消费者关联的消费者标识
         */
        channel.basicConsume(QUEUE_NAME, true, ctag, deliverCallback, consumerTag -> {});
```

##### 15.String basicConsume(String queue, boolean autoAck, String consumerTag, DeliverCallback deliverCallback, ConsumerShutdownSignalCallback shutdownSignalCallback)

```java
/**
         * 启动一个消费者，并返回服务端生成的消费者标识
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * consumerTag:客户端生成的一个消费者标识
         * deliverCallback： 当一个消息发送过来后的回调接口
         * shutdownSignalCallback: 当channel/connection 关闭后回调
         * @return 与消费者关联的消费者标识
         */
        channel.basicConsume(QUEUE_NAME, true, ctag, deliverCallback, (consumerTag, sig) -> {});
```

##### 16.String basicConsume(String queue, boolean autoAck, String consumerTag, DeliverCallback deliverCallback, CancelCallback cancelCallback, ConsumerShutdownSignalCallback shutdownSignalCallback)

```java
  /**
         * 启动一个消费者，并返回服务端生成的消费者标识
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * consumerTag:客户端生成的一个消费者标识
         * deliverCallback： 当一个消息发送过来后的回调接口
         * cancelCallback：当一个消费者取消订阅时的回调接口;取消消费者订阅队列时除了使用{@link Channel#basicCancel}之外的所有方式都会调用该回调方法
         * shutdownSignalCallback: 当channel/connection 关闭后回调
         * @return 与消费者关联的消费者标识
         */
        channel.basicConsume(QUEUE_NAME, true, ctag, deliverCallback, consumerTag -> {}, (consumerTag, sig) -> {});
```

##### 17.String basicConsume(String queue, boolean autoAck, String consumerTag, boolean noLocal, boolean exclusive, Map<String, Object> arguments, Consumer callback)

```java
 /**
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * consumerTag:客户端生成的一个消费者标识
         * nolocal:如果服务器不应将在此通道连接上发布的消息传递给此使用者，则为true;请注意RabbitMQ服务器上不支持此标记
         * exclusive: 如果是单个消费者，则为true
         * callback:消费者对象接口
         * @return 与消费者关联的消费者标识
         */
        channel.basicConsume(QUEUE_NAME, true, ctag, false, false, arguments, new DefaultConsumer(channel){
            //根据需求实现方法
        });
```

##### 18.String basicConsume(String queue, boolean autoAck, String consumerTag, boolean noLocal, boolean exclusive, Map<String, Object> arguments, DeliverCallback deliverCallback, CancelCallback cancelCallback)

```java
 /**
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * consumerTag:客户端生成的一个消费者标识
         * nolocal:如果服务器不应将在此通道连接上发布的消息传递给此使用者，则为true;请注意RabbitMQ服务器上不支持此标记
         * exclusive: 如果是单个消费者，则为true
         * arguments:消费的一组参数
         * deliverCallback： 当一个消息发送过来后的回调接口
         * cancelCallback：当一个消费者取消订阅时的回调接口;取消消费者订阅队列时除了使用{@link Channel#basicCancel}之外的所有方式都会调用该回调方法
         * @return 与消费者关联的消费者标识
         */
        channel.basicConsume(QUEUE_NAME, true, ctag, false, false, arguments, deliverCallback, consumerTag -> {});
```

##### 19.String basicConsume(String queue, boolean autoAck, String consumerTag, boolean noLocal, boolean exclusive, Map<String, Object> arguments, DeliverCallback deliverCallback, ConsumerShutdownSignalCallback shutdownSignalCallback)

```java
 /**
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * consumerTag:客户端生成的一个消费者标识
         * nolocal:如果服务器不应将在此通道连接上发布的消息传递给此使用者，则为true;请注意RabbitMQ服务器上不支持此标记
         * exclusive: 如果是单个消费者，则为true
         * arguments:消费的一组参数
         * deliverCallback： 当一个消息发送过来后的回调接口
         * shutdownSignalCallback: 当channel/connection 关闭后回调
         */
        channel.basicConsume(QUEUE_NAME, true, ctag, false, false, arguments, deliverCallback, (consumerTag, sig) -> {});
```

##### 20.String basicConsume(String queue, boolean autoAck, String consumerTag, boolean noLocal, boolean exclusive, Map<String, Object> arguments, DeliverCallback deliverCallback, CancelCallback cancelCallback, ConsumerShutdownSignalCallback shutdownSignalCallback)

```java
	 /**
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * consumerTag:客户端生成的一个消费者标识
         * nolocal:如果服务器不应将在此通道连接上发布的消息传递给此使用者，则为true;请注意RabbitMQ服务器上不支持此标记
         * exclusive: 如果是单个消费者，则为true
         * arguments:消费的一组参数
         * deliverCallback： 当一个消息发送过来后的回调接口
         * cancelCallback：当一个消费者取消订阅时的回调接口;取消消费者订阅队列时除了使用{@link Channel#basicCancel}之外的所有方式都会调用该回调方法
         * shutdownSignalCallback: 当channel/connection 关闭后回调
         */
        channel.basicConsume(QUEUE_NAME, true, ctag, false, false, arguments, deliverCallback, consumerTag -> {}, (consumerTag, sig) -> {});
```

##### 主动拉取队列中的一条消息

```java
GetResponse basicGet(String queue, boolean autoAck)
```

使用示例：

```java
        /**
         * 从消息队列中取出第一条消息；整个方法的执行过程是首先消费队列，然后检索第一条消息，然后再取消订阅
         */
        GetResponse response = channel.basicGet(QUEUE_NAME, true);
        System.out.println("消费者接收到的消息是："+new String(response.getBody(), "UTF-8"));
```

##### 取消消费者订阅

```java
/**
* 取消消费者对队列的订阅关系
* consumerTag:服务器端生成的消费者标识
**/
void basicCancel(String consumerTag)
```

##### basicQoc设置服务端每次发送给消费者的消息数量

```java
/**
* prefetchSize:服务器传送最大内容量（以八位字节计算），如果没有限制，则为0
* prefetchCount:服务器每次传递的最大消息数，如果没有限制，则为0；
* global:如果为true,则当前设置将会应用于整个Channel(频道)
**/
void basicQos(int prefetchSize, int prefetchCount, boolean global)
```

```java
/**
* prefetchCount:服务器每次传递的最大消息数，如果没有限制，则为0；
* global:如果为true,则当前设置将会应用于整个Channel(频道)
**/
void basicQos(int prefetchCount, boolean global)
```

```java
/**
* prefetchCount:服务器每次传递的最大消息数，如果没有限制，则为0；
**/
void basicQos(int prefetchCount)
```

##### Acknowledge(确认)收到一个或者多个消息

```java
/**
* 消费者确认收到一个或者多个消息
* deliveryTag：服务器端向消费者推送消息，消息会携带一个deliveryTag参数，也可以成此参数为消息 * 的唯一标识，是一个递增的正整数
* multiple：true表示确认所有消息，包括消息唯一标识小于等于deliveryTag的消息，false只确认 * * deliveryTag指定的消息
**/
void basicAck(long deliveryTag, boolean multiple)
```

```java
/**
* 要求代理重新发送未确认的消息
* requeue:如果为true,消息将会重新入队，可能会被发送给其它的消费者；如果为false,消息将会发送给* 相同的消费者
**/
Basic.RecoverOk basicRecover(boolean requeue)
```

```java
/**
* 要求代理重新发送未确认的消息;消息将会重新排队，并且可能会发送给其它的消费者
**/
Basic.RecoverOk basicRecover()
```

##### 拒绝消息

```java
/**
* 拒绝接收到的一个或者多个消息
* deliveryTag：接收到消息的唯一标识
* multiple: true表示拒绝所有的消息，包括提供的deliveryTag；false表示仅拒绝提供的deliveryTag
* requeue：true 表示拒绝的消息应重新入队，而不是否丢弃
*/
void basicNack(long deliveryTag, boolean multiple, boolean requeue)
```

```java
/**
* 拒绝接收到的一个或者多个消息
* deliveryTag：接收到消息的唯一标识
* requeue：true 表示拒绝的消息应重新入队，而不是否丢弃
*/
void basicReject(long deliveryTag, boolean requeue) 
```

##### 发送消息

```java
/**
* exchange:要将消息发送到的Exchange(交换器)
* routingKey:路由Key
* mandatory:true 如果mandatory标记被设置
* immediate: true 如果immediate标记被设置，注意：RabbitMQ服务端不支持此标记
* props:其它的一些属性，如：{@link MessageProperties.PERSISTENT_TEXT_PLAIN}
* body:消息内容
**/
void basicPublish(String exchange, String routingKey, boolean mandatory, boolean immediate, BasicProperties props, byte[] body)
```

```java
/**
* 发布消息
* 发布到不存在的交换器将导致信道级协议异常，该协议关闭信道，
* exchange: 要将消息发送到的交换器
* routingKey: 路由KEY
* mandatory:true 如果mandatory标记被设置
* props: 消息的其它属性，如：路由头等
* body: 消息体
*/
void basicPublish(String exchange, String routingKey, boolean mandatory, BasicProperties props, byte[] body)
```

```java
/**
* 发布消息
* 发布到不存在的交换器将导致信道级协议异常，该协议关闭信道，
* exchange: 要将消息发送到的交换器
* routingKey: 路由KEY
* props: 消息的其它属性，如：路由头等
* body: 消息体
*/
void basicPublish(String exchange, String routingKey, BasicProperties props, byte[] body)
```

GitHub地址：[https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E4%B9%8BbasicConsume%E3%80%81basicCancel%E3%80%81basicPublish%E7%AD%89%E6%96%B9%E6%B3%95%E8%AF%A6%E8%A7%A3.md](https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E4%B9%8BbasicConsume%E3%80%81basicCancel%E3%80%81basicPublish%E7%AD%89%E6%96%B9%E6%B3%95%E8%AF%A6%E8%A7%A3.md)

### RabbitMQ消费端CancelCallback回调接口详解

#### 1.RabbitMQ消费端消费消息时会在取消订阅时调用此回调接口

```java
        /**
         * 启动一个消费者，并返回服务端生成的消费者标识
         * queue:队列名
         * autoAck：true 接收到传递过来的消息后acknowledged（应答服务器），false 接收到消息后不应答服务器
         * deliverCallback： 当一个消息发送过来后的回调接口
         * cancelCallback：当一个消费者取消订阅时的回调接口;取消消费者订阅队列时除了使用{@link Channel#basicCancel}之外的所有方式都会调用该回调方法
         * @return 服务端生成的消费者标识
         */
        String consumerTag1 = channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
            System.out.println("调用"+consumerTag);
        });
```

#### 2.CancelCallback是一个函数式接口，具体的实现需要开发者自己实现：

```java
@FunctionalInterface
public interface CancelCallback {

    /**
     * Called when the consumer is cancelled for reasons <i>other than</i> by a call to
     * {@link Channel#basicCancel}. For example, the queue has been deleted.
     * See {@link Consumer#handleCancelOk} for notification of consumer
     * cancellation due to {@link Channel#basicCancel}.
     * @param consumerTag the <i>consumer tag</i> associated with the consumer
     * @throws IOException
     */
    void handle(String consumerTag) throws IOException;

}
```



当一个消费者取消订阅时的回调接口;取消消费者订阅队列时除了使用{@link Channel#basicCancel}之外的所有方式都会调用该回调方法

#### 3.删除消费者订阅的队列

```java

        /**
         * 删除一个队列，不管队列是否在使用还是上面有消息
         */
        channel.queueDelete(QUEUE_NAME);
        /**
         * 删除一个队列
         * queue：队列名
         * ifUnused：true 只有队列不在使用的时候才允许删除
         * ifEmpty：true 只有在队列是空的时候才允许删除
         * @return 返回一个队列已经成功删除的确认类
         */
        AMQP.Queue.DeleteOk ok = channel.queueDelete(QUEUE_NAME, true, true);
        /**
         * 清空队列的消息
         */
        channel.queuePurge(QUEUE_NAME);
```

GitHub地址：[https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E6%B6%88%E8%B4%B9%E7%AB%AFCancelCallback%E5%9B%9E%E8%B0%83%E6%8E%A5%E5%8F%A3%E8%AF%A6%E8%A7%A3.md](https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-rabbitmq-service/RabbitMQ%E6%B6%88%E8%B4%B9%E7%AB%AFCancelCallback%E5%9B%9E%E8%B0%83%E6%8E%A5%E5%8F%A3%E8%AF%A6%E8%A7%A3.md)

### RabbitMQ学习笔记：消息发布确认（Publisher Confirms）

> Publisher confirms是实现可靠发布的RabbitMQ扩展。在信道上启用Publisher confirm后，代理将异步确认客户端发布的消息，这意味着它们已经在服务器端处理。

##### 概括

在本教程中，我们将使用发布者确认（Publisher Confirms）来确保发布的消息已经安全到达Broker,我们将介绍几种使用发布者确认的策略并解释其优缺点。

##### 在信道上启用发布者确认

发布者确认是AMQP 0-9-1协议的RabbitMQ扩展，因此默认情况下未启用它们。发布者确认在信道级别使用confirmSelect方法启用。

```java
Channel channel = connection.createChannel（）;
channel.confirmSelect（）;
```

##### 策略一：分别发布消息

让我们使用确认发布最简单的方法开始，即发布消息并同步等待其确认：

```java
while (thereAreMessagesToPublish()) {
    byte[] body = ...;
    BasicProperties properties = ...;
    channel.basicPublish(exchange, queue, properties, body);
    // uses a 5 second timeout
    channel.waitForConfirmsOrDie(5_000);
}
```

在上面的示例中，我们像通常一样发布一条消息，并等待通过 Channel#waitForConfirmsOrDie(long)
方法对其进行确认。确认消息后，该方法立即返回。如果未在超时时间内确认该消息或该消息没有被确认（这意味着代理出于某种原因无法处理该消息），则该方法将引发异常。异常的处理通常包括记录错误消息或重试发送消息。

该策略非常简单，但也有一个主要缺点：由于消息的确认会阻止所有后续消息的发布，因此它会大大减慢发布速度。这种方法不会提供每秒超过数百条已发布消息的吞吐量。但是，对于某些应用程序来说这可能已经够用了。

> 发布者确认异步吗？
>
> 我们在一开始提到代理是异步地确认发布的消息，但是在第一个示例中，代码同步等待直到消息被确认。客户端实际上异步接收确认，并相应的取消对
> waitForConfirmsOrDie 的调用。把waitForConfirmsOrDie看作是一个同步助手，它依赖于引擎盖下的异步通知。

##### 策略2：批量发布消息（Publishing Messages in Batches）

为了改进前面的示例，我们可以发布一批消息，并等待整个批次被确认。以下示例使用了100个批次：

```java
int batchSize = 100;
int outstandingMessageCount = 0;
while (thereAreMessagesToPublish()) {
    byte[] body = ...;
    BasicProperties properties = ...;
    channel.basicPublish(exchange, queue, properties, body);
    outstandingMessageCount++;
    if (outstandingMessageCount == batchSize) {
        ch.waitForConfirmsOrDie(5_000);
        outstandingMessageCount = 0;
    }
}
if (outstandingMessageCount > 0) {
    ch.waitForConfirmsOrDie(5_000);
}
```

与等待单个消息相比，等待一批消息被确认可以极大地提高吞吐量（对于远程RabbitMQ节点，这最多可以达到20~
30次）。一个缺点是我们不知道发生故障时到底出了什么问题，因此我们可能必须将整个批处理保存在内存中以记录有意义的内容或重新发布消息。而且该解决方案仍然是同步的，因此它阻止了消息的发布。

##### 策略3：异步处理发布者确认（Handling Publisher Confirms Asynchronously）

代理异步确认已发布的消息，只需在客户端上注册一个回调即可收到这些确认的通知：

```java
Channel channel = connection.createChannel();
channel.confirmSelect();
channel.addConfirmListener((sequenceNumber, multiple) -> {
    // code when message is confirmed
}, (sequenceNumber, multiple) -> {
    // code when message is nack-ed
});
```

这里有两个回调，一个是确认消息，另外一个是未确认的消息（代理认为丢失的消息）。每一个回调都有两个参数：

- sequenceNumber：标识已确认或未确认消息的数字。我们很快将会看到它如何和已经发布的消息相关联。
- multiple:这是一个boolean值，如果为false,只有一个已确认或已取消的消息，如果为true,所有序号小于等于sequenceNumber的都确认或者取消。

将消息与序列号关联的一种简单方法是使用映射。假设我们要发布字符串，因为它们很容易变成要发布的字节数组。这是一个使用映射将发布序列号与消息的字符串主题相关联的代码示例：

```java
ConcurrentNavigableMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();
// ... code for confirm callbacks will come later
String body = "...";
outstandingConfirms.put(channel.getNextPublishSeqNo(), body);
channel.basicPublish(exchange, queue, properties, body.getBytes());

```

现在发布消息代码使用Map跟踪发布的消息，当消息确认的时候我们需要清除这个map,当取消或发布失败时需要做一些记录日志警告的操作：

```java
ConcurrentNavigableMap<Long, String> outstandingConfirms = new ConcurrentSkipListMap<>();
ConfirmCallback cleanOutstandingConfirms = (sequenceNumber, multiple) -> {
    if (multiple) {
        ConcurrentNavigableMap<Long, String> confirmed = outstandingConfirms.headMap(
          sequenceNumber, true
        );
        confirmed.clear();
    } else {
        outstandingConfirms.remove(sequenceNumber);
    }
};

channel.addConfirmListener(cleanOutstandingConfirms, (sequenceNumber, multiple) -> {
    String body = outstandingConfirms.get(sequenceNumber);
    System.err.format(
      "Message with body %s has been nack-ed. Sequence number: %d, multiple: %b%n",
      body, sequenceNumber, multiple
    );
    cleanOutstandingConfirms.handle(sequenceNumber, multiple);
});
// ... publishing code
```

> 如何跟踪未完成的确认？
>
>
我们的示例使用ConcurrentNavigableMap跟踪未完成的确认。由于以下几个原因，此数据结构很方便，它允许轻松地将序列号与消息相关联（无论消息数据是什么），还可以轻松清除条目直到给定序列ID(
以处理多个确认/提示)。最后，它支持并发访问，因为在客户端库拥有的线程中调用了确认回调，该线程应与发布线程保持不同。
>
> 除了使用复杂的映射实现之外，还有其他跟踪未完成确认的方法，例如使用简单的并发哈希映射和变量来跟踪发布序列的下限，但是他们通常涉及跟多
> 且不属于本教程。

综上所述，处理发布者异步确认通常需要执行以下步骤：

- 提供一种将发布序列号与消息相关联的方法。
- 在通道上注册一个确认监听器，以便在发布者确认或通知到达后执行相应操作（例如记录或重新发布未确认的消息）时收到通知。序列号与消息的关联机制在此步骤中可能还需要一些清洗。
- 在发布消息之前跟踪发布序列号

> 重新发布nack-ed消息？
>
> 从相应的回调中重新发布一个nack-ed消息可能很诱人，但是应该避免这种情况，因为回调是在不应执行通道的I/O线程中分派的。更好的解决方案是将消息放入由发布线程轮询的内存队列中。诸如
> ConcurrentLinkedQueue 之类的类将是在确认回调和发布线程之间传输消息的理想选择。


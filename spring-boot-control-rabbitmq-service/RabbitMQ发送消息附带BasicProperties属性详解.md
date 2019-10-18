#### RabbitMQ发送消息附带BasicProperties属性详解

##### BasicPropertie属性字段详解

1. contentType:消息的内容类型，如：text/plain
2. contentEncoding:消息内容编码
3. headers:设置消息的header,类型为Map<String,Object>
4. deliveryMode:1（nopersistent）非持久化，2（persistent）持久化
5. priority:消息的优先级
6. correlationId:关联ID
7. replyTo:用于指定回复的队列的名称
8. expiration:消息的失效时间
9. messageId:消息ID
10. timestamp:消息的时间戳
11. type:类型
12. userId:用户ID
13. appId：应用程序ID
14. custerId:集群ID

##### BasicProperties使用详解

> MessageProperties类默认提供了6种不同默认值的BasicProperties属性值对象

生产者发送消息：

```java
AMQP.BasicProperties.Builder properties = MessageProperties.PERSISTENT_TEXT_PLAIN.builder();
properties.messageId("消息ID");
properties.deliveryMode(2);
/**
* 发布消息
* 发布到不存在的交换器将导致信道级协议异常，该协议关闭信道，
* exchange: 要将消息发送到的交换器
* routingKey: 路由KEY
* props: 消息的其它属性，如：路由头等
* body: 消息体
*/
channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, properties.build(), message.getBytes());
```

消费者接收消息：

```
DeliverCallback deliverCallback = (consumerTag, delivery)->{
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message);
            BasicProperties properties = delivery.getProperties();
            System.out.println("deliveryMode:"+properties.getDeliveryMode()
                    +"-消息ID"+properties.getMessageId());
        };
        
channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
            System.out.println("调用"+consumerTag);
        });
```


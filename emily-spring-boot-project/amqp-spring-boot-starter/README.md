##### 一、POM依赖

```xml
            <dependency>
                <groupId>io.github.mingyang66</groupId>
                <artifactId>amqp-spring-boot-starter</artifactId>
                <version>${revision}</version>
            </dependency>
```

##### 二、属性配置

- spring.emily.rabbit.default-config默认标识必须配置；
- spring.emily.rabbit.store-log-messages指定是否记录发送、退回、接收到的消息，默认自动串联上线文；

```properties
spring.emily.rabbit.enabled=true
#默认配置标识
spring.emily.rabbit.default-config=test
#存储发送、退回、接收的消息到日志平台，默认：true
spring.emily.rabbit.store-log-messages=true
#------------------------------------------------连接工厂配置--------------------------------------------------
#监听器类型，默认：simple
spring.emily.rabbit.connection.listener-type=direct
#设置TCP连接超时时间，默认：60000ms
spring.emily.rabbit.connection.connection-timeout=60000
#启用或禁用连接自动恢复，默认：true
spring.emily.rabbit.connection.automatic-recovery=true
#设置连接恢复时间间隔，默认：5000ms
spring.emily.rabbit.connection.network-recovery-interval=5000
#启用或禁用拓扑恢复，默认：true【拓扑恢复功能可以帮助消费者重新声明之前定义的队列、交换机和绑定等拓扑结构】
spring.emily.rabbit.connection.topology-recovery=true
#------------------------------------------------连接配置-----------------------------------------------------
#虚拟主机
spring.emily.rabbit.config.test.virtual-host=xx
#集群地址
spring.emily.rabbit.config.test.addresses=10.10.xx.xx:5672,10.10.xx.xx:5672,10.10.xx.xx:5672
#用户名
spring.emily.rabbit.config.test.username=xx-xx
#密码
spring.emily.rabbit.config.test.password=xxxx
#producer
spring.emily.rabbit.config.test.publisher-confirm-type=correlated
#开启消息发布回退模式，优先级最高
#spring.emily.rabbit.config.test.template.mandatory=true
#开启消息发布回退模式，优先级低于mandatory
#spring.emily.rabbit.config.test.publisher-returns=true
#---------------------------------------------------------生产端重试--------------------------------------------
#是否启用发布重试，默认：false
#spring.emily.rabbit.config.test.template.retry.enabled=true
#发送消息的最大重试次数，默认：2
#spring.emily.rabbit.config.test.template.retry.max-attempts=3
#第一次和第二次发送消息的时间间隔，默认：1000ms
#spring.emily.rabbit.config.test.template.retry.initialInterval=1000ms
#应用于与上一次时间间隔的乘数，默认：1.0
#spring.emily.rabbit.config.test.template.retry.multiplier=1.0
#重试的最大时间间隔，默认：10000ms
#spring.emily.rabbit.config.test.template.retry.max-interval=10s
```

##### 三、使用案例

- 客户端发送消息

```java
@RestController
public class RabbitController {
    private final RabbitTemplate rabbitTemplate;
    private final RabbitTemplate emilyRabbitTemplate;

    public RabbitController(RabbitTemplate rabbitTemplate, @Qualifier(value = "emilyRabbitTemplate") RabbitTemplate emilyRabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.emilyRabbitTemplate = emilyRabbitTemplate;
    }

    @GetMapping("api/rabbit/return")
    public void test() {
        rabbitTemplate.convertAndSend("emily.return", "return", new Message("nihao".getBytes(Charset.defaultCharset())));
        emilyRabbitTemplate.convertAndSend("emily.account", "account", new Message("nihao".getBytes(Charset.defaultCharset())));
    }

    @GetMapping("api/rabbit/send")
    public void send() {
        rabbitTemplate.convertAndSend("emily.test", "", new Message("nihao".getBytes(Charset.defaultCharset())));
        emilyRabbitTemplate.convertAndSend("emily.test", "", new Message("nihao".getBytes(Charset.defaultCharset())));
    }

    @GetMapping("api/rabbit/send1")
    public void send1() {
        RabbitTemplate rabbitTemplateEmily = DataRabbitFactory.getRabbitTemplate("emily");
        rabbitTemplateEmily.convertAndSend("exchange_emily", "emily.23", new Message("nihao".getBytes(Charset.defaultCharset())));
    }
}
```

- 消费端消费消息

```java
@Configuration
public class RabbitConfig {

    @RabbitListener(queues = "emily.test.queue", containerFactory = "emilyRabbitListenerContainerFactory")
    public void handler(Channel channel, Message message) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        System.out.println("EMILY-" + new String(message.getBody()));
        channel.basicAck(deliveryTag, false);
    }

    //@RabbitListener(queues = "topic.test.queue")
    public void handlerEmily(Channel channel, Message message) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        System.out.println("TEST-" + new String(message.getBody()));
        channel.basicAck(deliveryTag, false);
    }

    @RabbitListener(queues = "topic.test.queue")
    public void handlerEmily1(Channel channel, @Payload String message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        System.out.println("TEST-" + message);
        channel.basicAck(deliveryTag, false);
    }
}
```

##### 四、@RabbitListener消息确认模式

```
#消息确认模式
spring.emily.rabbit.config.emily.listener.direct.acknowledge-mode=manual
```

- 一共有两种种确认模式：MANUAL、AUTO

- 默认是手动确认模式：AUTO

##### 五、重构SDK注意事项

- 多个containerFactory无需自动化配置，系统启动时自动初始化核心注意com.emily.infrastructure.rabbitmq.annotation.RabbitListenerAnnotationBeanPostProcessor#resolveContainerFactory方法；

- s

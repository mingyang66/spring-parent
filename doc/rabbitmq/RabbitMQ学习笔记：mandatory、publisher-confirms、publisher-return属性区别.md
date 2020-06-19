### RabbitMQ学习笔记：mandatory、publisher-confirms、publisher-return属性区别

> rabbitmq客户端发送消息首先发送的交换器exchange，然后通过路由键routingKey和bindingKey比较判定需要将消息发送到那个队列queue上；在这个过程有两个地方消息可能丢失，第一消息发送到交换器exchange的过程，第二消息从交换器exchange发送到队列queue的过程；

##### 1.publiser-confirm模式可以确保生产者到交换器exchange消息有没有发送成功

```
#设置此属性配置可以确保消息成功发送到交换器
spring.rabbitmq.publisher-confirms=true
```

##### 2.publisher-return模式可以在消息没有被路由到指定的queue时将消息返回，而不是丢弃

```
#可以确保消息在未被队列接收时返回
spring.rabbitmq.publisher-returns=true
```

在使用上面的属性配置时通常会和mandatory属性配合一起使用：

```
#指定消息在没有被队列接收时是否强行退回还是直接丢弃
spring.rabbitmq.template.mandatory=true
```

到这里你可能会有一个疑问，这两个配置都是指定未找到合适队列时将消息退回，究竟是如何分别起作用呢？接下来我们看下RabbitAutoConfiguration自动化配置类就清楚了：

```java
        @Bean
        @ConditionalOnSingleCandidate(ConnectionFactory.class)
        @ConditionalOnMissingBean
        public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
            PropertyMapper map = PropertyMapper.get();
            RabbitTemplate template = new RabbitTemplate(connectionFactory);
            MessageConverter messageConverter = (MessageConverter)this.messageConverter.getIfUnique();
            if (messageConverter != null) {
                template.setMessageConverter(messageConverter);
            }
						//设置rabbitmq处理未被queue接收消息的模式
            template.setMandatory(this.determineMandatoryFlag());
            Template properties = this.properties.getTemplate();
            if (properties.getRetry().isEnabled()) {
                template.setRetryTemplate((new RetryTemplateFactory((List)this.retryTemplateCustomizers.orderedStream().collect(Collectors.toList()))).createRetryTemplate(properties.getRetry(), Target.SENDER));
            }

            properties.getClass();
            map.from(properties::getReceiveTimeout).whenNonNull().as(Duration::toMillis).to(template::setReceiveTimeout);
            properties.getClass();
            map.from(properties::getReplyTimeout).whenNonNull().as(Duration::toMillis).to(template::setReplyTimeout);
            properties.getClass();
            map.from(properties::getExchange).to(template::setExchange);
            properties.getClass();
            map.from(properties::getRoutingKey).to(template::setRoutingKey);
            properties.getClass();
            map.from(properties::getDefaultReceiveQueue).whenNonNull().to(template::setDefaultReceiveQueue);
            return template;
        }
				//判定是否将未找到合适queue的消息退回
        private boolean determineMandatoryFlag() {
          	/**
              * 获取spring.rabbitmq.template.mandatory属性配置；
              * 这里面会有三种可能，为null、false、true
              * 而只有在mandatory为null时才会读取publisher-return属性值
              **/
            Boolean mandatory = this.properties.getTemplate().getMandatory();
            return mandatory != null ? mandatory : this.properties.isPublisherReturns();
        }
```

阅读上面的源码可以获取如下信息：

1. spring.rabbitmq.template.mandatory属性的优先级高于spring.rabbitmq.publisher-returns的优先级
2. spring.rabbitmq.template.mandatory属性可能会返回三种值null、false、true,
3. spring.rabbitmq.template.mandatory结果为true、false时会忽略掉spring.rabbitmq.publisher-returns属性的值
4. spring.rabbitmq.template.mandatory结果为null（即不配置）时结果由spring.rabbitmq.publisher-returns确定

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/doc/rabbitmq](https://github.com/mingyang66/spring-parent/tree/master/doc/rabbitmq)
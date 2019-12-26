### RabbitMQ学习笔记：Springboot amqp之 spring_listener_return_correlation、 spring_returned_message_correlation详解

##### 1.引入springboot amqp依赖

```
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-amqp</artifactId>
        </dependency>
```

启用消息未成功发送到队列时强制退回及开启回调功能：

```
spring.rabbitmq.publisher-returns=true
spring.rabbitmq.template.mandatory=true
```



##### 2.发送消息到RabbitMQ消息服务器我们可以看到headers有两个属性

![在这里插入图片描述](https://img-blog.csdnimg.cn/20191226174600463.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9taW5neWFuZy5ibG9nLmNzZG4ubmV0,size_16,color_FFFFFF,t_70)

这两个属性分别代表什么意思呢？我们看下org.springframework.amqp.rabbit.connection.PublisherCallbackChannel接口：

```java
public interface PublisherCallbackChannel extends Channel {

	/**
	 * Header used to determine which listener to invoke for a returned message.
	 */
	String RETURN_LISTENER_CORRELATION_KEY = "spring_listener_return_correlation";

	/**
	 * Header used to locate a pending confirm to which to attach a returned message.
	 */
	String RETURNED_MESSAGE_CORRELATION_KEY = "spring_returned_message_correlation";
	}
```

- spring_listener_return_correlation:该属性是用来确定消息被退回时调用哪个监听器
- spring_returned_message_correlation：该属性是指退回待确认消息的唯一标识

##### 3.spring_returned_message_correlation在哪里设置，又有什么作用？

> 发送消息时有一个退回消息相关数据org.springframework.amqp.rabbit.connection.CorrelationData类，这个header属性对应相关数据类的Id属性

```java
    private final RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
        @Override
        public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
            System.err.println("return exchange: " + exchange + ", routingKey: "
                    + routingKey + ", replyCode: " + replyCode + ", replyText: " + replyText+",MessageId:"+message.getMessageProperties().getMessageId());
        }
    };


public void sendMsg(String exchange, String routingKey, String message, Map<String, Object> properties){
        org.springframework.messaging.Message msg = org.springframework.messaging.support.MessageBuilder.withPayload(message).build();
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
        correlationData.setId(msg.getHeaders().getId().toString());

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
				...
                return message;
            }
        }, correlationData);
    }
```

对应上面代码的correlationData.setId(msg.getHeaders().getId().toString());方法

> 这个消息ID主要是用来在发送端记录发送了哪些消息，哪些消息已经成功确认，哪些消息发送失败，需要进一步的处理，如重试等等；

##### 4.spring_listener_return_correlation属性是在哪里设置的？又是起什么作用？

在org.springframework.amqp.rabbit.core.RabbitTemplate类中有如下方法：

```java
//随机生成
private final String uuid = UUID.randomUUID().toString();

public void doSend(Channel channel, String exchangeArg, String routingKeyArg, Message message, // NOSONAR complexity
			boolean mandatory, @Nullable CorrelationData correlationData)
					throws Exception { // NOSONAR TODO: change to IOException in 2.2.

		...

		Message messageToUse = message;
		MessageProperties messageProperties = messageToUse.getMessageProperties();
		if (mandatory) {
//设置spring_listener_return_correlation属性值	
            messageProperties.getHeaders().put(PublisherCallbackChannel.RETURN_LISTENER_CORRELATION_KEY, this.uuid);
		}
		if (this.beforePublishPostProcessors != null) {
			for (MessagePostProcessor processor : this.beforePublishPostProcessors) {
				messageToUse = processor.postProcessMessage(messageToUse, correlationData);
			}
		}
    //在此方法中会从退回消息相关数据类中取spring_returned_message_correlation设置到Message对象
		setupConfirm(channel, messageToUse, correlationData);
		if (this.userIdExpression != null && messageProperties.getUserId() == null) {
			String userId = this.userIdExpression.getValue(this.evaluationContext, messageToUse, String.class);
			if (userId != null) {
				messageProperties.setUserId(userId);
			}
		}
		sendToRabbit(channel, exch, rKey, mandatory, messageToUse);
		// Check if commit needed
		if (isChannelLocallyTransacted(channel)) {
			// Transacted channel created by this template -> commit.
			RabbitUtils.commitIfNecessary(channel);
		}
	}
```

spring_returned_message_correlation属性设置

```java
	private void setupConfirm(Channel channel, Message message, @Nullable CorrelationData correlationDataArg) {
		if ((this.publisherConfirms || this.confirmCallback != null) && channel instanceof PublisherCallbackChannel) {

			PublisherCallbackChannel publisherCallbackChannel = (PublisherCallbackChannel) channel;
			CorrelationData correlationData = this.correlationDataPostProcessor != null
					? this.correlationDataPostProcessor.postProcess(message, correlationDataArg)
					: correlationDataArg;
			long nextPublishSeqNo = channel.getNextPublishSeqNo();
			message.getMessageProperties().setPublishSequenceNumber(nextPublishSeqNo);
			publisherCallbackChannel.addPendingConfirm(this, nextPublishSeqNo,
					new PendingConfirm(correlationData, System.currentTimeMillis()));
			if (correlationData != null && StringUtils.hasText(correlationData.getId())) {
				message.getMessageProperties().setHeader(PublisherCallbackChannel.RETURNED_MESSAGE_CORRELATION_KEY,
						correlationData.getId());
			}
		}
		else if (channel instanceof ChannelProxy && ((ChannelProxy) channel).isConfirmSelected()) {
			long nextPublishSeqNo = channel.getNextPublishSeqNo();
			message.getMessageProperties().setPublishSequenceNumber(nextPublishSeqNo);
		}
	}
```

> spring_listener_return_correlation的属性作用是确认调用哪个监听器，即消息退回处理回调方法。


GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service)
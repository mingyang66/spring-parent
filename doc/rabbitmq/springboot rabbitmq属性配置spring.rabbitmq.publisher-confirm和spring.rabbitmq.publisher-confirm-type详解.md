### springboot rabbitmq属性配置spring.rabbitmq.publisher-confirm和spring.rabbitmq.publisher-confirm-type详解

>
在springboot2.2.0.RELEASE版本之前是amqp正式支持的属性，用来配置消息发送到交换器之后是否触发回调方法，在2.2.0及之后该属性过期使用spring.rabbitmq.publisher-confirm-type属性配置代替，用来配置更多的确认类型；

##### 1.spring.rabbitmq.publisher-confirm发布确认属性配置

如果该属性为true，则会触发以下方法：

```java
        /**
         * 设置生产者消息publish-confirm回调函数
         */
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if(!ack){
                LoggerUtil.error(RabbitConfig.class, StringUtils.join("publishConfirm消息发送到交换器被退回，Id：", correlationData.getId(), ";退回原因是：", cause));
            } else {
                LoggerUtil.info(RabbitConfig.class, "发送消息到交换器成功,MessageId:"+correlationData.getId());
            }
        });
```

##### 2.spring.rabbitmq.publisher-confirm-type新版发布确认属性有三种确认类型

```java
	/**
	 * The type of publisher confirms to use.
	 */
	public enum ConfirmType {

		/**
		 * Use {@code RabbitTemplate#waitForConfirms()} (or {@code waitForConfirmsOrDie()}
		 * within scoped operations.
		 */
		SIMPLE,

		/**
		 * Use with {@code CorrelationData} to correlate confirmations with sent
		 * messsages.
		 */
		CORRELATED,

		/**
		 * Publisher confirms are disabled (default).
		 */
		NONE

	}
```

- NONE值是禁用发布确认模式，是默认值
- CORRELATED值是发布消息成功到交换器后会触发回调方法，如1示例
-

SIMPLE值经测试有两种效果，其一效果和CORRELATED值一样会触发回调方法，其二在发布消息成功后使用rabbitTemplate调用waitForConfirms或waitForConfirmsOrDie方法等待broker节点返回发送结果，根据返回结果来判定下一步的逻辑，要注意的点是waitForConfirmsOrDie方法如果返回false则会关闭channel，则接下来无法发送消息到broker;

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/doc/rabbitmq](https://github.com/mingyang66/spring-parent/tree/master/doc/rabbitmq)
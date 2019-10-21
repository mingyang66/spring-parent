### RabbitMQ学习笔记：获取消息（Ack Mode）

> 在服务器端的客户端页面从队列中获取消息是一个危险的动作，生产环境一定要了解业务之后再做操作

##### Act Mode

- Nack message requeue true

> 获取消息，但是不做ack应答确认，消息重新入队

- Ack message requeue false

> 获取消息，应答确认，消息不重新入队，将会从队列中删除

- reject requeue true

> 拒绝获取消息，消息重新入队

- reject requeue false

> 拒绝获取消息，消息不重新入队，将会被删除



##### Encoding

> AMQP消息负载可以包含任何的二进制内容，因此他们很难再浏览器中展示，编码的选项含义有如下内容：string/base64,如果消息负载可以使用UTF-8字符串编码，就执行此操作，否则就按照base64编码进行返回。

##### Messages

> 定义一次从队列中获取的消息数量


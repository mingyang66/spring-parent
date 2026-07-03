package com.emily.sample.rabbitmq.rabbit;

import com.emily.infrastructure.amqp.common.DataRabbitInfo;
import com.emily.infrastructure.amqp.factory.DataRabbitFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfig {
    @Bean
    public String createQueueAndBinding(AmqpAdmin amqpAdmin) {
        //AmqpAdmin amqpAdmin = DataRabbitFactory.getAmqpAdmin("emily");
        // 1. 创建一个 Direct 类型的交换机
        amqpAdmin.declareExchange(new DirectExchange("my.exchange"));

        // 2. 创建一个队列（参数：队列名、是否持久化）
        amqpAdmin.declareQueue(new Queue("my.queue", true));

        // 3. 绑定交换机和队列（参数：目标队列名、目标类型、源交换机名、路由键、绑定参数）
        Binding binding = new Binding("my.queue", Binding.DestinationType.QUEUE,
                "my.exchange", "my.routing.key", null);
        amqpAdmin.declareBinding(binding);
        Channel channel = DataRabbitFactory.getChannel("test", false);
        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, com.rabbitmq.client.Envelope envelope, com.rabbitmq.client.AMQP.BasicProperties properties, byte[] body) {
                System.out.println("Received message: " + new String(body));
            }
        };
        return "Queue and Binding created successfully";
    }
}

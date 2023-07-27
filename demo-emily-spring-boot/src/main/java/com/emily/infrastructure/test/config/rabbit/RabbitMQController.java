/*
package com.emily.infrastructure.test.controller.rabbit;

import com.emily.infrastructure.rabbitmq.factory.RabbitMqFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;

*/
/**
 *  消息中间件
 * @author  Emily
 * @since  Created in 2022/6/6 11:38 上午
 *//*


@RestController
@RequestMapping("api/rabbit")
public class RabbitMQController {

    @GetMapping("test")
    public void test() {
        TopicExchange exchange = ExchangeBuilder.topicExchange("exchange").build();
        Queue queue = QueueBuilder.durable("topic.test.queue").build();
        Binding binding = BindingBuilder.bind(queue).to(exchange).with("topic.#");
        RabbitMqFactory.declare("test", queue, exchange, binding);

        TopicExchange exchange1 = ExchangeBuilder.topicExchange("exchange_emily").build();
        Queue queue1 = QueueBuilder.durable("topic.emily.queue").build();
        Binding binding1 = BindingBuilder.bind(queue1).to(exchange1).with("emily.#");
        RabbitMqFactory.declare("emily", queue1, exchange1, binding1);

        RabbitMessagingTemplate template = RabbitMqFactory.getRabbitMessagingTemplate();
        RabbitMessagingTemplate template1 = RabbitMqFactory.getRabbitMessagingTemplate("emily");
        //template.convertAndSend("exchange","topic.test",new Message("nihao".getBytes(Charset.defaultCharset())));
        //template.convertAndSend("exchange","topic.emily",new Message("nihao".getBytes(Charset.defaultCharset())));
        template1.convertAndSend("exchange_emily", "emily.23", new Message("nihao".getBytes(Charset.defaultCharset())));

        RabbitTemplate rabbitTemplate = RabbitMqFactory.getRabbitTemplate();
        RabbitTemplate rabbitTemplateEmily = RabbitMqFactory.getRabbitTemplate("emily");
        rabbitTemplate.convertAndSend("exchange", "topic.test", new Message("nihao".getBytes(Charset.defaultCharset())));
        rabbitTemplateEmily.convertAndSend("exchange_emily", "emily.23", new Message("nihao".getBytes(Charset.defaultCharset())));

    }

    @GetMapping("send")
    public void send() {
        RabbitTemplate rabbitTemplate = RabbitMqFactory.getRabbitTemplate();
        rabbitTemplate.convertAndSend("exchange", "topic.test", new Message("nihao".getBytes(Charset.defaultCharset())));
        RabbitTemplate rabbitTemplateEmily = RabbitMqFactory.getRabbitTemplate("emily");
        rabbitTemplateEmily.convertAndSend("exchange_emily", "emily.23", new Message("nihao".getBytes(Charset.defaultCharset())));
    }

    @GetMapping("send1")
    public void send1() {
        RabbitTemplate rabbitTemplateEmily = RabbitMqFactory.getRabbitTemplate("emily");
        rabbitTemplateEmily.convertAndSend("exchange_emily", "emily.23", new Message("nihao".getBytes(Charset.defaultCharset())));
    }
}
*/

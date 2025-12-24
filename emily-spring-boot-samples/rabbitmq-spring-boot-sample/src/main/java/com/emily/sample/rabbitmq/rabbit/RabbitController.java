package com.emily.sample.rabbitmq.rabbit;

import com.emily.infrastructure.rabbitmq.factory.DataRabbitFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;


/**
 * 消息中间件
 *
 * @author Emily
 * @since Created in 2022/6/6 11:38 上午
 */


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
        rabbitTemplate.convertAndSend("emily.return", "", new Message("nihao".getBytes(Charset.defaultCharset())));

    }

    @GetMapping("api/rabbit/send")
    public void send() {
        //RabbitTemplate rabbitTemplate = RabbitMqFactory.getRabbitTemplate();
        rabbitTemplate.convertAndSend("emily.test", "", new Message("nihao".getBytes(Charset.defaultCharset())));
        //RabbitTemplate rabbitTemplateEmily = RabbitMqFactory.getRabbitTemplate("emily");
        emilyRabbitTemplate.convertAndSend("emily.test", "", new Message("nihao".getBytes(Charset.defaultCharset())));
    }

    @GetMapping("api/rabbit/send1")
    public void send1() {
        RabbitTemplate rabbitTemplateEmily = DataRabbitFactory.getRabbitTemplate("emily");
        rabbitTemplateEmily.convertAndSend("exchange_emily", "emily.23", new Message("nihao".getBytes(Charset.defaultCharset())));
    }
}

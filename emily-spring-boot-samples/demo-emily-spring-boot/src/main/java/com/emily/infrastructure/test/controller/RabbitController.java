/*
package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.rabbitmq.factory.RabbitMqFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

*/
/**
 * 消息中间件控制器
 *
 * @author :  Emily
 * @since :  2023/8/25 10:49 PM
 *//*

@RestController
@RequestMapping("api/rabbit")
public class RabbitController {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    @Qualifier("emilyRabbitTemplate")
    private RabbitTemplate rabbitTemplateEmily;

    @GetMapping("send")
    public void send() {
        rabbitTemplate.convertAndSend("emily.test", "", "测试故障恢复");
        rabbitTemplateEmily.convertAndSend("exchange_emily", "exchange.#", "测试故障恢复");
        RabbitMqFactory.getRabbitTemplate("emily").convertAndSend("exchange_emily", "exchange.#", "测试故障恢复");
    }
}
*/

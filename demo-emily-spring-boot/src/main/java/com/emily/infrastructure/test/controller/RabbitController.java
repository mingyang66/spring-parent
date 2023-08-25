package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.rabbitmq.factory.RabbitMqFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息中间件控制器
 * @author :  Emily
 * @since :  2023/8/25 10:49 PM
 */
@RestController
@RequestMapping("api/rabbit")
public class RabbitController {
    @GetMapping("send")
    public void send(){
        RabbitMqFactory.getRabbitTemplate("test").convertAndSend("emily.test", "", "测试故障恢复");
    }
}

package com.yaomy.control.test.api;

import com.sgrain.boot.common.enums.DateFormatEnum;
import com.yaomy.control.rabbitmq.amqp.delay.RabbitDelaySender;
import com.yaomy.control.rabbitmq.amqp.lazy.RabbitLazySender;
import com.yaomy.control.rabbitmq.amqp.ttl.RabbitSender;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @Description: RabbitMQ消息队列控制器
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
@RestController
public class RabbitController {
    @Autowired
    private RabbitSender rabbitSender;
    @Autowired
    private RabbitDelaySender rabbitDelaySender;
    @Autowired
    private RabbitLazySender rabbitLazySender;

    @GetMapping(value = "/rabbit/ttl")
    public String ttl(String exchange, String route){

        MessageProperties properties = new MessageProperties();
        properties.getHeaders().put("number", "12345");
        properties.getHeaders().put("send_time", DateFormatUtils.format(new Date(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()));
        rabbitSender.sendMsg(exchange, route, "Hello RabbitMQ For Spring Boot!", properties);
        return "ttl";
    }


    @GetMapping(value = "/rabbit/delay")
    public String delay(String exchange, String route){

        MessageProperties properties = new MessageProperties();
        properties.getHeaders().put("number", "12345");
        properties.getHeaders().put("send_time", DateFormatUtils.format(new Date(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()));
        rabbitDelaySender.sendMsg(exchange, route, "Hello RabbitMQ For Spring Boot!", properties);
        return "delay";
    }

    @GetMapping(value = "/rabbit/lazy")
    public String lazy(String exchange, String route){
        new Thread(()->{
                for(int i=0;i<100000;i++){
                    MessageProperties properties = new MessageProperties();
                    properties.getHeaders().put("number", i+1);
                    properties.getHeaders().put("send_time", DateFormatUtils.format(new Date(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()));
                    rabbitLazySender.sendMsg(exchange, route, "你好洛奇你好洛奇你好洛奇你好洛奇你好洛奇", properties);
                    System.out.println("已经发送了："+i+"条消息...");
                }
        }).start();
        return "lazy";
    }
}

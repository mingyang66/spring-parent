package com.emily.sample.websocket.controller;


import com.emily.sample.websocket.entity.HelloMessage;
import com.emily.sample.websocket.entity.ReceiverMessage;
import com.emily.sample.websocket.entity.Greeting;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

    private final SimpMessagingTemplate messagingTemplate;

    public GreetingController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return new Greeting("广播聊天： " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

    @MessageMapping("/send")
    //@SendToUser(destinations = "/queue/chat")
    public void chat(@Payload ReceiverMessage message) throws Exception {
        //Thread.sleep(1000); // simulated delay
        messagingTemplate.convertAndSendToUser(message.getReceiver(), "/queue/chat", message);
        //return new Greeting("一对一聊天： " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

}
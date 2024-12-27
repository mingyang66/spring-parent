package com.emily.infrastructure.sample.web.controller.event;

import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author :  Emily
 * @since :  2024/12/27 上午10:41
 */
@RestController
public class ApplicationEventController {
    private final ApplicationContext applicationContext;

    public ApplicationEventController(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @GetMapping("api/event/publish")
    public void publishEvent() {
       /// applicationContext.publishEvent(new LoggerApplicationEvent("期望"));
        System.out.println("end");
    }

    @GetMapping("api/event/publishEx")
    public void publishEx(@RequestParam String username) throws IllegalAccessException {
        /// applicationContext.publishEvent(new LoggerApplicationEvent("期望"));
    }
}

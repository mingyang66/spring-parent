package com.emily.cloud.test.api;

import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 *
 * @author  Emily
 * @since  Created in 2023/6/25 6:02 PM
 */
@Component
public class DemoEventListener {

    @EventListener
    public void handler(EnvironmentChangeEvent event) {
        //ApplicationContext context = (ApplicationContext) event.getSource();
        //IOCContext.setCONTEXT(context);
        //System.out.println(event.getSource());
    }
}

package com.emily.cloud.test.api;

import com.emily.infrastructure.core.context.ioc.IOCContext;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @Description :
 * @Author :  姚明洋
 * @CreateDate :  Created in 2023/6/25 6:02 PM
 */
@Component
public class DemoEventListener {

    @EventListener
    public void handler(EnvironmentChangeEvent event){
        //ApplicationContext context = (ApplicationContext) event.getSource();
        //IOCContext.setCONTEXT(context);
        //System.out.println(event.getSource());
    }
}

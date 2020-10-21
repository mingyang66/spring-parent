package com.sgrain.boot.quartz;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SpringBootApplication
public class QuartzBootStrap implements ApplicationContextAware {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(QuartzBootStrap.class);
        application.run(args);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Object obj = applicationContext.getBean("quartzTest");
        Object obj1 = applicationContext.getBean("quartzTest2");
        System.out.println("---------");
    }
}

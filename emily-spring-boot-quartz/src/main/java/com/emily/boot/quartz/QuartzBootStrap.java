package com.emily.boot.quartz;


import org.springframework.beans.BeansException;
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
        System.out.println("---------");
    }
}

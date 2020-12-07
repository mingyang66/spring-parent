package com.yaomy.control.test;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoBootStrap {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        SpringApplication application = new SpringApplication(DemoBootStrap.class);
        application.run(args);

        long end = System.currentTimeMillis();
        System.out.println("启动耗时：" + (end - start));
    }


}

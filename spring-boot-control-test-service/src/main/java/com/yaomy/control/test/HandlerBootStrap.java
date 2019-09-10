package com.yaomy.control.test;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"com.yaomy.control"})
@MapperScan(basePackages = {"com.yaomy.control.*.mapper"}, sqlSessionTemplateRef = "sqlSessionTemplate")
public class HandlerBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(HandlerBootStrap.class, args);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}

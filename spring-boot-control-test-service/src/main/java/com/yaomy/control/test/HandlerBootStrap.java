package com.yaomy.control.test;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication(scanBasePackages = {"com.yaomy.control"}, exclude = {DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class})
@MapperScan(value = "com.yaomy.control.mapper",sqlSessionTemplateRef = "jdbcTemplate")
public class HandlerBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(HandlerBootStrap.class, args);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}

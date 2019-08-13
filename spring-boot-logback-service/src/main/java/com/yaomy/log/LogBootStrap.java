package com.yaomy.log;


import com.yaomy.log.utils.LoggerUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LogBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(LogBootStrap.class, args);
        LoggerUtil.info("sfddsf-----------------");
        LoggerUtil.error("-----------error------------");
        LoggerUtil.warn("----------warn--------------");
        LoggerUtil.debug("---------------debug-");
    }
}

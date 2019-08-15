package com.yaomy.log;


import com.yaomy.log.po.UserAction;
import com.yaomy.log.utils.LoggerUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LogBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(LogBootStrap.class, args);
        LoggerUtil.info(LogBootStrap.class,"sfddsf-----------------");
        LoggerUtil.info(LogBootStrap.class,"{username:liming}");
        LoggerUtil.error(LogBootStrap.class,"-----------error------------");
        LoggerUtil.warn(LogBootStrap.class,"----------warn--------------");
        LoggerUtil.debug(LogBootStrap.class,"---------------debug-");
        UserAction userAction = new UserAction();
        userAction.setUsername("dsdf");
        userAction.setNumber("12");
        LoggerUtil.user(userAction);
    }
}

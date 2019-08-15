package com.yaomy.log;


import com.fasterxml.jackson.databind.JsonNode;
import com.yaomy.log.po.UserAction;
import com.yaomy.log.utils.LoggerUtil;
import jdk.nashorn.internal.ir.ObjectNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

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
        Map<String, Object> param = new HashMap<>();
        param.put("a", "b");
        param.put("c", 1);
        userAction.setIN_PARAM(param);
        LoggerUtil.user(userAction);
    }
}

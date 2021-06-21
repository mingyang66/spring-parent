package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.logback.utils.LoggerUtils;
import com.emily.infrastructure.test.po.Job;
import org.springframework.web.bind.annotation.*;

/**
 * @program: spring-parent
 * @description: 日志控制器
 * @author: Emily
 * @create: 2021/05/31
 */
@RestController
@RequestMapping("logback")
public class LogbackController {

    @GetMapping("debug")
    public String debug(){
        LoggerUtils.getLogger(LogbackController.class, "66/12/34", "test").error("211112122");
        LoggerUtils.getLogger(LogbackController.class, "emily", "smile").error("+++++++++++==ttttttttttttt");
        LoggerUtils.getLogger(LogbackController.class, "emily", "smile").debug("+++++++++++==ttttttttttttt");
        LoggerUtils.getLogger(LogbackController.class, "emily", "smile").info("+++++++++++==ttttttttttttt");
        LoggerUtils.getLogger(LogbackController.class, "emily", "smile").warn("+++++++++++==ttttttttttttt");
        LoggerUtils.getLogger(LogbackController.class, "emily", "smile").trace("+++++++++++==ttttttttttttt");
        LoggerUtils.warn(LogbackController.class,"shuai2 +++++++++++++++++++++++++++++++++++++warn");
        LoggerUtils.info(LogbackController.class,"shuai3 +++++++++++++++++++++++++++++++++++++info");
        LoggerUtils.error(LogbackController.class,"shuai4 +++++++++++++++++++++++++++++++++++++error");
        LoggerUtils.trace(LogbackController.class,"shuai5 +++++++++++++++++++++++++++++++++++++trace");
        LoggerUtils.module(LogbackController.class, "test1", "tt0", "ni-----------------" + System.currentTimeMillis());

        return "success";
    }
    @PostMapping("test")
    public void test(@RequestBody Job job){
        System.out.println(JSONUtils.toJSONPrettyString(job));
    }
}

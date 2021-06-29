package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.logback.factory.LogbackFactory;
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
        LogbackFactory.getLogger(LogbackController.class, "66/12/34", "test").error("211112122");
        LogbackFactory.getLogger(LogbackController.class, "66/12/34", "test").debug("211112122");
        LogbackFactory.getLogger(LogbackController.class, "66/12/34", "test").info("211112122");
        LogbackFactory.getLogger(LogbackController.class, "66/12/34", "test").warn("211112122");
        LogbackFactory.getLogger(LogbackController.class, "66/12/34", "test").trace("211112122");
        LogbackFactory.getLogger(LogbackController.class, "emily", "smile").error("+++++++++++==ttttttttttttt");
        LogbackFactory.getLogger(LogbackController.class, "emily", "smile").debug("+++++++++++==ttttttttttttt");
        LogbackFactory.getLogger(LogbackController.class, "emily", "smile").info("+++++++++++==ttttttttttttt");
        LogbackFactory.getLogger(LogbackController.class, "emily", "smile").warn("+++++++++++==ttttttttttttt");
        LogbackFactory.getLogger(LogbackController.class, "emily", "smile").trace("+++++++++++==ttttttttttttt");
        LogbackFactory.warn(LogbackController.class,"shuai2 +++++++++++++++++++++++++++++++++++++warn");
        LogbackFactory.info(LogbackController.class,"shuai3 +++++++++++++++++++++++++++++++++++++info");
        LogbackFactory.error(LogbackController.class,"shuai4 +++++++++++++++++++++++++++++++++++++error");
        LogbackFactory.trace(LogbackController.class,"shuai5 +++++++++++++++++++++++++++++++++++++trace");
        LogbackFactory.module(LogbackController.class, "test1", "tt0", "ni-----------------" + System.currentTimeMillis());

        return "success";
    }
    @PostMapping("test")
    public void test(@RequestBody Job job){
        System.out.println(JSONUtils.toJSONPrettyString(job));
    }
}

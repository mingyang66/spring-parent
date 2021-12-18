package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.logback.factory.LoggerFactory;
import com.emily.infrastructure.test.po.Job;
import org.slf4j.Logger;
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
    //private static final Logger logger = LoggerFactory.getGroupLogger("66/12/34", "test");
    @GetMapping("debug")
    public String debug() {
        LoggerFactory.getGroupLogger("66/12/34", "test").error("211112122");
        LoggerFactory.getGroupLogger("66/12/34", "test").debug("211112122");
        LoggerFactory.getGroupLogger("66/12/34", "test").info("211112122");
        LoggerFactory.getGroupLogger("66/12/34", "test").warn("211112122");
        LoggerFactory.getGroupLogger("66/12/34", "test").trace("211112122");
        LoggerFactory.getGroupLogger("emily", "smile").error("+++++++++++==ttttttttttttt");
        LoggerFactory.getGroupLogger("emily", "smile").debug("+++++++++++==ttttttttttttt");
        LoggerFactory.getGroupLogger("emily", "smile").info("+++++++++++==ttttttttttttt");
        LoggerFactory.getGroupLogger("emily", "smile").warn("+++++++++++==ttttttttttttt");
        LoggerFactory.getGroupLogger("emily", "smile").trace("+++++++++++==ttttttttttttt");

        LoggerFactory.getModuleLogger("test1", "tt0").error("ni-----------------" + System.currentTimeMillis());

        return "success";
    }

    @PostMapping("test")
    public void test(@RequestBody Job job) {
        System.out.println(JSONUtils.toJSONPrettyString(job));
    }
}

package com.emily.sample.request.controller;

import com.emily.sample.request.entity.User;
import org.springframework.web.bind.annotation.*;

/**
 * @author :  Emily
 * @since :  2024/10/25 上午10:17
 */
@RestController
public class RequestController {
    /**
     * GET请求入参和控制器一一对应
     * api/request/get?name="钱钟书"
     */
    @GetMapping("api/request/get")
    public String getRequest(@RequestParam String name) {
        return "钱钟书";
    }

    /**
     * Get请求入参和实体类中字段一一对应
     * api/request/get2?name="钱钟书“&id=24
     */
    @GetMapping("api/request/get1")
    public String getRequest1(User user) {
        return "钱钟书";
    }

    /**
     * GET请求入参以body的形式传递和实体类一一对应，实体类前必须标注@RequetBody
     * url:api/request/get2
     * body:{
     * "name":"钱钟书",
     * "id":24
     * }
     */
    @GetMapping("api/request/get2")
    public String getRequest2(@RequestBody User user) {
        return "钱钟书";
    }

    @PostMapping("api/request/post")
    public String postRequest(@RequestBody User user) {
        return "杨绛";
    }
}

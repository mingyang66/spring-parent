package com.emily.infrastructure.sample.web.controller;

import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.sample.web.entity.Job;
import com.emily.infrastructure.sample.web.entity.User;
import com.otter.infrastructure.servlet.RequestUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Emily
 * @program: spring-parent
 * 参数控制器
 * @since 2021/10/30
 */
@RestController
@RequestMapping("api/param")
public class ParamController {
    /**
     *
     */
    @PostMapping("test1")
    public Job test1(@Validated @RequestBody Job job) {
        return job;
    }

    @PostMapping("test2")
    public ResponseEntity<Job> test2(@Validated @RequestBody Job job) {

        return ResponseEntity.ok(job);
    }

    @PostMapping("postList")
    public int postList(@RequestBody List<User> list) {
        System.out.println(JsonUtils.toJSONPrettyString(list));
        return 0;
    }

    @PostMapping("postArray")
    public int postList(@RequestBody User[] list) {
        System.out.println(JsonUtils.toJSONPrettyString(list));
        return 0;
    }

    @GetMapping("rest/{code}/{name}")
    public String restTest(@PathVariable("code") String code, @PathVariable("name") @NotNull String name) {
        return "s";
    }

    @GetMapping("getParam")
    public String getParam(@RequestParam(value = "name", required = true) String username) {
        return username;
    }

    @GetMapping("getParam1")
    public String getParam1(@Validated @RequestBody Job job) {
        return "";
    }

    /**
     * 1. Get请求方式，传递Body类型参数，参数校验不通过时会触发MethodArgumentNotValidException（BindException的子类）异常
     */
    @GetMapping("getBody")
    public String getParam(@Validated @RequestBody Job job) {
        job.setJobDesc("我修改了入参值");
        return "sdf";
    }

    @PostMapping("validParam")
    public String validParam(@Validated @NotEmpty(message = "不可为空") String username) {
        return username;
    }
}

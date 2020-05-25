package com.yaomy.control.test.api;

import com.sgrain.boot.common.enums.AppHttpStatus;
import com.sgrain.boot.common.exception.BusinessException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @program: spring-parent
 * @description: 对象测试类
 * @author: 姚明洋
 * @create: 2020/05/22
 */
@RestController
public class ObjectController {

    @GetMapping("api/obj/test1")
    public void test1(){
        //String s = Objects.requireNonNull(null, "空指针异常浏览量");
        //System.out.println(s);
        throw new BusinessException(AppHttpStatus.IO_EXCEPTION);
    }
}

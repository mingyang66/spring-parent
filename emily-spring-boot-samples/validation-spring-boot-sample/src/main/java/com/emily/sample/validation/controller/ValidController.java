package com.emily.sample.validation.controller;

import com.emily.sample.validation.entity.User;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author :  Emily
 * @since :  2024/10/28 下午5:56
 */
@RestController
public class ValidController {

    /**
     * @Valid 标记控制器方法参数，嵌套属性，不可以分组
     */
    @PostMapping("api/valid/body")
    public void body(@RequestBody @Valid User user) {
        System.out.println(user.getId());
        System.out.println(user.getName());
        ResourceBundle bundle = ResourceBundle.getBundle("ValidationMessages", Locale.CHINA);
        System.out.println(bundle.getString("jakarta.validation.constraints.IsAccountCode.message"));

    }

}

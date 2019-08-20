package com.yaomy.control.test.api;

import com.yaomy.control.common.control.conf.PropertyService;
import com.yaomy.control.common.control.po.BaseResponse;
import com.yaomy.control.logback.po.UserAction;
import com.yaomy.control.logback.utils.LoggerUtil;
import com.yaomy.control.test.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @Description 测试类
 * @Date 2019/8/19 11:32
 * @Version  1.0
 */
@RestController
public class HandlerController {
    @Autowired
    private PropertyService propertyService;

    @RequestMapping(value = "/handler/test")
    public ResponseEntity<BaseResponse> getName(@RequestBody @Valid User user){
        LoggerUtil.info(HandlerController.class, "测试。。。");
        return ResponseEntity.ok(BaseResponse.createResponse(10006, "自定义测试", user));
    }
    @RequestMapping(value = "/handler/test1")
    public BaseResponse testNull(@RequestBody @Valid User user){
        LoggerUtil.info(HandlerController.class, "测试。。。");
        return BaseResponse.createResponse(1004, "sfsdf");
    }
    @RequestMapping(value = "/handler/test2")
    public ResponseEntity<String> testNull1(@RequestBody @Valid User user){
        System.out.println("----------------deee");
        return ResponseEntity.ok(null);
    }

    @RequestMapping(value = "/handler/test3")
    public void testNull13(@RequestBody @Valid User user){
        LoggerUtil.info(HandlerController.class, propertyService.getProperty("sms.code")+"---"+propertyService.getProperty("sms.message"));
        UserAction userAction = new UserAction();
        userAction.setNumber("12222");
        userAction.setUsername("hhhhhhh");
        LoggerUtil.user(userAction);
        System.out.println("----------------deee");
    }

}

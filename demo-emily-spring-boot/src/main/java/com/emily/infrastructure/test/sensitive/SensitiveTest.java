package com.emily.infrastructure.test.sensitive;

import com.emily.infrastructure.common.sensitive.SensitiveUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description :  脱敏测试
 * @Author :   Emily
 * @CreateDate :  Created in 2022/7/20 1:15 下午
 */
public class SensitiveTest {

    public static void main(String[] args) {
       /* SensitiveRequest request = new SensitiveRequest();
        request.setIdCard("413266266667876787");
        request.setPassword("123456");
        request.setUsername("孙少安");

        Strategy strategy = new Strategy();
        strategy.setAddress("原西县城，城关小学");
        strategy.setName("田润叶");
        strategy.setAge(23);
        request.setStrategy(strategy);
        System.out.println(JSONUtils.toJSONPrettyString(request));
        Object request1 = SensitiveUtils.getSensitive(request);
        Object request2 = SensitiveUtils.getSensitive(BaseResponse.buildResponse(request));
        System.out.println(JSONUtils.toJSONPrettyString(request1));
        System.out.println(JSONUtils.toJSONPrettyString(request2));*/


        // System.out.println(SensitiveUtils.getSensitive(null));
        //System.out.println(SensitiveUtils.getSensitive(123));
        // System.out.println(SensitiveUtils.getSensitive("23"));
        System.out.println(SensitiveUtils.getSensitive(Arrays.asList(null, "", 1, 2, 3, 4, 5)));
        Map<String, Object> dMap = new HashMap<>();
        dMap.put("a", 23);
        dMap.put("b", "23233");
        System.out.println(SensitiveUtils.getSensitive(dMap));
        System.out.println(SensitiveUtils.getSensitive("23"));
        System.out.println(SensitiveUtils.getSensitive("23"));


    }
}

package com.emily.infrastructure.test.mainTest;

import com.emily.infrastructure.autoconfigure.httpclient.annotation.TargetHttpTimeout;
import com.emily.infrastructure.common.entity.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @Description :
 * @Author :  Emily
 * @CreateDate :  Created in 2022/8/2 7:28 下午
 */
@Service
public class TestTimeout {
    @Autowired
    private RestTemplate restTemplate;

    @TargetHttpTimeout(readTimeout = 4000)
    public String loadStr() {
        BaseResponse<String> result = restTemplate.getForObject("https://127.0.0.1:8080/api/http/testResponse?timeout=3000", BaseResponse.class);
        System.out.println(result.getData());
        return result.getData();
    }
}

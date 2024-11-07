package com.emily.infrastructure.test.test;

import com.emily.infrastructure.transfer.rest.annotation.TargetHttpTimeout;
import com.emily.infrastructure.web.response.entity.BaseResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author Emily
 * @since Created in 2022/8/2 7:28 下午
 */
@Service
public class TestTimeout {
    //@Autowired
    private RestTemplate restTemplate;

    @TargetHttpTimeout(readTimeout = 4000)
    public String loadStr() {
        BaseResponse<String> result = restTemplate.getForObject("https://127.0.0.1:8080/api/http/testResponse?timeout=3000", BaseResponse.class);
        System.out.println(result.getData());
        return result.getData();
    }
}

package com.yaomy.control.test.consul;

import com.sgrain.boot.common.utils.json.JSONUtils;
import com.sgrain.boot.consul.httpclient.HttpClientBalanceAutoConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @program: spring-parent
 * @description:
 * @author: 姚明洋
 * @create: 2020/11/10
 */
@RestController
public class PropertyController {
    @Autowired
    private Environment environment;
    @Autowired
    @Qualifier(HttpClientBalanceAutoConfiguration.LOAD_BALANCED_BEAN_NAME)
    private RestTemplate restTemplate;
    @GetMapping("consul/test")
    public String test(){

        Map<String, Object> data = restTemplate.getForObject(StringUtils.join("http://CONSUL-DEMO", "/api/http/test1"), Map.class);
        //Map<String, Object> data = restTemplate.getForObject(StringUtils.join("http://127.0.0.1:9000", "/api/http/test1"), Map.class);
        System.out.println(JSONUtils.toJSONPrettyString(data));
        return environment.getProperty("test");
    }
}

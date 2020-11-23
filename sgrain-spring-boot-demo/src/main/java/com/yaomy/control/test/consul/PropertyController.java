package com.yaomy.control.test.consul;

import com.sgrain.boot.cloud.httpclient.HttpClientBalanceAutoConfiguration;
import com.sgrain.boot.common.cloud.ServiceInstanceUtils;
import com.sgrain.boot.common.utils.json.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @program: spring-parent
 * @description: consul
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
        String url = ServiceInstanceUtils.getRequestUrl("consul-demo-dev", "/api/http/test1?name=12231&pass=123");
        Map<String, Object> data = restTemplate.getForObject(url, Map.class);
        //Map<String, Object> data = restTemplate.getForObject(StringUtils.join("http://127.0.0.1:9001", "/api/http/test1"), Map.class);
        System.out.println(JSONUtils.toJSONPrettyString(data));
        return environment.getProperty("test");
    }
    @GetMapping("health")
    public void health(){

    }

}

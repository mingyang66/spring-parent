package com.yaomy.control.rest.webclient;

import com.google.common.collect.Maps;
import com.yaomy.control.rest.client.HttpClientService;
import com.yaomy.control.test.HandlerBootStrap;
import com.yaomy.control.test.po.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.web.reactive.function.BodyInserters.fromFormData;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {HandlerBootStrap.class})
public class WebClientTest {
    @Autowired
    private HttpClientService httpClientService;
    //@Test
    public void test(){
        String baseUrl = "http://172.30.67.122:9000//handler/";
        WebClient webClient = WebClient.create(baseUrl);
        Map<String, Object> param = Maps.newHashMap();
        param.put("path", "client");
        Mono<String> result = webClient.post().uri("path", param).retrieve().bodyToMono(String.class);
        System.out.println(result.block());
    }
    @Test
    public void testUrlBuilder(){
        User user = new User();
        user.setName("李明");
        user.setAge(26);
        user.setWeight(new String[]{"12"});
        /*Map<String, Object> user = new HashMap<>();
        user.put("name", "李明");
        user.put("age", 26);*/
        MultipartBodyBuilder multiValueMap = new MultipartBodyBuilder();
        multiValueMap.part("name", "lili");
        multiValueMap.part("age", 26);
       // multiValueMap.part("user", user);

        String baseUrl = "http://172.30.67.122:9000/handler/";
        WebClient webClient = WebClient.create(baseUrl);
        Mono<Map> result = webClient.post()
                                    .uri("client1")
                                    //.contentType(MediaType.APPLICATION_STREAM_JSON)
                                   // .body(fromFormData("name", "sd").with("age", "23"))
                                    .syncBody(multiValueMap.build())
                                    .retrieve()
                                    .onStatus(HttpStatus::is3xxRedirection, clientResponse -> Mono.error(new Throwable()))
                                    .bodyToMono(ParameterizedTypeReference.forType(Map.class));
        System.out.println(result.block());
    }
    @Test
    public void testRest(){
        RestTemplate restTemplate = new RestTemplate();
        User user = new User();
        user.setName("李明");
        user.setAge(26);
        user.setWeight(new String[]{"12"});
        ResponseEntity<String> entity = restTemplate.postForEntity("http://172.30.67.122:9000/handler/client1", user, String.class);
        System.out.println(entity);
    }
}

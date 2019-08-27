package com.yaomy.control.rest.webclient;

import com.google.common.collect.Maps;
import com.yaomy.control.test.HandlerBootStrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {HandlerBootStrap.class})
public class WebClientTest {
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
        String baseUrl = "http://172.30.67.122:9000/handler/";
        WebClient webClient = WebClient.create();
        Mono<String> result = webClient.post()
                                    .uri(uriBuilder -> uriBuilder.path("/handler/client1")
                                                                .scheme("http")
                                                                .host("172.30.67.122")
                                                                .port(9000)
                                                                .queryParam("name", "李磊", "李明", "lisa")
                                                                .replaceQueryParam("name", "hhhh")
                                                                .replacePath("/handler/{path}")
                                                                .build("client"))
                                    .retrieve()
                                    .bodyToMono(String.class);
        System.out.println(result.block());

    }
}

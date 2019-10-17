package com.yaomy.control.test;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"com.yaomy.control"})
@MapperScan(basePackages = {"com.yaomy.control.*.mapper"}, sqlSessionTemplateRef = "sqlSessionTemplate")
public class HandlerBootStrap {

    @Resource(name = "defaultThreadPool")
    private ThreadPoolTaskExecutor threadPool;
    public static void main(String[] args) {
        SpringApplication.run(HandlerBootStrap.class, args);
    }

    //@Bean
    public RestTemplate restTemplate() {
        String endpoint = "tcp://127.0.0.1:5000";
        try {

            //new Thread(()->new ZeroMQServer(endPoint).start()).start();
           // new Thread(()->new ZeroMQClient(endPoint).build()).start();

        } catch (Exception e){

        }
        /**
         * 启动发布订阅模式服务端和客户端
         */
        //PubSubTest.start(endpoint);
        /**
         * 启动ZEROMQk q请求响应模式服务端和客户端
         */
        //ReqRepTest.start(endpoint);
        /**
         * 启动JAVA SOCKET服务器和客户端
         */
       // SocketTest.start(threadPool, "127.0.0.1", 5004);
        return new RestTemplate();
    }
}

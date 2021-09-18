package com.emily.infrastructure.rpc.server;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Emily
 */
@SpringBootApplication
public class RpcServerBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(RpcServerBootStrap.class, args);
    }

}

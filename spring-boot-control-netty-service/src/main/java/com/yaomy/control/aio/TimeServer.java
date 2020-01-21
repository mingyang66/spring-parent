package com.yaomy.control.aio;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Author: 姚明洋
 * @Date: 2020/1/20 17:46
 * @Version: 1.0
 */
public class TimeServer {
    public static void main(String[] args) {
        int port = 8080;
        if(args != null && args.length > 0){
            port = Integer.valueOf(args[0]);
        }

    }
}

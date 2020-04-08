package com.yaomy.control.nio;

import com.yaomy.control.nio.thread.TimeClientHandle;

/**
 * @Description: NIO CLIENT
 * @ProjectName: spring-parent
 * @Date: 2020/1/20 15:34
 * @Version: 1.0
 */
public class TimeClient {
    public static void main(String[] args) {
        int port = 8080;
        if(args != null && args.length > 0){
            port = Integer.valueOf(args[0]);
        }
        new Thread(new TimeClientHandle("172.30.67.122", port), "TimeClient-001").start();
    }
}

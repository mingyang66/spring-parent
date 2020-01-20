package com.yaomy.control.netty.nio;

import com.yaomy.control.netty.nio.thread.MultiplexerTimeServer;

/**
 * @Description: NIO SERVER
 * @ProjectName: spring-parent
 * @Author: 姚明洋
 * @Date: 2020/1/20 10:46
 * @Version: 1.0
 */
public class TimeServer {
    public static void main(String[] args) {
        int port = 8080;
        if(args != null && args.length > 0){
            port = Integer.valueOf(args[0]);
        }
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer, "NIO-MultiplexerimeServer-001").start();
    }
}

package com.yaomy.control.aio.thread;

import com.yaomy.control.aio.AcceptCompletionHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Author: 姚明洋
 * @Date: 2020/1/20 17:47
 * @Version: 1.0
 */
public class AsyncTimeServerHandler implements Runnable {

    private int port;
    public CountDownLatch latch;
    public AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    public AsyncTimeServerHandler(int port){
        this.port = port;
        try {
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("The time server is start in port : "+ port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
            latch = new CountDownLatch(1);
            doAccept();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void doAccept(){
        asynchronousServerSocketChannel.accept(this, new AcceptCompletionHandler());
    }
}

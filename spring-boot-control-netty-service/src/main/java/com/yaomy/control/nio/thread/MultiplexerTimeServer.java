package com.yaomy.control.nio.thread;

import com.sgrain.boot.common.utils.CharsetUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * @Description: 多路复用类
 * @ProjectName: spring-parent
 * @Date: 2020/1/20 10:52
 * @Version: 1.0
 */
public class MultiplexerTimeServer implements Runnable{
    private Selector selector;
    private ServerSocketChannel servChannel;
    private volatile boolean stop;

    /**
     * 初始化多路复用器，绑定监听端口
     * @param port
     */
    public MultiplexerTimeServer(int port){
        try {
            //创建多路复用器
            selector = Selector.open();
            //创建ServerSocketChannel对象
            servChannel = ServerSocketChannel.open();
            //配置当前信道为非阻塞模式
            servChannel.configureBlocking(false);
            //指定ServerSocket绑定的IP及端口号，Socket上挂起的最大请求连接数是1024
            servChannel.socket().bind(new InetSocketAddress(port), 1024);
            //使用给定的selector多路复用器注册信道，并返回选择键；监听SelectionKey.OP_ACCEPT操作位
            servChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("The time server is start in port:"+port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        this.stop = true;
    }

    @Override
    public void run() {
       while (!stop){
           try {
               //Selector轮询就绪的Key
               selector.select(1000);
               //返回Channel的SelectionKey集合
               Set<SelectionKey> selectionKeys = selector.selectedKeys();
               Iterator<SelectionKey> it = selectionKeys.iterator();
               SelectionKey key = null;
               while (it.hasNext()){
                   key = it.next();
                   it.remove();
                   try{
                       handleInput(key);
                   } catch (Exception e){
                       if(key != null){
                           key.cancel();
                           if(key.channel() != null){
                               key.channel().close();
                           }
                       }
                   }
               }
           } catch (IOException e) {
               //e.printStackTrace();
           }
       }
       //多路复用器关闭后，所有注册在上面的Channel和Pipe等资源都会被自动去注册并关闭，所以不需要重复释放资源
       if(selector != null){
           try {
               selector.close();
           } catch (IOException e) {
               e.printStackTrace();
           }
       }
    }

    private void handleInput(SelectionKey key) throws IOException {
        //判定key是否有效
        if(key.isValid()){
            //处理新接入的请求消息
            if(key.isAcceptable()){
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ);
            }
            //判定key对应的通道是否可以读取
            if(key.isReadable()){
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(readBuffer);
                if(readBytes > 0){
                    //将缓冲区当前的limit设置为position，position设置为0，用于对后续缓冲区的读取操作
                    readBuffer.flip();
                    //根据缓冲区可读的字节个数创建字节数组
                    byte[] bytes = new byte[readBuffer.remaining()];
                    //将缓冲区中可读的字节数组复制到bytes数组
                    readBuffer.get(bytes);
                    String body = new String(bytes, CharsetUtils.UTF8);
                    System.out.println("The time server receive order:"+body);
                    String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "BAD ORDER";
                    doWrite(sc, currentTime);
                } else if(readBytes < 0){
                   //链路已关闭，需要关闭SocketChannel，释放资源
                    key.cancel();
                    sc.close();
                } else {
                    //读到0字节忽略
                }
            }
        }
    }

    /**
     * 将应答消息异步发送给客户端
     */
    private void doWrite(SocketChannel channel, String response) throws IOException {
        if(response != null && response.trim().length() > 0){
            byte[] bytes = response.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();
            channel.write(writeBuffer);
        }
    }
}

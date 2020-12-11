package com.yaomy.control.zeromq.socket.server;


import com.emily.boot.common.utils.log.LoggerUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Description: JAVA SOCKET 服务端
 * @Version: 1.0
 */
public class SocketServer {
    /**
     * 端口
     */
    private int port;
    /**
     * IP地址
     */
    private String host;
    /**
     * DataOutputStream允许应用程序将java原始数据类型以可移植的方式写入到输出流中，应用程序可以使用输入流来读取数据
     */
    private DataOutputStream writer;
    /**
     * DataInputStream允许应用程序以独立于机器的方式从底层输入流读取JAVA原始类型数据，应用程序使用数据输出流来写入数据，
     * 这些数据可以由数据输入流来读取。
     * DataInputStream 对于多线程来说不是线程安全的，线程安全是可选的并且
     */
    private DataInputStream reader;
    /**
     * 线程池对象
     */
    private ThreadPoolTaskExecutor threadPool;

    public SocketServer(ThreadPoolTaskExecutor threadPool, String host, int port) {
        this.threadPool = threadPool;
        this.host = host;
        this.port = port;
    }

    /**
     * 启动SOCKET服务器
     */
    public void start(){
        try {
            ServerSocket serverSocket = new ServerSocket();
            //默认绑定0.0.0.0
            //server.bind(new InetSocketAddress(port));
            //绑定指定的IP和端口号
            serverSocket.bind(new InetSocketAddress(host, port));
            LoggerUtils.info(SocketServer.class, "SOCKET SERVER服务器启动成功...");
            while (true) {
                //server尝试接收其他Socket的连接请求，server的accept方法是阻塞式的
                Socket socket = serverSocket.accept();
                /**
                 * 启动或者关闭指定的超时时间，单位：毫秒
                 * 通过这个选项设置超时时间，一个读取调用InputStream流与套接字关联的将会阻塞指定的时间，
                 * 如果超时将会抛出ava.net.SocketTimeoutException异常，但是socket套接字仍然有效；
                 * 该选项必须在进入阻塞之前启用才会有效，超时时间必须大于0（timeout>0）
                 * 超时时间设置为0表示无限阻塞；
                 * 关闭超时时间时getSoTimeout获取的值为0；
                 */
                socket.setSoTimeout(0);
                /**
                 * 启动或者关闭SOCKET通道空闲后是否发送空包测试对等方是否还在连接，默认：false
                 */
                socket.setKeepAlive(false);
                //获取数据输出流对象
                writer = new DataOutputStream(socket.getOutputStream());
                //获取数据输入流对象
                reader = new DataInputStream(socket.getInputStream());
                //每接收到一个Socket就建立一个新的线程来处理它
                this.threadPool.execute(new ServerTask(reader, writer));
            }

        } catch (IOException e){
            LoggerUtils.error(SocketServer.class, "SOCKET SERVER服务器发生IO异常"+e.toString());
        }
    }
    /**
     * 输出流
     */
    public DataOutputStream getWriter(){
        return this.writer;
    }

    /**
     * 输入流
     */
    public DataInputStream getReader(){
        return this.reader;
    }

}

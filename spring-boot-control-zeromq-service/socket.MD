### socket套接字通信服务端和客户端详解

#### 1.socket服务端代码示例
```
package com.yaomy.control.zeromq.socket.server;

import com.yaomy.control.logback.utils.LoggerUtil;
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
            LoggerUtil.info(SocketServer.class, "SOCKET SERVER服务器启动成功...");
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
            LoggerUtil.error(SocketServer.class, "SOCKET SERVER服务器发生IO异常"+e.toString());
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
```

#### 2.socket服务端接收发送消息处理任务
```
package com.yaomy.control.zeromq.socket.server;

import com.yaomy.control.logback.utils.LoggerUtil;
import com.yaomy.control.zeromq.socket.client.SocketClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @Description: 服务端接收消息处理线程
 * @Version: 1.0
 */
public class ServerTask implements Runnable{
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

    public ServerTask(DataInputStream reader, DataOutputStream writer){
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public void run() {
        try{
            while (true){
                //读取发送数据的长度
                int len = this.reader.readInt();
                byte[] buffer = new byte[len];
                this.reader.read(buffer, 0, len);
                LoggerUtil.info(ServerTask.class, "server端接收到的数据是："+new String(buffer));
                //返回服务端接收数据成功标识
                this.writer.writeBoolean(true);
            }
        } catch (IOException e){
            LoggerUtil.error(SocketClient.class, "IO异常"+e.toString());
        }
    }
}
```

#### 3.socket客户端代码
```
package com.yaomy.control.zeromq.socket.client;

import com.yaomy.control.logback.utils.LoggerUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @Description: JAVA SOCKET通信客户端
 * @Version: 1.0
 */
public class SocketClient {
    /**
     * 端口
     */
    private int port;
    /**
     * IP地址
     */
    private String host;
    /**
     * SOCKET客户端对象
     */
    private Socket socket;
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

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
        build();
    }

    private void build(){
        try {
            //与服务端建立连接
            socket = new Socket(host, port);
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
        } catch (UnknownHostException e){
            e.printStackTrace();
            LoggerUtil.error(SocketClient.class, "IP地址不正确："+host);
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(SocketClient.class, "IO异常"+e.toString());
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

    /**
     * 释放资源
     */
    public void release(){
        try{
            this.writer.close();
            this.reader.close();
            this.socket.close();
        } catch (IOException e){
            LoggerUtil.error(SocketClient.class, "IO异常"+e.toString());
        }
    }
}
```

#### 4.测试示例启动方法
```
    public static void start(ThreadPoolTaskExecutor threadPool, String host, int port){
        try{
            //为了简单起见，所有的异常都直接往外抛
           // String host = "127.0.0.1";
           // int port = 5004;
            new Thread(()->{
                new SocketServer(threadPool, host, port).start();
            }).start();
            SocketClient client = new SocketClient(host, port);

            DataOutputStream os = client.getWriter();
            //写完以后进行读操作
            DataInputStream reader = client.getReader();
            while (true){
                String str = "你好"+System.currentTimeMillis();
                System.out.println("客户端发送数据："+str);
                byte[] msg = str.getBytes();
                os.writeInt(msg.length);
                os.write(msg);
                os.flush();
                boolean flag = reader.readBoolean();
                System.out.println("Client执行结果是："+flag);
                TimeUnit.SECONDS.sleep(1);
            }

        } catch (Exception e){

        }

    }
```

GitHub代码：[https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-zeromq-service/socket.MD](https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-zeromq-service/socket.MD)    


    
package com.yaomy.control.zeromq.pubsub.client;

import com.emily.framework.common.utils.logger.LoggerUtils;
import com.yaomy.control.zeromq.pubsub.client.task.MoniterMQTask;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * @Description: ZEROMQ订阅客户端
 * @Version: 1.0
 */
@SuppressWarnings("all")
public class ZeroMQClient {
    /**
     * 通信SOCKET套接字
     */
    private ZMQ.Socket socket;
    /**
     * 订阅端点
     */
    private String endPoint;

    public ZeroMQClient(String endPoint){
        this.endPoint = endPoint;
        build();
    }
    /**
     * @Description 跟指定端点服务器建立连接，监控事件变化，并接受消息
     * @Version  1.0
     */
    private void build(){
        /**
         * ZContext提供一种高级的ZeroMQ上下文管理类，它管理上下文中打开的SOCKET套接字，并在终止上下文之前自动关闭这些SOCKET套接字
         * 它提供一种在SOCKET套接字上设置延时超时的简单方法，并未I/O线程数配置上线文；设置进程的信号（中断）处理。
         * 默认构造函数分配给此上下文的IO线程数是1（ioThreads）
         */
        ZContext context = new ZContext();
        /**
         * 在此ZContext中创建新的托管SOCKET套接字，指定创建的套接字类型是客户端（SUB）
         */
        socket = context.createSocket(SocketType.SUB);
        //指定监控所有的事件
        socket.monitor("inproc://reqmoniter", ZMQ.EVENT_ALL);
        //启动事件监控线程
        new Thread(new MoniterMQTask(context)).start();
        /**
         * 连接到远程应用程序
         * @param addr 将要连接的端点
         */
        socket.connect(endPoint);
    }
    /**
     * @Description 订阅指定前缀prefix的消息通道
     * @Version  1.0
     */
    public boolean subscribe(byte[] prefix){
        LoggerUtils.info(ZeroMQClient.class, "订阅TOPIC:"+new String(prefix));

        return socket.subscribe(prefix);
    }
    /**
     * @Description 取消订阅指定前缀prefix的消息通道
     * @Version  1.0
     */
    public boolean unsubscribe(byte[] prefix){
        LoggerUtils.info(ZeroMQClient.class, "取消订阅TOPIC:"+new String(prefix));
        return socket.unsubscribe(prefix);
    }

    /**
     * 获取服务端SOCKET对象
     * @return
     */
    public ZMQ.Socket getSocket() {
        return this.socket;
    }
}

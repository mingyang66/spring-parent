package com.yaomy.control.zeromq.pubsub.client;

import com.google.protobuf.ByteString;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * @Description: ZEROMQ订阅客户端
 * @Version: 1.0
 */
@SuppressWarnings("all")
public class ZeroMQClient {
    private String endPoint;

    public ZeroMQClient(String endPoint){
        this.endPoint = endPoint;
    }
    public void build(){
        /**
         * ZContext提供一种高级的ZeroMQ上下文管理类，它管理上下文中打开的SOCKET套接字，并在终止上下文之前自动关闭这些SOCKET套接字
         * 它提供一种在SOCKET套接字上设置延时超时的简单方法，并未I/O线程数配置上线文；设置进程的信号（中断）处理。
         * 默认构造函数分配给此上下文的IO线程数是1（ioThreads）
         */
        ZContext context = new ZContext();
        // Socket to talk to server
        System.out.println("Connecting to hello world server…");
        /**
         * 在此ZContext中创建新的托管SOCKET套接字，指定创建的套接字类型是客户端（REQ）
         */
        ZMQ.Socket socket = context.createSocket(SocketType.SUB);

        socket.monitor("inproc://reqmoniter", ZMQ.EVENT_CONNECTED|ZMQ.EVENT_DISCONNECTED|ZMQ.EVENT_ACCEPT_FAILED|ZMQ.EVENT_MONITOR_STOPPED|ZMQ.EVENT_ACCEPTED);

        final ZMQ.Socket moniter = context.createSocket(SocketType.PAIR);
        moniter.connect("inproc://reqmoniter");
        new Thread(()->{
                while (true) {
                    ZMQ.Event event =ZMQ.Event.recv(moniter);
                    /**
                     * 连接已建立
                     */
                    if(event.getEvent() == ZMQ.EVENT_CONNECTED){
                         System.out.println("监控地址："+event.getAddress()+"--监控事件："+"连接已建立"+"--值："+event.getValue());
                    }
                    /**
                     * 同步连接失败，正在轮询，当立即连接尝试被延迟并且正在轮询其完成时，将会出发EVENT_CONNECT_DELAYED事件
                     */
                    if(event.getEvent() == ZMQ.EVENT_CONNECT_DELAYED){
                        System.out.println("监控地址："+event.getAddress()+"--监控事件："+"同步连接失败，正在轮询"+"--值："+event.getValue());
                    }
                    /**
                     * 异步连接或重连尝试，当重新连接计时器正在处理连接尝试时，EVENT_CONNECT_RETRIED 事件将会被出发，重新计算每次重连的时间间隔
                     */
                    if(event.getEvent() == ZMQ.EVENT_CONNECT_RETRIED){
                        System.out.println("监控地址："+event.getAddress()+"--监控事件："+"异步连接或重连尝试"+"--值："+event.getValue());
                    }
                    /**
                     * SOCKET绑定到地址，准备接收连接，当套接字成功绑定到端口上时，将触发EVENT_LISTENING 事件，值是新绑定套接字的FD
                     */
                    if(event.getEvent() == ZMQ.EVENT_LISTENING){
                        System.out.println("监控地址："+event.getAddress()+"--监控事件："+"SOCKET绑定到地址，准备接收连接"+"--值："+event.getValue());
                    }
                    if(event.getEvent() == ZMQ.EVENT_BIND_FAILED){
                        System.out.println("监控地址："+event.getAddress()+"--监控事件："+"异步连接或重连尝试"+"--值："+event.getValue());
                    }
                    /**
                     * 已接受绑定到端点的连接，当使用套接字的监听地址建立了远程对等方的连接时，将会触发EVENT_ACCEPTED事件
                     */
                    if(event.getEvent() == ZMQ.EVENT_ACCEPTED){
                        System.out.println("监控地址："+event.getAddress()+"--监控事件："+"异步连接或重连尝试"+"--值："+event.getValue());
                    }
                    /**
                     * 无法接受客户端的连接，当连接到套接字的绑定失败时，将会触发EVENT_ACCEPT_FAILED事件
                     */
                    if(event.getEvent() == ZMQ.EVENT_ACCEPT_FAILED){
                        System.out.println("监控地址："+event.getAddress()+"--监控事件："+"无法接受客户端的连接"+"--值："+event.getValue());
                    }
                    /**
                     * 连接关闭，当连接的底层描述符已经关闭时，将会触发EVENT_CLOSED事件
                     */
                    if(event.getEvent() == ZMQ.EVENT_CLOSED){
                        System.out.println("监控地址："+event.getAddress()+"--监控事件："+"连接关闭"+"--值："+event.getValue());
                    }
                    /**
                     * 无法关闭事件，当无法将描述符释放会操作系统时，将会触发EVENT_CLOSE_FAILED事件，实现说明：仅适用于IPC套接字
                     */
                    if(event.getEvent() == ZMQ.EVENT_CLOSE_FAILED){
                        System.out.println("监控地址："+event.getAddress()+"--监控事件："+"异步连接或重连尝试"+"--值："+event.getValue());
                    }
                    /**
                     * 中断会话，当流引擎（特定于TCP/IPC）监测到损坏/断开的会话时，将会触发EVENT_DISCONNECTED事件
                     */
                    if(event.getEvent() == ZMQ.EVENT_DISCONNECTED){
                        System.out.println("监控地址："+event.getAddress()+"--监控事件："+"中断会话"+"--值："+event.getValue());
                    }
                    /**
                     * SOCKET监视器停止，当SOCKET套接字的监视器停止时，将会触发EVENT_MONITOR_STOPPED事件
                     */
                    if(event.getEvent() == ZMQ.EVENT_MONITOR_STOPPED){
                        System.out.println("监控地址："+event.getAddress()+"--监控事件："+"SOCKET监视器停止"+"--值："+event.getValue());
                    }
                    /**
                     * 已成功协商协议，当流引擎（TCP/IPC）成功与对等方协商协议时，将会触发EVENT_HANDSHAKE_PROTOCOL事件
                     */
                    if(event.getEvent() == ZMQ.EVENT_HANDSHAKE_PROTOCOL){
                        System.out.println("监控地址："+event.getAddress()+"--监控事件："+"已成功协商协议"+"--值："+event.getValue());
                    }
                }
        }).start();
        /**
         * 连接到远程应用程序
         * @param addr 将要连接的端点
         */
        socket.connect(endPoint);
        System.out.println("send_time_out:"+socket.getSendTimeOut()+", recv_time_out:"+socket.getReceiveTimeOut());
        /**
         * subscribe会在ZMQ_SUB套接字上建立新的消息筛选器，新建立的ZMQ_SUB应过滤掉所有传入消息，因此应该调用此选项建立初始筛选器；
         * 长度为零的空选项值应订阅所有的传入消息，即空字符串subscribe.subscribe("");
         * 非空的选项值应订阅以指定前缀开头的所有消息；
         * 多个过滤器可以连接到单个ZMQ_SUB套接字上，在这种情况下，如果消息与至少一个过滤器匹配，则应该接受此消息
         */
    /*    subscribe.subscribe("1");
        subscribe.subscribe("2");*/
        socket.subscribe("");
        //subscribe.subscribe("6".getBytes());
        /*subscribe.subscribe("10");
        subscribe.subscribe("11");
        subscribe.subscribe("12");
        subscribe.subscribe("13");*/
        while(true){
            System.out.println("----------------------start------------------------");
            /**
             * 接收消息，如果没有接收到消息会一直阻塞等待，直到超时返回null
             * @param flags 接收消息的标记
             */
            try{
                byte[] msg = socket.recv();
                ByteString byteString = ByteString.copyFrom(msg);
                System.out.println(new String(byteString.toByteArray()));
               /* BytesValue bytesValue = BytesValue.parseFrom(msg);
                System.out.println(new String(bytesValue.toByteString().toByteArray()));*/
               /* String s = SerializationUtils.deserialize(msg);
                System.out.println(s);*/

            } catch (Exception e){
                System.out.println("---------------exception--------------------");
            }
           // System.out.println(socket.recvStr());
        }

    }
}

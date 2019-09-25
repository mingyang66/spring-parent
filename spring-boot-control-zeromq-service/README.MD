### zeromq发布订阅模式及监控monitor

#### 1.zeromq服务端示例代码
```
package com.yaomy.control.zeromq.pubsub.server;

import com.yaomy.control.logback.utils.LoggerUtil;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * @Description: 服务端
 * @Version: 1.0
 */
public class ZeroMQServer {
    private String endPoint;

    public ZeroMQServer(String endPoint) {
        this.endPoint = endPoint;
    }

    public void start() {
        /**
         * ZContext提供一种高级的ZeroMQ上下文管理类，它管理上下文中打开的SOCKET套接字，并在终止上下文之前自动关闭这些SOCKET套接字
         * 它提供一种在SOCKET套接字上设置延时超时的简单方法，并未I/O线程数配置上线文；设置进程的信号（中断）处理。
         * 默认构造函数分配给此上下文的IO线程数是1（ioThreads）
         */
        ZContext context = new ZContext();
        /**
         * 在此ZContext中创建新的托管SOCKET套接字，指定创建的套接字类型是服务端（PUB）
         */
        ZMQ.Socket socket = context.createSocket(SocketType.PUB);
        /**
         * 绑定到网络端口，开始监听新的连接
         */
        socket.bind(endPoint);

        LoggerUtil.info(ZeroMQServer.class, "ZeroMQServer服务端启动成功...");
        while (true) {
            String msg = "Time:给订阅客户端的回复："+System.currentTimeMillis();
            /**
             * ZMQ_SNDMORE 指定发送的消息是一个多部分组成消息，接下来是更多的消息；ZMQ消息由一个或者多个组成，ZMQ确保消息的原子性传递，对等方要么接收消息的所有部分，
             * 要么根本不接收任何消息部分，除可用内存外，消息部分的总数不受限制。
             * send方法如果发送成功将会返回true，否则将会返回false
             * 参考：http://api.zeromq.org/4-1:zmq-send
             */
            socket.send(msg);
        }
    }

}
```

#### 2.zeromq客户端示例代码
```
package com.yaomy.control.zeromq.pubsub.client;

import com.yaomy.control.logback.utils.LoggerUtil;
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
        LoggerUtil.info(ZeroMQClient.class, "订阅TOPIC:"+new String(prefix));

        return socket.subscribe(prefix);
    }
    /**
     * @Description 取消订阅指定前缀prefix的消息通道
     * @Version  1.0
     */
    public boolean unsubscribe(byte[] prefix){
        LoggerUtil.info(ZeroMQClient.class, "取消订阅TOPIC:"+new String(prefix));
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
```

#### 3.mq客户端事件监听线程任务
```
package com.yaomy.control.zeromq.pubsub.client.task;

import com.yaomy.control.logback.utils.LoggerUtil;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * @Description: 监控ZEROMQ事件
 * @Version: 1.0
 */
public class MoniterMQTask implements Runnable{

    private ZContext context;

    public MoniterMQTask(ZContext context){
        this.context = context;
    }

    @Override
    public void run() {
        LoggerUtil.info(MoniterMQTask.class, "启动监控ZEROMQ事件线程...");
        final ZMQ.Socket moniter = context.createSocket(SocketType.PAIR);
        moniter.connect("inproc://reqmoniter");
        while (true) {
            ZMQ.Event event =ZMQ.Event.recv(moniter);
            //事件类型
            int type = event.getEvent();
            switch (type){
                /**
                 * 连接已建立
                 */
                case ZMQ.EVENT_CONNECTED:
                    LoggerUtil.info(MoniterMQTask.class, "监控地址："+event.getAddress()+"--监控事件："+"连接已建立"+"--值："+event.getValue());
                    break;
                /**
                 * 同步连接失败，正在轮询，当立即连接尝试被延迟并且正在轮询其完成时，将会出发EVENT_CONNECT_DELAYED事件
                 */
                case ZMQ.EVENT_CONNECT_DELAYED:
                    LoggerUtil.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"同步连接失败，正在轮询"+"--值："+event.getValue());
                    break;
                /**
                 * 异步连接或重连尝试，当重新连接计时器正在处理连接尝试时，EVENT_CONNECT_RETRIED 事件将会被出发，重新计算每次重连的时间间隔
                 */
                case ZMQ.EVENT_CONNECT_RETRIED:
                    LoggerUtil.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"异步连接或重连尝试"+"--值："+event.getValue());
                    break;
                /**
                 * SOCKET绑定到地址，准备接收连接，当套接字成功绑定到端口上时，将触发EVENT_LISTENING 事件，值是新绑定套接字的FD
                 */
                case ZMQ.EVENT_LISTENING:
                    LoggerUtil.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"SOCKET绑定到地址，准备接收连接"+"--值："+event.getValue());
                    break;
                case ZMQ.EVENT_BIND_FAILED:
                    LoggerUtil.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"异步连接或重连尝试"+"--值："+event.getValue());
                    break;
                /**
                 * 已接受绑定到端点的连接，当使用套接字的监听地址建立了远程对等方的连接时，将会触发EVENT_ACCEPTED事件
                 */
                case ZMQ.EVENT_ACCEPTED:
                    LoggerUtil.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"异步连接或重连尝试"+"--值："+event.getValue());
                    break;
                /**
                 * 无法接受客户端的连接，当连接到套接字的绑定失败时，将会触发EVENT_ACCEPT_FAILED事件
                 */
                case ZMQ.EVENT_ACCEPT_FAILED:
                    LoggerUtil.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"无法接受客户端的连接"+"--值："+event.getValue());
                    break;
                /**
                 * 连接关闭，当连接的底层描述符已经关闭时，将会触发EVENT_CLOSED事件
                 */
                case ZMQ.EVENT_CLOSED:
                    LoggerUtil.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"连接关闭"+"--值："+event.getValue());
                    break;
                /**
                 * 无法关闭事件，当无法将描述符释放会操作系统时，将会触发EVENT_CLOSE_FAILED事件，实现说明：仅适用于IPC套接字
                 */
                case ZMQ.EVENT_CLOSE_FAILED:
                    LoggerUtil.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"异步连接或重连尝试"+"--值："+event.getValue());
                    break;
                /**
                 * 中断会话，当流引擎（特定于TCP/IPC）监测到损坏/断开的会话时，将会触发EVENT_DISCONNECTED事件
                 */
                case ZMQ.EVENT_DISCONNECTED:
                    LoggerUtil.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"中断会话"+"--值："+event.getValue());
                    break;
                /**
                 * SOCKET监视器停止，当SOCKET套接字的监视器停止时，将会触发EVENT_MONITOR_STOPPED事件
                 */
                case ZMQ.EVENT_MONITOR_STOPPED:
                    LoggerUtil.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"SOCKET监视器停止"+"--值："+event.getValue());
                    break;
                /**
                 * 已成功协商协议，当流引擎（TCP/IPC）成功与对等方协商协议时，将会触发EVENT_HANDSHAKE_PROTOCOL事件
                 */
                case ZMQ.EVENT_HANDSHAKE_PROTOCOL:
                    LoggerUtil.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"已成功协商协议"+"--值："+event.getValue());
                    break;
                default:
                    break;
            }
        }
    }
}
```

#### 4.客户端消息接收线程
```
package com.yaomy.control.zeromq.pubsub.client.task;

import com.yaomy.control.logback.utils.LoggerUtil;
import org.zeromq.ZMQ;

/**
 * @Description: ZEROMQ接收订阅TOPIC通道消息
 * @Version: 1.0
 */
public class MsgRecvTask implements Runnable {
    /**
     * SOCKET对象
     */
    private ZMQ.Socket socket;

    public MsgRecvTask(ZMQ.Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        LoggerUtil.info(MsgRecvTask.class, "启动ZEROMQ接收订阅TOPIC通道消息...");
        while (true){
            LoggerUtil.info(MsgRecvTask.class, "接收到数据："+socket.recvStr());
        }
    }
}
```

#### 5.测试启动方法
```
   public static void start(String endpoint){
        //启动服务端
        new Thread(()->{
            new ZeroMQServer(endpoint).start();
        }).start();

        ZeroMQClient client = new ZeroMQClient(endpoint);
        //启动接收消息线程
        new Thread(new MsgRecvTask(client.getSocket())).start();

        client.subscribe("Time:".getBytes());
    }
```
    
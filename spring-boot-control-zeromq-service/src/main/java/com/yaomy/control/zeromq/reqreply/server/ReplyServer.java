package com.yaomy.control.zeromq.reqreply.server;

import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * @Description: 请求应答模式--server
 * @Version: 1.0
 */
@SuppressWarnings("all")
public class ReplyServer {

    public static void main(String[] args) {
        /**
         * ZContext提供一种高级的ZeroMQ上下文管理类，它管理上下文中打开的SOCKET套接字，并在终止上下文之前自动关闭这些SOCKET套接字
         * 它提供一种在SOCKET套接字上设置延时超时的简单方法，并未I/O线程数配置上线文；设置进程的信号（中断）处理。
         * 默认构造函数分配给此上下文的IO线程数是1（ioThreads）
         */
        ZContext context = new ZContext(1);
        /**
         * 在此ZContext中创建新的托管SOCKET套接字，指定创建的套接字类型是服务端（REP）
         */
        ZMQ.Socket socket = context.createSocket(ZMQ.REP);
        /**
         * 绑定到网络端口，开始监听新的连接
         */
        socket.bind("tcp://127.0.0.1:5554");
        /**
         * 设置SOCKET套接字发送时的超时时间，
         * 如果为0，则send将立即返回，如果无法发送消息，则返回false
         * 如果是-1，则它将阻塞，直到发送消息为止
         * 对于其他的值，它将尝试在返回false和EAGAIN之前的指端时间发送消息
         * 单位：毫秒（milliseconds）
         * 默认值：-1
         */
        socket.setSendTimeOut(-1);
        /**
         * 设置SOCKET套接字接收的超时时间
         * 如果为0，则recv将立即返回，如果没有要接收的消息，则返回null
         * 如果为-1，它将阻塞，直到接收到消息为止
         * 对于其他的值，它将等待在返回null和EAGAIN错误之前接收消息
         * 单位：毫秒（milliseconds）
         * 默认：-1
         */
        socket.setReceiveTimeOut(-1);
        /**
         * 定义Socket套接字是否作为纯服务器以实现简单的安全性
         * 值为true表示Socket将作为纯服务器
         * 值为false表示Socket不会充当纯服务器
         * 其安全性则取决于其它选项设置
         * @link{setPlainUsername}@link{setPlainPassword}
         * 查看：http://api.zeromq.org/4-2:zmq-plain
         */
        socket.setAsServerPlain(false);
        /**
         * 设置通过TCP或者IPC传输的用户名
         */
        socket.setPlainUsername("user");
        /**
         * 设置通过TCP或者IPC传输的密码
         */
        socket.setPlainPassword("123");
        /**
         * 曲线（CURVE）机制定义了客户端和服务端通信的安全身份验证和机密性机制，曲线（CURVE）用于公共网络环境；
         * 客户端和服务端角色
         * 使用曲线（CURVE）的SOCKET可以是client也可以是server端，但是不能同时是两者，角色独立于绑定或连接的方向；
         * 套接字可以通过设置新选项在任何时候更改角色，该角色会影响其后面的所有zmq_connect和zmq_bind调用；
         * 要成为曲线（CURVE）服务器，应用程序在套接字上设置ZMQ_CURVE_SERVER{@link setAsServerCurve},然后设置ZMQ_CURVE_SECRETKEY
         * 提供长期的私钥{@link setCurveSecretKey},服务端不停工长期的公钥，该公钥仅由客户端使用。
         * 参考：http://api.zeromq.org/4-2:zmq-curve
         */
        socket.setAsServerCurve(false);
        socket.setCurveSecretKey("sdf".getBytes());
        System.out.println("send_time_out:"+socket.getSendTimeOut()+", recv_time_out:"+socket.getReceiveTimeOut());;
        System.out.println("-----------------start-------------------------");
        while (!Thread.currentThread().isInterrupted()) {
            /**
             * 接收消息，如果没有接收到消息会一直阻塞等待，直到超时返回null
             * @param flags 接收消息的标记
             */
            byte[] reply = socket.recv(0);

            // Print the message
            System.out.println("Received: [" + new String(reply, ZMQ.CHARSET) + "]");

            // Send a response
            String response = "Hello, world!";
            /**
             * 发送消息到指定的标记
             * @param data 消息
             * @param flags 发送消息的标记
             */
            socket.send(response.getBytes(ZMQ.CHARSET), 0);
        }
        System.out.println("-----------------end-------------------------");
    }
}

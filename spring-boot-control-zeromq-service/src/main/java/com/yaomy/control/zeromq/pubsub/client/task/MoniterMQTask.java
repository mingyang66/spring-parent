package com.yaomy.control.zeromq.pubsub.client.task;

import com.emily.framework.common.utils.log.LoggerUtils;
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
        LoggerUtils.info(MoniterMQTask.class, "启动监控ZEROMQ事件线程...");
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
                    LoggerUtils.info(MoniterMQTask.class, "监控地址："+event.getAddress()+"--监控事件："+"连接已建立"+"--值："+event.getValue());
                    break;
                /**
                 * 同步连接失败，正在轮询，当立即连接尝试被延迟并且正在轮询其完成时，将会出发EVENT_CONNECT_DELAYED事件
                 */
                case ZMQ.EVENT_CONNECT_DELAYED:
                    LoggerUtils.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"同步连接失败，正在轮询"+"--值："+event.getValue());
                    break;
                /**
                 * 异步连接或重连尝试，当重新连接计时器正在处理连接尝试时，EVENT_CONNECT_RETRIED 事件将会被出发，重新计算每次重连的时间间隔
                 */
                case ZMQ.EVENT_CONNECT_RETRIED:
                    LoggerUtils.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"异步连接或重连尝试"+"--值："+event.getValue());
                    break;
                /**
                 * SOCKET绑定到地址，准备接收连接，当套接字成功绑定到端口上时，将触发EVENT_LISTENING 事件，值是新绑定套接字的FD
                 */
                case ZMQ.EVENT_LISTENING:
                    LoggerUtils.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"SOCKET绑定到地址，准备接收连接"+"--值："+event.getValue());
                    break;
                case ZMQ.EVENT_BIND_FAILED:
                    LoggerUtils.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"异步连接或重连尝试"+"--值："+event.getValue());
                    break;
                /**
                 * 已接受绑定到端点的连接，当使用套接字的监听地址建立了远程对等方的连接时，将会触发EVENT_ACCEPTED事件
                 */
                case ZMQ.EVENT_ACCEPTED:
                    LoggerUtils.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"异步连接或重连尝试"+"--值："+event.getValue());
                    break;
                /**
                 * 无法接受客户端的连接，当连接到套接字的绑定失败时，将会触发EVENT_ACCEPT_FAILED事件
                 */
                case ZMQ.EVENT_ACCEPT_FAILED:
                    LoggerUtils.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"无法接受客户端的连接"+"--值："+event.getValue());
                    break;
                /**
                 * 连接关闭，当连接的底层描述符已经关闭时，将会触发EVENT_CLOSED事件
                 */
                case ZMQ.EVENT_CLOSED:
                    LoggerUtils.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"连接关闭"+"--值："+event.getValue());
                    break;
                /**
                 * 无法关闭事件，当无法将描述符释放会操作系统时，将会触发EVENT_CLOSE_FAILED事件，实现说明：仅适用于IPC套接字
                 */
                case ZMQ.EVENT_CLOSE_FAILED:
                    LoggerUtils.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"异步连接或重连尝试"+"--值："+event.getValue());
                    break;
                /**
                 * 中断会话，当流引擎（特定于TCP/IPC）监测到损坏/断开的会话时，将会触发EVENT_DISCONNECTED事件
                 */
                case ZMQ.EVENT_DISCONNECTED:
                    LoggerUtils.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"中断会话"+"--值："+event.getValue());
                    break;
                /**
                 * SOCKET监视器停止，当SOCKET套接字的监视器停止时，将会触发EVENT_MONITOR_STOPPED事件
                 */
                case ZMQ.EVENT_MONITOR_STOPPED:
                    LoggerUtils.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"SOCKET监视器停止"+"--值："+event.getValue());
                    break;
                /**
                 * 已成功协商协议，当流引擎（TCP/IPC）成功与对等方协商协议时，将会触发EVENT_HANDSHAKE_PROTOCOL事件
                 */
                case ZMQ.EVENT_HANDSHAKE_PROTOCOL:
                    LoggerUtils.info(MoniterMQTask.class,"监控地址："+event.getAddress()+"--监控事件："+"已成功协商协议"+"--值："+event.getValue());
                    break;
                default:
                    break;
            }
        }
    }
}

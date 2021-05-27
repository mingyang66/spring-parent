package com.yaomy.control.zeromq.pubsub.client.task;


import com.emily.infrastructure.logback.common.LoggerUtils;
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
        LoggerUtils.info(MsgRecvTask.class, "启动ZEROMQ接收订阅TOPIC通道消息...");
        while (true){
            LoggerUtils.info(MsgRecvTask.class, "接收到数据："+socket.recvStr());
        }
    }
}

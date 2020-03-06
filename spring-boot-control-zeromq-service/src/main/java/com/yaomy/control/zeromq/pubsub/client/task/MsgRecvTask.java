package com.yaomy.control.zeromq.pubsub.client.task;

import com.yaomy.sgrain.logback.utils.LoggerUtil;
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

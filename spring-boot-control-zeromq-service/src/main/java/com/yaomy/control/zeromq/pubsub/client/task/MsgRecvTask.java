package com.yaomy.control.zeromq.pubsub.client.task;


import com.emily.infrastructure.logback.factory.LoggerFactory;
import org.slf4j.Logger;
import org.zeromq.ZMQ;

/**
 * @Description: ZEROMQ接收订阅TOPIC通道消息
 * @Version: 1.0
 */
public class MsgRecvTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MsgRecvTask.class);
    /**
     * SOCKET对象
     */
    private ZMQ.Socket socket;

    public MsgRecvTask(ZMQ.Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        logger.info("启动ZEROMQ接收订阅TOPIC通道消息...");
        while (true) {
            logger.info("接收到数据：" + socket.recvStr());
        }
    }
}

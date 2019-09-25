package com.yaomy.control.test.zeromq;

import com.yaomy.control.zeromq.pubsub.client.ZeroMQClient;
import com.yaomy.control.zeromq.pubsub.client.task.MsgRecvTask;
import com.yaomy.control.zeromq.pubsub.server.ZeroMQServer;

/**
 * @Description: 发布订阅模式
 * @Version: 1.0
 */
public class PubSubTest {
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
}

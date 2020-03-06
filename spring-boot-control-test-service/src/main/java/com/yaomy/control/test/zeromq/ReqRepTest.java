package com.yaomy.control.test.zeromq;

import com.yaomy.sgrain.logback.utils.LoggerUtil;
import com.yaomy.control.zeromq.reqreply.client.RequestClient;
import com.yaomy.control.zeromq.reqreply.server.ReplyServer;
import org.zeromq.ZMQ;


/**
 * @Description: ZEROMQ 请求响应测试
 * @Version: 1.0
 */
public class ReqRepTest {

    public static void start(String endpoint){
       new Thread(()->{
          new ReplyServer(endpoint).start();
       }).start();

        ZMQ.Socket socket = new RequestClient(endpoint).build();
        while (true){
            socket.send("ZEROMQ发送请求应答模式消息..."+System.currentTimeMillis());
            byte[] msg = socket.recv();
            LoggerUtil.info(ReqRepTest.class, "ZEROMQ接收到消息是："+new String(msg));

        }
    }
}

package com.yaomy.control.test.zeromq;

import com.yaomy.control.zeromq.socket.client.SocketClient;
import com.yaomy.control.zeromq.socket.server.SocketServer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.concurrent.TimeUnit;

/**
 * @Description: Java Socket通信
 * @Version: 1.0
 */
public class SocketTest {
    /**
     * 测试示例
     * @throws Exception
     */
    public static void start(ThreadPoolTaskExecutor threadPool, String host, int port){
        try{
            //为了简单起见，所有的异常都直接往外抛
           // String host = "127.0.0.1";
           // int port = 5004;
            new Thread(()->{
                new SocketServer(threadPool, host, port).start();
            }).start();
            SocketClient client = new SocketClient(host, port);

            DataOutputStream os = client.getWriter();
            //写完以后进行读操作
            DataInputStream reader = client.getReader();
            while (true){
                String str = "你好"+System.currentTimeMillis();
                System.out.println("客户端发送数据："+str);
                byte[] msg = str.getBytes();
                os.writeInt(msg.length);
                os.write(msg);
                os.flush();
                boolean flag = reader.readBoolean();
                System.out.println("Client执行结果是："+flag);
                TimeUnit.SECONDS.sleep(1);
            }

        } catch (Exception e){

        }

    }
}

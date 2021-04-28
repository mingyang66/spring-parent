package com.yaomy.control.zeromq.socket.server;


import com.emily.framework.autoconfigure.logger.common.LoggerUtils;
import com.yaomy.control.zeromq.socket.client.SocketClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @Description: 服务端接收消息处理线程
 * @Version: 1.0
 */
public class ServerTask implements Runnable{
    /**
     * DataOutputStream允许应用程序将java原始数据类型以可移植的方式写入到输出流中，应用程序可以使用输入流来读取数据
     */
    private DataOutputStream writer;
    /**
     * DataInputStream允许应用程序以独立于机器的方式从底层输入流读取JAVA原始类型数据，应用程序使用数据输出流来写入数据，
     * 这些数据可以由数据输入流来读取。
     * DataInputStream 对于多线程来说不是线程安全的，线程安全是可选的并且
     */
    private DataInputStream reader;

    public ServerTask(DataInputStream reader, DataOutputStream writer){
        this.reader = reader;
        this.writer = writer;
    }

    @Override
    public void run() {
        try{
            while (true){
                //读取发送数据的长度
                int len = this.reader.readInt();
                byte[] buffer = new byte[len];
                this.reader.read(buffer, 0, len);
                LoggerUtils.info(ServerTask.class, "server端接收到的数据是："+new String(buffer));
                //返回服务端接收数据成功标识
                this.writer.writeBoolean(true);
            }
        } catch (IOException e){
            LoggerUtils.error(SocketClient.class, "IO异常"+e.toString());
        }
    }
}

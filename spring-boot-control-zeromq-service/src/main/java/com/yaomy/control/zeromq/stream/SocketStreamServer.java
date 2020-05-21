package com.yaomy.control.zeromq.stream;

import com.sgrain.boot.common.utils.CharsetUtils;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.io.UnsupportedEncodingException;

/**
 * @Description: Description
 * @Version: 1.0
 */
public class SocketStreamServer {
    /**
     * 端口
     */
    private String endpoint;

    public SocketStreamServer(String endpoint) {
        this.endpoint = endpoint;
    }

    public void start() {
        ZContext context = new ZContext();
        /**
         * 指定STREAM流套接字标志；
         * STREAM类型套接字用于从非ZEROMQ对等机发送或者接收TCP数据；
         * 当使用tcp:// transport时，SOCKET可以作为client或者server端，异步发送或者接收TCP数据；
         * 当接收到TCP数据时，流套接字在将消息传递给应用程序之前，应预先准备一个消息部分，其中包含消息发起对等方的标识；
         * 接收到的消息在所有连接的对等方之间公平的排队；
         * 当发送TCP数据时，流套接字应移除消息的第一部分，并使用它来确定消息应路由到的对等方身份，不可发布的消息应导致EHOSTUNREACH或EAGAIN错误；
         * 打开服务连接需要调用{@link ZMQ.Socket#connect(String)},然后调用{@link ZMQ.Socket#getIdentity()}获取SOCKET套接字标识；
         * 若要关闭特定连接，请发送标识帧，后跟零长度消息。
         * 当建立连接时，应用程序将收到零长度的消息。同样的
         */
        ZMQ.Socket socket = context.createSocket(SocketType.STREAM);
        //绑定端点
        socket.bind(endpoint);
        while (true){
            try{
                System.out.println("----------------start-------------------");
                byte[] s = socket.recv();
                byte[] buffer = new byte[s[0]];
                socket.recv(buffer, 0, s[0], 0);
                System.out.println("Server端接收到数据："+new String(buffer, CharsetUtils.UTF_8));
                socket.send("nihaoa".getBytes());
                System.out.println("----------------end-------------------");
            } catch (UnsupportedEncodingException e){

            }
        }
    }

}

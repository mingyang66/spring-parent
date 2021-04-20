package com.yaomy.control.zeromq.stream;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

/**
 * @Description: Description
 * @Version: 1.0
 */
public class SocketStreamClient {
    /**
     * 端口
     */
    private String endpoint;

    public SocketStreamClient(String endpoint) {
        this.endpoint = endpoint;
    }

    public void build() {
        ZContext context = new ZContext();
        /**
         * 指定STREAM流套接字标志；
         * STREAM类型套接字用于从非ZEROMQ对等机发送或者接收TCP数据；
         * 当使用tcp:// transport时，SOCKET可以作为client或者server端，异步发送或者接收TCP数据；
         * 当接收到TCP数据时，流套接字在将消息传递给应用程序之前，应预先准备一个消息部分，其中包含消息发起对等方的标识；
         * 接收到的消息在所有连接的对等方之间公平的排队；
         * 当发送TCP数据时，流套接字应移除消息的第一部分，并使用它来确定消息应路由到的对等方身份，不可发布的消息应导致EHOSTUNREACH或EAGAIN错误；
         * 打开服务连接需要调用{@link org.zeromq.ZMQ.Socket#connect(String)},然后调用{@link org.zeromq.ZMQ.Socket#getIdentity()}获取SOCKET套接字标识；
         * 若要关闭特定连接，请发送标识帧，后跟零长度消息。
         * 当建立连接时，应用程序将收到零长度的消息。同样的
         */
        ZMQ.Socket socket = context.createSocket(SocketType.STREAM);
        //连接指定端点
        socket.connect(endpoint);
        while (true){
            try{
                System.out.println("----------------start-------------------");
                byte[] c = "你好".getBytes();
                byte d = (byte) c.length;
                socket.send(new byte[]{d});
                socket.send(c);
                String s = socket.recvStr();
                System.out.println(s);
                System.out.println("----------------end-------------------");
            }catch (Exception e){

            }
        }
    }
    public static void main(String[] args) {
        String endpoint = "tcp://127.0.0.1:5004";
        /*new Thread(()->{
            new SocketStreamServer(endpoint).start();
        }).start();*/
        new SocketStreamClient(endpoint).build();

    }
}

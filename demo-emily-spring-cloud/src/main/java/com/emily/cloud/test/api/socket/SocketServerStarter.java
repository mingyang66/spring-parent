package com.emily.cloud.test.api.socket;

import com.emily.infrastructure.common.type.DateFormatType;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Emily
 * @program: spring-parent
 * @description: socket监听器
 * @create: 2021/03/04
 */
public class SocketServerStarter {
    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(8888);
            while (true) {
                Socket a1 = ss.accept();
                InputStream is = a1.getInputStream();
                byte[] bytes = new byte[1024];
                int len = is.read(bytes);
                System.out.println("服务器端接收到：" + new String(bytes, 0, len));
                OutputStream os = a1.getOutputStream();
                String now = "我是服务器，当前时间是：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatType.YYYY_MM_DD_HH_MM_SS_SSS.getFormat()));
                os.write(now.getBytes());
                os.flush();
            }

        } catch (Exception e) {

        }
    }
}

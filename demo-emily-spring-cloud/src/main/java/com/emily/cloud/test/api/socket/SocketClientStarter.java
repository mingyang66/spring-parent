package com.emily.cloud.test.api.socket;

import com.emily.infrastructure.date.DateConvertUtils;
import com.emily.infrastructure.date.DatePatternInfo;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;

/**
 * @author Emily
 * @program: spring-parent
 *  客户端
 * @since 2021/03/04
 */
public class SocketClientStarter {
    public static void main(String[] args) {
        try {
            Socket sc = new Socket("172.30.67.122", 8888);
            OutputStream out = sc.getOutputStream();
            String now = "我是客户端，当前时间是：" + DateConvertUtils.format(LocalDateTime.now(), DatePatternInfo.YYYY_MM_DD_HH_MM_SS_SSS);
            out.write(now.getBytes());
            InputStream is = sc.getInputStream();
            byte[] bytes = new byte[1024];
            int len = is.read(bytes);
            System.out.println("客户端接收到：" + new String(bytes, 0, len));
            sc.close();
        } catch (Exception e) {

        }
    }
}

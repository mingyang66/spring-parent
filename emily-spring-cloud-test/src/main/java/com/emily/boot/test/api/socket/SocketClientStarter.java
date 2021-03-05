package com.emily.boot.test.api.socket;

import com.emily.framework.common.enums.DateFormatEnum;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @program: spring-parent
 * @description: 客户端
 * @create: 2021/03/04
 */
public class SocketClientStarter {
    public static void main(String[] args) {
        try {
            Socket sc=new Socket("172.30.67.122",8888);
            OutputStream Out = sc.getOutputStream();
            String now = "我是客户端，当前时间是："+ LocalDateTime.now().format(DateTimeFormatter.ofPattern(DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat()));
            Out.write(now.getBytes());
            InputStream is = sc.getInputStream();
            byte[] bytes=new byte[1024];
            int len=is.read(bytes);
            System.out.println("客户端接收到："+new String(bytes,0,len));
            sc.close();
        } catch (Exception e){

        }
    }
}

package com.sgrain.boot.common.servlet;

import com.sgrain.boot.common.utils.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * @Description: 对HttpServletRequest进行重写，用来接收application/json参数数据类型，即@RequestBody注解标注的参数,解决多次读取问题
 * @Author: 姚明洋
 * @create: 2020/8/19
 */
public class RequestWrapper extends HttpServletRequestWrapper {
    //参数字节数组
    private byte[] body;
    //Http请求对象
    private HttpServletRequest request;

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.request = request;
    }

    /**
     * 获取请求参数对象的包装流
     *
     * @return
     * @throws IOException
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        /**
         * 每次调用此方法时将数据流中的数据读取出来，然后再回填到InputStream之中
         * 还有一个问题是下面这段代码网上有很多是写在构造函数中的，通常情况是没有问题的但是如果使用POST方式@ReqeustParam注解接收参数控制器是拿不到参数的；
         *
         */
        if (null == this.body) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(request.getInputStream(), baos);
            this.body = baos.toByteArray();
        }

        final ByteArrayInputStream bais = new ByteArrayInputStream(body);
        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }

            @Override
            public int read() {
                return bais.read();
            }
        };
    }

    public byte[] getBody() {
        return body;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
}
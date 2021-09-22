package com.emily.infrastructure.rpc.client;

import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.rpc.core.decoder.MyMessageDecoder;
import com.emily.infrastructure.rpc.core.encoder.MyMessageEncoder;
import com.emily.infrastructure.rpc.core.entity.ClassInfo;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @program: spring-parent
 * @description: 创建Netty客户端及自定义处理器
 * @author: Emily
 * @create: 2021/09/17
 */
public class NettyClient {
    private static NettyClientHandler nettyClientHandler;
    static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(3, 5, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10));

    public static <T> T getBean(Class<T> service) {
        String simpleName = service.getSimpleName();
        return getBean(service, simpleName);
    }

    /**
     * 获取一个动态代理对象
     *
     * @param target
     * @param className
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> target, String className) {
        return (T) Proxy.newProxyInstance(target.getClassLoader(), new Class<?>[]{target}, ((proxy, method, args1) -> {
            //组装传输类的属性值
            ClassInfo classInfo = new ClassInfo(className, method.getName(), method.getParameterTypes(), args1);
            nettyClientHandler.setClassInfo(classInfo);
            //运行线程，发送数据
            Future future = threadPool.submit(nettyClientHandler);
            //返回结果
            String o1 = (String) future.get();
            //获取返回类型，并将服务端返回的json数据转化为对应的类型
            Type returnType = method.getAnnotatedReturnType().getType();
            Object o2 = JSONUtils.toJavaBean(o1, (Class<?>) returnType);
            return o2;
        }));
    }

    /**
     * 启动netty客户端
     *
     * @param host
     * @param port
     */
    public static void start(String host, int port) {
        nettyClientHandler = new NettyClientHandler();
        //客户端需要一个事件循环组就可以
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        try {
            //创建客户端的启动对象 bootstrap ，不是 serverBootStrap
            Bootstrap bootstrap = new Bootstrap();
            //设置相关参数,设置线程组
            bootstrap.group(group)
                    //设置客户端通道的实现数 （反射）
                    .channel(NioSocketChannel.class)
                    //加入自己的处理器
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new MyMessageDecoder())
                                    .addLast(new MyMessageEncoder())
                                    .addLast(nettyClientHandler);
                        }
                    });
            System.out.println("客户端 ready is ok..");
            //连接服务器
            final ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            //对关闭通道进行监听
//            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
//            group.shutdownGracefully();
        }
    }
}

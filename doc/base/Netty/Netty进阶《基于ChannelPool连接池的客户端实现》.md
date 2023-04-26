#### Netty进阶《基于ChannelPool连接池的客户端实现》

> 基于Netty的TCP客户端开发通常一次只能建一个连接，如果是在高并发场景的话性能就会受到限制，而Netty也考虑到了这一点给我们提供了ChannelPool的实现，这样就实现了建立一次连接可以
> 重复使用N此，有效的提升了性能。

##### 一、ChannelPool的实现涉及到如下几个类

```properties
ChannelPool-》连接池的接口，提供了获取、释放、关闭Channel信道的方法
SimpleChannelPool-》ChannelPool的简单实现，如果要获取Channel不存在，则会重新创建，默认是LIFO
FixedChannelPool-》SimpleChannelPool的实现类，提供了设置最大并发连接数的能力--生产级别连接池
ChannelPoolMap-》存放指定key和ChannelPool连接池的映射关系Map接口
AbstractChannelPoolMap-》对ChannelPoolMap方法的具体实现，并提供了抽象方法newPool用来新建ChannelPool
```

##### 二、基于SimpleChannelInboundHandler类实现自定义处理程序IoChannelHandler

```java
public class IoChannelHandler extends SimpleChannelInboundHandler<DataPacket> {
    /**
     * 锁对象
     */
    public final Object object = new Object();

    public DataPacket result;

    public IoChannelHandler() {
        System.out.println("新建handler------------DbClientChannelHandler");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DataPacket response) throws Exception {
        if (response.packageType == 0) {
            synchronized (this.object) {
                result = response;
                this.object.notify();
            }
        }
    }

    /**
     * 异常处理
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(cause.getMessage());
        if (ctx.channel().id() != null && SimpleChannelPoolHandler.ioHandlerMap.containsKey(ctx.channel().id())) {
            SimpleChannelPoolHandler.ioHandlerMap.remove(ctx.channel().id());
        }
        ctx.close();
    }

}

```

>
在IoChannelHandler类中定义锁对象object，用于获取到服务器端返回结果后通知等待的方法继续执行，并返回结果；其中异常处理方法用于当处理程序发生异常的时候将SimpleChannelPoolHandler中缓存的Channel信道映射的处理程序对象移除。

##### 三、定义基于AbstractChannelPoolHandler的实现类AbstractChannelPoolHandler

```java
public class SimpleChannelPoolHandler extends AbstractChannelPoolHandler {
    /**
     * 缓存Channel与handler的映射关系
     */
    public static final Map<ChannelId, IoChannelHandler> ioHandlerMap = new ConcurrentHashMap<>();

    @Override
    public void channelAcquired(Channel ch) throws Exception {
        super.channelAcquired(ch);
        System.out.println("--------------------------------------------------------channelAcquired");
    }

    @Override
    public void channelReleased(Channel ch) throws Exception {
        super.channelReleased(ch);
        System.out.println("--------------------------------------------------------channelReleased");
    }

    /**
     * 在创建ChannelPool连接池时会调用此方法对Channel进行初始化
     *
     * @param ch
     * @throws Exception
     */
    @Override
    public void channelCreated(Channel ch) throws Exception {
        //缓存当前Channel对应的handler
        ioHandlerMap.put(ch.id(), new IoChannelHandler());

        ChannelPipeline pipeline = ch.pipeline();
        /**
         * 基于消息中的长度字段动态的分割接收到的ByteBuf
         * byteOrder:表示协议中Length字段的字节是大端还是小端
         * maxFrameLength：表示协议中Content字段的最大长度，如果超出，则抛出TooLongFrameException异常
         * lengthFieldOffset：表示Length字段的偏移量，即在读取一个二进制流时，跳过指定长度个字节之后的才是Length字段。如果Length字段之前没有其他报文头，指定为0即可。如果Length字段之前还有其他报文头，则需要跳过之前的报文头的字节数。
         * lengthFieldLength：表示Length字段占用的字节数。指定为多少，需要看实际要求，不同的字节数，限制了Content字段的最大长度。
         * lengthAdjustment：表示Length字段调整值
         * initialBytesToStrip：解码后跳过的初始字节数，表示获取完一个完整的数据报文之后，忽略前面指定个数的字节
         * failFast:如果为true，则表示读取到Length字段时，如果其值超过maxFrameLength，就立马抛出一个 TooLongFrameException
         */
        pipeline.addLast(new LengthFieldBasedFrameDecoder(ByteOrder.BIG_ENDIAN, 65535, 0, 2, 0, 2, true));
        //自定义解码器
        pipeline.addLast(new MessagePackDecoder());
        /**
         * 在消息前面加上前缀的编码器（只能是1、2、3、4、8，默认不包含长度字段的长度）
         * byteOrder:表示Length字段本身占用的字节数使用的是大端还是小端编码
         * lengthFieldLength：表示Length字段本身占用的字节数,只可以指定 1, 2, 3, 4, 或 8
         *     1：8位无符号二进制最大整数255
         *     2：16位无符号二进制最大整数65535
         *     3：24位无符号二进制最大整数是16777215
         *     4：32位无符号二进制最大整数是xxxx
         *     8: 64位无符号二进制最大整数是xxxx
         * lengthAdjustment：表示Length字段调整值
         * lengthIncludesLengthFieldLength:表示Length字段本身占用的字节数是否包含在Length字段表示的值中
         * Length字段的值=真实数据可读字节数+Length字段调整值
         */
        pipeline.addLast(new LengthFieldPrepender(ByteOrder.BIG_ENDIAN, 2, 0, false));
        //自定义编码器
        pipeline.addLast(new MessagePackEncoder());
        //自定义handler处理
        pipeline.addLast(ioHandlerMap.get(ch.id()));
        //空闲状态处理器，参数说明：读时间空闲时间，0禁用时间|写事件空闲时间，0则禁用|读或写空闲时间，0则禁用
        pipeline.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
        //心跳处理器
        pipeline.addLast(new HeartBeatChannelHandler());
    }
}
```

> 此类是为ChannelPool处理各种操作调用的处理程序，如：创建、释放、获取处理程序，其中channelCreated方法是在Channel被创建成功后进行初始化的；

##### 四、ChannelPoolClient类是对Bootstrap进行初始化及消息发送

```java
public class ChannelPoolClient {
    /**
     * 线程工作组
     */
    private static final EventLoopGroup workerGroup = new NioEventLoopGroup();
    /**
     * 创建客户端的启动对象 bootstrap
     */
    private static final Bootstrap bootstrap = new Bootstrap();
    /**
     * 允许将特定的key映射到ChannelPool，可以获取匹配的ChannelPool,如果不存在则会创建一个新的对象
     * 即：根据不同的服务器地址初始化ChannelPoolMap
     */
    private static ChannelPoolMap<InetSocketAddress, ChannelPool> poolMap;
    /**
     * 允许获取和释放Channel,从而可以充当Channel的池
     */
    private static ChannelPool channelPool;

    private PoolProperties properties;

    public ChannelPoolClient(PoolProperties properties) {
        this.properties = properties;
        build();
        properties.getAddress().stream().forEach(address -> {
            channelPool = poolMap.get(new InetSocketAddress(address.getIp(), address.getPort()));
        });
    }

    private void build() {
        //设置线程组
        bootstrap.group(workerGroup)
                //初始化通道
                .channel(NioSocketChannel.class)
                /**
                 * 是否启用心跳保活机制。在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）并且在两个小时左右上层没有任何数据传输的情况下，
                 * 这套机制才会被激活
                 */
                .option(ChannelOption.SO_KEEPALIVE, true)
                /**
                 * 1.在TCP/IP协议中，无论发送多少数据，总是要在数据前面加上协议头，同时，对方接收到数据，也需要发送ACK表示确认。
                 * 为了尽可能的利用网络带宽，TCP总是希望尽可能的发送足够大的数据。这里就涉及到一个名为Nagle的算法，该算法的目的就是为了尽可能发送大块数据，
                 * 避免网络中充斥着许多小数据块。
                 * 2.TCP_NODELAY就是用于启用或关于Nagle算法。如果要求高实时性，有数据发送时就马上发送，就将该选项设置为true关闭Nagle算法；
                 * 如果要减少发送次数减少网络交互，就设置为false等累积一定大小后再发送。默认为false。
                 */
                .option(ChannelOption.TCP_NODELAY, true)
                /**
                 * The timeout period of the connection.
                 * If this time is exceeded or the connection cannot be established, the connection fails.
                 */
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, NumberUtils.toInt(String.valueOf(properties.getConnectTimeOut().toMillis())));

        //ChannelPool存储、创建、删除管理Map类
        poolMap = new AbstractChannelPoolMap<>() {
            //如果ChannelPool不存在，则会创建一个新的对象
            @Override
            protected ChannelPool newPool(InetSocketAddress key) {
                return new FixedChannelPool(bootstrap.remoteAddress(key), new SimpleChannelPoolHandler(), properties.getMaxConnections());
            }
        };
    }

    public <T> T sendRequest(TransHeader transHeader, TransContent transContent, TypeReference<? extends T> reference) throws Exception {
        T response = null;
        try {
            //从ChannelPool中获取一个Channel
            final Future<Channel> future = channelPool.acquire();
            //等待future完成
            future.await();
            //判定I/O操作是否成功完成
            if (future.isSuccess()) {
                //无阻塞获取Channel对象
                final Channel ch = future.getNow();
                if (ch != null && ch.isActive() && ch.isWritable()) {
                    //获取信道对应的handler对象
                    final IoChannelHandler ioHandler = SimpleChannelPoolHandler.ioHandlerMap.get(ch.id());
                    if (ioHandler != null) {
                        //请求唯一标识序列化
                        byte[] headerBytes = MessagePackUtils.serialize(transHeader);
                        //请求体序列化
                        byte[] contentBytes = MessagePackUtils.serialize(transContent);
                        //TCP发送数据包，并对发送数据序列化
                        DataPacket packet = new DataPacket(headerBytes, contentBytes);
                        synchronized (ioHandler.object) {
                            //发送TCP请求
                            ch.writeAndFlush(packet);
                            //等待请求返回结果
                            ioHandler.object.wait(this.properties.getReadTimeOut().toMillis());
                        }
                        //根据返回结果做后续处理
                        if (ioHandler.result == null) {
                            //todo
                        } else {
                            response = MessagePackUtils.deSerialize(ioHandler.result.content, reference);
                        }
                        //释放返回结果
                        ioHandler.result = null;
                    } else {
                        //todo
                    }
                } else {
                    //todo
                }
                if (ch != null) {
                    //释放Channel到ChannelPool
                    channelPool.release(ch);
                }

            } else {
                //todo
            }
        } catch (Exception exception) {
            //todo
        }
        return response;
    }
}

```

>
此类是整个客户端的核心方法，第一步先对基于Bootstrap的客户端相关信息及ChannelPool连接池进行初始化，为连接池能够成功的创建连接做基础准备；第二部是构建发送消息的方法，其首先从连接池中获取连接、获取处理程序中的锁对象等，然后发送消息并释放连接到连接池；

源码参考：[https://github.com/mingyang66/SkyDb](https://github.com/mingyang66/SkyDb)

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
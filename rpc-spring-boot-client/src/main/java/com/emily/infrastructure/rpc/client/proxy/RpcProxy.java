package com.emily.infrastructure.rpc.client.proxy;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BusinessException;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.core.ioc.IOCContext;
import com.emily.infrastructure.rpc.client.pool.RpcConnection;
import com.emily.infrastructure.rpc.client.pool.RpcObjectPool;
import com.emily.infrastructure.rpc.core.protocol.RpcRequest;
import com.emily.infrastructure.rpc.core.protocol.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @program: spring-parent
 * @description: 创建Netty客户端及自定义处理器
 * @author: Emily
 * @create: 2021/09/17
 */
public class RpcProxy {

    private static final Logger logger = LoggerFactory.getLogger(RpcProxy.class);


    /**
     * 获取一个动态代理对象
     *
     * @param target
     * @param <T>
     * @return
     */
    public static <T> T create(Class<T> target) {
        RpcMethodProxy handler = new RpcMethodProxy(target.getSimpleName());
        // 获取class对象接口实例对象
        Class<?>[] interfaces = target.isInterface() ? new Class<?>[]{target} : target.getInterfaces();
        return (T) Proxy.newProxyInstance(target.getClassLoader(), interfaces, handler);
    }

    /**
     * Rpc动态代理调用服务
     */
    private static class RpcMethodProxy implements InvocationHandler {

        private String className;

        public RpcMethodProxy(String className) {
            this.className = className;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            //组装传输类的属性值
            RpcRequest rpcRequest = new RpcRequest(className, method.getName(), method.getParameterTypes(), args);
            //运行线程，发送数据
            RpcResponse response = call(rpcRequest);
            //获取返回类型，并将服务端返回的json数据转化为对应的类型
            Class<?> returnType = method.getReturnType();
            return JSONUtils.toJavaBean(JSONUtils.toJSONString(response.getData()), returnType);
        }

        /**
         * 通过连接池发送
         *
         * @param request
         * @return
         */
        public RpcResponse call(RpcRequest request) {
            //运行线程，发送数据
            RpcObjectPool pool = IOCContext.getBean(RpcObjectPool.class);
            RpcConnection connection = null;
            try {
                connection = pool.borrowObject();
                return connection.sendRequest(request);
            } catch (Exception exception) {
                logger.error(PrintExceptionInfo.printErrorInfo(exception));
                throw new BusinessException(AppHttpStatus.EXCEPTION.getStatus(), "Rpc调用异常");
            } finally {
                if (connection != null) {
                    pool.returnObject(connection);
                }
            }
        }
    }
}

package com.emily.infrastructure.rpc.client.proxy;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BasicException;
import com.emily.infrastructure.common.exception.PrintExceptionInfo;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.core.context.ioc.IOCContext;
import com.emily.infrastructure.rpc.client.logger.RecordLogger;
import com.emily.infrastructure.rpc.client.pool.IRpcConnection;
import com.emily.infrastructure.rpc.client.pool.IRpcObjectPool;
import com.emily.infrastructure.rpc.core.message.IRpcMessage;
import com.emily.infrastructure.rpc.core.message.IRpcRequest;
import com.emily.infrastructure.rpc.core.message.IRpcResponse;
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
public class IRpcInvokeProxy {

    private static final Logger logger = LoggerFactory.getLogger(IRpcInvokeProxy.class);


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
            //开始时间
            long startTime = System.currentTimeMillis();
            //组装传输类的属性值
            IRpcRequest request = IRpcRequest.build(className, method.getName(), method.getParameterTypes(), args);
            //响应结果
            IRpcResponse rpcResponse = null;
            try {
                //运行线程，发送数据
                rpcResponse = invokeTargetMethod(IRpcMessage.build(JSONUtils.toByteArray(request)));
                //判定返回结果是否为null
                return JSONUtils.parseObject(rpcResponse.getData(), method.getReturnType());
            } catch (Exception ex) {
                //异常信息
                rpcResponse = IRpcResponse.buildResponse(AppHttpStatus.EXCEPTION.getStatus(), AppHttpStatus.EXCEPTION.getMessage(), PrintExceptionInfo.printErrorInfo(ex));
                throw ex;
            } finally {
                RecordLogger.recordResponse(request, rpcResponse, startTime);
            }
        }

        /**
         * 通过连接池发送
         *
         * @param message
         * @return
         */
        public IRpcResponse invokeTargetMethod(IRpcMessage message) {
            //运行线程，发送数据
            IRpcObjectPool pool = IOCContext.getBean(IRpcObjectPool.class);
            //Channel对象
            IRpcConnection connection = null;
            try {
                connection = pool.borrowObject();
                return connection.getClientChannelHandler().send(message);
            } catch (Exception exception) {
                logger.error(PrintExceptionInfo.printErrorInfo(exception));
                throw new BasicException(AppHttpStatus.EXCEPTION.getStatus(), "Rpc调用异常");
            } finally {
                if (connection != null) {
                    pool.returnObject(connection);
                }
            }
        }
    }
}

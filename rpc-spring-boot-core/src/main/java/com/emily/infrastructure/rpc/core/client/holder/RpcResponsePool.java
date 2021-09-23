package com.emily.infrastructure.rpc.core.client.holder;

import com.emily.infrastructure.rpc.core.protocol.RpcResponse;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.SynchronousQueue;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/09/22
 */
public class RpcResponsePool {
    /**
     * requestID ——》 调用结果
     */
    private static final ConcurrentHashMap<String, SynchronousQueue<RpcResponse>> queueMap = new ConcurrentHashMap<>();

    public static void putRequest(String requestId) {
        //建立request的同步队列
        SynchronousQueue<RpcResponse> queue = new SynchronousQueue<>();
        queueMap.put(requestId, queue);
    }

    public static void putResponse(RpcResponse response) {
        queueMap.get(response.getTraceId()).offer(response);
    }

    public static RpcResponse takeResponse(String requestId) {
        RpcResponse response = null;
        try {
            //在requestId对应的response放入之前，会阻塞在这里
            response = queueMap.get(requestId).take();
            queueMap.remove(requestId);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return response;
    }
}

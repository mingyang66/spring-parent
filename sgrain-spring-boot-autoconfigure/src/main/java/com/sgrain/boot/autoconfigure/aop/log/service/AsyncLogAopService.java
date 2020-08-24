package com.sgrain.boot.autoconfigure.aop.log.service;

import com.sgrain.boot.autoconfigure.aop.log.po.AsyncLogAop;

public interface AsyncLogAopService {
    /**
     * 追踪API请求日志
     *
     * @param asyncLog
     */
    void traceRequest(AsyncLogAop asyncLog);

    /**
     * 追踪API响应日志
     *
     * @param asyncLog
     */
    void traceResponse(AsyncLogAop asyncLog);

    /**
     * 追踪API响应异常日志
     *
     * @param asyncLog
     */
    void traceError(AsyncLogAop asyncLog);
}

package com.emily.boot.context.apilog.service;

import com.emily.boot.context.apilog.po.AsyncLogAop;

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

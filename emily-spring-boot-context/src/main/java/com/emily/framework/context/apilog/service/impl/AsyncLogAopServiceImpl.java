package com.emily.framework.context.apilog.service.impl;

import com.emily.framework.common.utils.json.JSONUtils;
import com.emily.framework.common.utils.logger.LoggerUtils;
import com.emily.framework.context.apilog.po.AsyncLogAop;
import com.emily.framework.context.apilog.service.AsyncLogAopService;
import org.springframework.scheduling.annotation.Async;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/08/22
 */
public class AsyncLogAopServiceImpl implements AsyncLogAopService {
    /**
     * 追踪API请求日志
     *
     * @param asyncLog
     */
    @Async
    @Override
    public void traceRequest(AsyncLogAop asyncLog) {
        if (LoggerUtils.isDebug()) {
            LoggerUtils.info(asyncLog.getClazz(), JSONUtils.toJSONPrettyString(asyncLog));
        } else {
            LoggerUtils.info(asyncLog.getClazz(), JSONUtils.toJSONString(asyncLog));
        }
    }

    /**
     * 追踪API响应日志
     *
     * @param asyncLog
     */
    @Async
    @Override
    public void traceResponse(AsyncLogAop asyncLog) {
        if (LoggerUtils.isDebug()) {
            LoggerUtils.info(asyncLog.getClazz(), JSONUtils.toJSONPrettyString(asyncLog));
        } else {
            LoggerUtils.info(asyncLog.getClazz(), JSONUtils.toJSONString(asyncLog));
        }
    }

    /**
     * 追踪API响应异常日志
     *
     * @param asyncLog
     */
    @Override
    @Async
    public void traceError(AsyncLogAop asyncLog) {
        if (LoggerUtils.isDebug()) {
            LoggerUtils.error(asyncLog.getClazz(), JSONUtils.toJSONPrettyString(asyncLog));
        } else {
            LoggerUtils.error(asyncLog.getClazz(), JSONUtils.toJSONString(asyncLog));
        }
    }
}

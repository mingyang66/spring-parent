package com.emily.framework.context.apilog.service.impl;

import com.emily.framework.context.apilog.po.AsyncLogAop;
import com.google.common.collect.Maps;
import com.emily.framework.context.apilog.service.AsyncLogAopService;
import com.emily.framework.common.enums.AppHttpStatus;
import com.emily.framework.common.enums.DateFormatEnum;
import com.emily.framework.common.utils.log.LoggerUtils;
import com.emily.framework.common.utils.calculation.ObjectSizeUtil;
import com.emily.framework.common.utils.constant.CharacterUtils;
import com.emily.framework.common.utils.date.DateUtils;
import com.emily.framework.common.utils.json.JSONUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

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
        Map<String, Object> logMap = Maps.newLinkedHashMap();
        logMap.put("T_ID", asyncLog.gettId());
        logMap.put("Request Time", DateUtils.formatDate(new Date(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat()));
        logMap.put("Class|Method", StringUtils.join(asyncLog.getClazz(), CharacterUtils.POINT_SYMBOL, asyncLog.getMethodName()));
        logMap.put("Request URL", asyncLog.getRequestUrl());
        logMap.put("Protocol", asyncLog.getProtocol());
        logMap.put("Request Method", asyncLog.getMethod());
        logMap.put("Request Params", CollectionUtils.isEmpty(asyncLog.getRequestParams()) ? Collections.emptyMap() : asyncLog.getRequestParams());
        logMap.put("Content-Type", asyncLog.getContentType());
        if (LoggerUtils.isDebug()) {
            LoggerUtils.info(asyncLog.getClazz(), JSONUtils.toJSONPrettyString(logMap));
        } else {
            LoggerUtils.info(asyncLog.getClazz(), JSONUtils.toJSONString(logMap));
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
        Object resultBody = asyncLog.getResponseBody();
        String url = asyncLog.getRequestUrl();
        if (ObjectUtils.isNotEmpty(resultBody) && (resultBody instanceof ResponseEntity)) {
            ResponseEntity entity = ((ResponseEntity) resultBody);
            resultBody = entity.getBody();
            if(entity.getStatusCode().value() == AppHttpStatus.API404_EXCEPTION.getStatus()){
                url = ((Map)(entity.getBody())).get("path").toString();
            }
        }
        Map<String, Object> logMap = Maps.newLinkedHashMap();
        logMap.put("T_ID", asyncLog.gettId());
        logMap.put("Response Time", DateUtils.formatDate(asyncLog.getResponseTime(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS_SSS.getFormat()));
        logMap.put("Class|Method", StringUtils.join(asyncLog.getClazz(), CharacterUtils.POINT_SYMBOL, asyncLog.getMethodName()));
        logMap.put("Request URL", url);
        logMap.put("Request Method", asyncLog.getMethod());
        logMap.put("Protocol", asyncLog.getProtocol());
        logMap.put("Request Params", CollectionUtils.isEmpty(asyncLog.getRequestParams()) ? Collections.emptyMap() : asyncLog.getRequestParams());
        logMap.put("Content-Type", asyncLog.getContentType());
        logMap.put("Spend Time", StringUtils.join((asyncLog.getSpentTime() == 0) ? 1 : asyncLog.getSpentTime(), "ms"));
        logMap.put("Data Size", ObjectSizeUtil.getObjectSizeUnit(resultBody));
        logMap.put("Response Body", resultBody);
        if (LoggerUtils.isDebug()) {
            LoggerUtils.info(asyncLog.getClazz(), JSONUtils.toJSONPrettyString(logMap));
        } else {
            LoggerUtils.info(asyncLog.getClazz(), JSONUtils.toJSONString(logMap));
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
        Map<String, Object> logMap = Maps.newLinkedHashMap();
        logMap.put("T_ID", asyncLog.gettId());
        logMap.put("Response Time", asyncLog.getResponseTime());
        logMap.put("Class|Method", StringUtils.join(asyncLog.getClazz(), CharacterUtils.POINT_SYMBOL, asyncLog.getMethodName()));
        logMap.put("Request URL", asyncLog.getRequestUrl());
        logMap.put("Request Method", asyncLog.getMethod());
        logMap.put("Protocol", asyncLog.getProtocol());
        logMap.put("Reuqest Params", CollectionUtils.isEmpty(asyncLog.getRequestParams()) ? Collections.emptyMap() : asyncLog.getRequestParams());
        logMap.put("Content-Type", asyncLog.getContentType());
        logMap.put("Exception", asyncLog.getException());
        if (LoggerUtils.isDebug()) {
            LoggerUtils.error(asyncLog.getClazz(), JSONUtils.toJSONPrettyString(logMap));
        } else {
            LoggerUtils.error(asyncLog.getClazz(), JSONUtils.toJSONString(logMap));
        }
    }
}

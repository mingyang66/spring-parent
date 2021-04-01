package com.emily.framework.cloud.feign.http.common;

import com.emily.framework.common.utils.RequestUtils;
import com.emily.framework.context.apilog.po.AsyncLogAop;

import java.util.Objects;

/**
 * @program: spring-parent
 * @description: feigin日志记录工具类
 * @create: 2021/04/01
 */
public class FeignLogUtils {
    /**
     * 获取日志记录对象
     */
    public static AsyncLogAop getAsyncLogAop(){
        //封装异步日志信息
        AsyncLogAop asyncLog;
        Object feignLog = RequestUtils.getRequest().getAttribute("feignLog");
        if (Objects.isNull(feignLog)) {
            asyncLog = new AsyncLogAop();
        } else {
            asyncLog = (AsyncLogAop) feignLog;
        }
        return asyncLog;
    }
}

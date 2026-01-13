package com.emily.infrastructure.logger.event;

import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.logger.utils.PrintLogUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;

/**
 * 打印日志事件监听器
 *
 * @author :  Emily
 * @since :  2024/12/28 下午11:24
 */
public class LogPrintApplicationListener implements ApplicationListener<@NonNull LogPrintApplicationEvent>, ApplicationContextAware {
    private ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public void onApplicationEvent(LogPrintApplicationEvent event) {
        //确保执行的事件是当前ApplicationContext广播出的
        if (event.getContext() != context) {
            return;
        }
        if (LogEventType.REQEUST == event.getEventType()) {
            PrintLogUtils.printRequest(() -> JsonUtils.toJSONString(event.getBaseLogger()));
        } else if (LogEventType.THIRD_PARTY == event.getEventType()) {
            PrintLogUtils.printThirdParty(() -> JsonUtils.toJSONString(event.getBaseLogger()));
        } else if (LogEventType.PLATFORM == event.getEventType()) {
            PrintLogUtils.printPlatform(() -> JsonUtils.toJSONString(event.getBaseLogger()));
        }
    }

}

package com.emily.infrastructure.logger.event;

import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.logger.utils.PrintLogUtils;
import org.jspecify.annotations.NonNull;
import org.springframework.context.ApplicationListener;

/**
 * 打印日志事件监听器
 *
 * @author :  Emily
 * @since :  2024/12/28 下午11:24
 */
public class LogPrintApplicationListener implements ApplicationListener<@NonNull LogPrintApplicationEvent> {
    @Override
    public void onApplicationEvent(LogPrintApplicationEvent event) {
        if (LogEventType.REQEUST == event.getEventType()) {
            PrintLogUtils.printRequest(() -> JsonUtils.toJSONString(event.getBaseLogger()));
        } else if (LogEventType.THIRD_PARTY == event.getEventType()) {
            PrintLogUtils.printThirdParty(() -> JsonUtils.toJSONString(event.getBaseLogger()));
        } else if (LogEventType.PLATFORM == event.getEventType()) {
            PrintLogUtils.printPlatform(() -> JsonUtils.toJSONString(event.getBaseLogger()));
        }
    }

    @Override
    public boolean supportsAsyncExecution() {
        return true;
    }
}

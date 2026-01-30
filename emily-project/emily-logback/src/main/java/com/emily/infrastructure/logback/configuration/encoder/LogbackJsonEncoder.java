package com.emily.infrastructure.logback.configuration.encoder;

import ch.qos.logback.classic.encoder.JsonEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.encoder.Encoder;

/**
 * Json编码方式
 *
 * @author :  Emily
 * @since :  2026/1/30 上午9:56
 */
public class LogbackJsonEncoder {
    private final Context context;

    public LogbackJsonEncoder(Context context) {
        this.context = context;
    }

    public Encoder<ILoggingEvent> getEncoder(String pattern) {
        JsonEncoder encoder = new JsonEncoder();
        encoder.setContext(context);
        //启用或禁用在编码输出中包含日志级别，默认：true
        encoder.setWithLevel(true);
        //启用或禁用在编码输出中包含记录器名称的功能，默认：true
        encoder.setWithLoggerName(true);
        //启用或禁用包含记录器上下文信息的功能。
        encoder.setWithContext(false);
        //启用或禁用将格式化消息包含在编码输出中的功能。默认：false
        encoder.setWithFormattedMessage(false);
        //启用或禁用在编码输出中包含原始消息文本的功能。默认：true
        encoder.setWithMessage(true);
        //设置是否在每个编码事件中包含序列号。默认：true
        encoder.setWithSequenceNumber(true);
        //设置是否在每个编码事件中包含事件时间戳。默认：true
        encoder.setWithTimestamp(true);
        //设置时间戳输出中是否包含纳秒。默认：true
        encoder.setWithNanoseconds(true);
        //启用或禁用在编码输出中包含MDC属性。默认：true
        encoder.setWithMDC(true);
        //启用或禁用在编码输出中包含标记的功能。默认：true
        encoder.setWithMarkers(true);
        //启用或禁用包含附加到日志事件中的键值对。默认：true
        encoder.setWithKVPList(true);
        //启用或禁用在编码输出中包含线程名称的功能。默认：true
        encoder.setWithThreadName(true);
        //启用或禁用在编码输出中包含可抛出信息。默认：true
        encoder.setWithThrowable(true);
        encoder.addInfo("Build LogbackJsonEncoder Success");
        encoder.start();
        return encoder;
    }
}

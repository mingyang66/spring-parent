package com.emily.infrastructure.logback.configuration.encoder;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;

import java.nio.charset.StandardCharsets;

/**
 * logback console编码方式
 * 案例：{@link ch.qos.logback.classic.BasicConfigurator}
 *
 * @author :  Emily
 * @since :  2026/1/29 下午2:22
 */
public class LogbackConsoleLayoutEncoder {
    private final Context context;

    public LogbackConsoleLayoutEncoder(Context context) {
        this.context = context;
    }

    public Encoder<ILoggingEvent> getEncoder(String pattern) {
        LayoutWrappingEncoder<ILoggingEvent> encoder = new LayoutWrappingEncoder<>();
        encoder.setContext(context);

        PatternLayout layout = new PatternLayout();
        layout.setPattern(pattern);
        layout.setContext(context);
        layout.start();

        encoder.setLayout(layout);
        encoder.setCharset(StandardCharsets.UTF_8);
        encoder.addInfo("Build LayoutWrappingEncoder Success");
        encoder.start();
        return encoder;
    }
}

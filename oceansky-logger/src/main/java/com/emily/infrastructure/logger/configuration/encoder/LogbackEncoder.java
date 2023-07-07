package com.emily.infrastructure.logger.configuration.encoder;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.Context;

import java.nio.charset.StandardCharsets;

/**
 * @program: spring-parent
 * @description: logback编码方式
 * @author: Emily
 * @create: 2022/01/10
 */
public class LogbackEncoder {
    /**
     * 获取编码方式
     *
     * @param context logback上下文
     * @param pattern 日志输出格式
     * @return
     */
    public static PatternLayoutEncoder getPatternLayoutEncoder(Context context, String pattern) {
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        //设置上下文，每个logger都关联到logger上下文，默认上下文名称为default。
        // 但可以使用<contextName>设置成其他名字，用于区分不同应用程序的记录。一旦设置，不能修改。
        encoder.setContext(context);
        //设置格式
        encoder.setPattern(pattern);
        //设置编码格式
        encoder.setCharset(StandardCharsets.UTF_8);
        //为了便于解析日志文件，logback可以在日志文件的顶部插入用于日志输出的模式，默认：false
        encoder.setOutputPatternAsHeader(false);
        encoder.start();
        return encoder;
    }
}

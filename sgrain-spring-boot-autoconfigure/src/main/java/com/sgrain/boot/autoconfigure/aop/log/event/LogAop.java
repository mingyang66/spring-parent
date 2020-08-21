package com.sgrain.boot.autoconfigure.aop.log.event;

/**
 * @program: spring-parent
 * @description: 日志事件对象
 * @create: 2020/08/07
 */
public class LogAop<T> {
    //日志级别
    private String logLevel;
    private Class aClass;
    private T traceLog;

    public LogAop(String logLevel, Class aClass, T traceLog) {
        this.logLevel = logLevel;
        this.aClass = aClass;
        this.traceLog = traceLog;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }

    public T getTraceLog() {
        return traceLog;
    }

    public void setTraceLog(T traceLog) {
        this.traceLog = traceLog;
    }
}

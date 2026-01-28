package com.emily.infrastructure.logback;


import com.emily.infrastructure.logback.entity.*;

/**
 * 日志配置属性
 *
 * @author Emily
 * @since : 2020/08/08
 */
public class LogbackProperties {
    /**
     * 是否开启日志组件，默认：true
     */
    private boolean enabled = true;
    /**
     * 是否开启debug模式，默认：false
     */
    private boolean debug = false;
    /**
     * 发生异常打印异常堆栈时是否将包信息追加到每行末尾，默认：true
     */
    private boolean packagingData = true;
    /**
     * 全局过滤器日志标记控制
     */
    private Marker marker = new Marker();
    /**
     * 基础根日志
     */
    private LogRoot root = new LogRoot();
    /**
     * 分组记录日志
     */
    private LogGroup group = new LogGroup();
    /**
     * 按模块记录日志
     */
    private LogModule module = new LogModule();
    /**
     * appender配置
     */
    private Appender appender = new Appender();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isPackagingData() {
        return packagingData;
    }

    public void setPackagingData(boolean packagingData) {
        this.packagingData = packagingData;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public Appender getAppender() {
        return appender;
    }

    public void setAppender(Appender appender) {
        this.appender = appender;
    }

    public LogRoot getRoot() {
        return root;
    }

    public void setRoot(LogRoot root) {
        this.root = root;
    }

    public LogGroup getGroup() {
        return group;
    }

    public void setGroup(LogGroup group) {
        this.group = group;
    }

    public LogModule getModule() {
        return module;
    }

    public void setModule(LogModule module) {
        this.module = module;
    }
}

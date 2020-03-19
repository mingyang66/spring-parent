package com.yaomy.sgrain.aop.po;

/**
 * @program: spring-parent
 * @description: 拦截器实体类
 * @author: 姚明洋
 * @create: 2020/03/19
 */
public class Interceptor {
    /**
     * 换行符
     */
    private static final String NEW_LINE = "\n";
    /**
     * 毫秒
     */
    private static final String MILLI_SECOND = "ms";
    /**
     * 控制器
     */
    private static final String MSG_CONTROLLER = "类|方法 ：";
    /**
     * 访问URL
     */
    private static final String MSG_ACCESS_URL = "访问URL ：";
    /**
     * 请求Method
     */
    public static final String MSG_METHOD = "Method  ：";
    /**
     * 请求PARAM
     */
    private static final String MSG_PARAMS = "请求参数：";
    /**
     * 耗时
     */
    private static final String MSG_TIME= "耗  时  ：";
    /**
     * 返回结果
     */
    private static final String MSG_RETURN_VALUE = "返回结果：";
    /**
     * 数据大小
     */
    private static final String MSG_DATA_SIZE = "数据大小：";
    /**
     * 异常
     */
    private static final String MSG_EXCEPTION = "异  常  ：";
    /**
     * START消息
     */
    private static final String MSG_DATASOURCE_START = "开始执行，切换数据源到【";
    /**
     * END消息
     */
    private static final String MSG_DATASOURCE_END = "执行结束，移除数据源【";
    /**
     * 中文右符号
     */
    private static final String MSG_RIGHT_SYMBOL = "】";

    private String nameLine = NEW_LINE;
    private String millSecond = MILLI_SECOND;
    private String msgController = MSG_CONTROLLER;
    private String msgAccessUrl = MSG_ACCESS_URL;
    private String msgMethod = MSG_METHOD;
    private String msgParams = MSG_PARAMS;
    private String msgTime = MSG_TIME;
    private String msgReturnValue = MSG_RETURN_VALUE;
    private String msgDataSize = MSG_DATA_SIZE;
    private String msgException = MSG_EXCEPTION;
    private String msgDataSourceStart = MSG_DATASOURCE_START;
    private String msgDataSourceEnd = MSG_DATASOURCE_END;
    private String msgRightSymbol = MSG_RIGHT_SYMBOL;

    public String getNameLine() {
        return nameLine;
    }

    public void setNameLine(String nameLine) {
        this.nameLine = nameLine;
    }

    public String getMillSecond() {
        return millSecond;
    }

    public void setMillSecond(String millSecond) {
        this.millSecond = millSecond;
    }

    public String getMsgController() {
        return msgController;
    }

    public void setMsgController(String msgController) {
        this.msgController = msgController;
    }

    public String getMsgAccessUrl() {
        return msgAccessUrl;
    }

    public void setMsgAccessUrl(String msgAccessUrl) {
        this.msgAccessUrl = msgAccessUrl;
    }

    public String getMsgMethod() {
        return msgMethod;
    }

    public void setMsgMethod(String msgMethod) {
        this.msgMethod = msgMethod;
    }

    public String getMsgParams() {
        return msgParams;
    }

    public void setMsgParams(String msgParams) {
        this.msgParams = msgParams;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public String getMsgReturnValue() {
        return msgReturnValue;
    }

    public void setMsgReturnValue(String msgReturnValue) {
        this.msgReturnValue = msgReturnValue;
    }

    public String getMsgDataSize() {
        return msgDataSize;
    }

    public void setMsgDataSize(String msgDataSize) {
        this.msgDataSize = msgDataSize;
    }

    public String getMsgException() {
        return msgException;
    }

    public void setMsgException(String msgException) {
        this.msgException = msgException;
    }

    public String getMsgDataSourceStart() {
        return msgDataSourceStart;
    }

    public void setMsgDataSourceStart(String msgDataSourceStart) {
        this.msgDataSourceStart = msgDataSourceStart;
    }

    public String getMsgDataSourceEnd() {
        return msgDataSourceEnd;
    }

    public void setMsgDataSourceEnd(String msgDataSourceEnd) {
        this.msgDataSourceEnd = msgDataSourceEnd;
    }

    public String getMsgRightSymbol() {
        return msgRightSymbol;
    }

    public void setMsgRightSymbol(String msgRightSymbol) {
        this.msgRightSymbol = msgRightSymbol;
    }
}

package com.emily.infrastructure.core.helper;

import com.emily.infrastructure.core.context.TraceContextProperties;
import com.emily.infrastructure.core.ioc.IOCContext;

/**
 * @program: spring-parent
 * @description: 系统编号帮助类
 * @author: Emily
 * @create: 2021/11/27
 */
public class SystemNumberHelper {
    /**
     * 获取系统编号
     * @return
     */
    public static String getSystemNumber(){
        return IOCContext.getBean(TraceContextProperties.class).getSystemNumber();
    }
}

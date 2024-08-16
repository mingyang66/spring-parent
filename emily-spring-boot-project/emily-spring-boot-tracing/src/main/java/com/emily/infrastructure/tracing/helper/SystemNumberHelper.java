package com.emily.infrastructure.tracing.helper;

import com.emily.infrastructure.common.StringUtils;
import com.emily.infrastructure.tracing.ContextProperties;
import com.emily.infrastructure.tracing.ioc.IocUtils;

/**
 * 系统编号帮助类
 *
 * @author Emily
 * @since 2021/11/27
 */
public class SystemNumberHelper {
    /**
     * 获取系统标识
     *
     * @return 系统标识
     */
    public static String getSystemNumber() {
        try {
            return IocUtils.getBean(ContextProperties.class).getSystemNumber();
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }
}

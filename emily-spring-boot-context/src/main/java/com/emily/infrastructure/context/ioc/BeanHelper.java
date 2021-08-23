package com.emily.infrastructure.context.ioc;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BusinessException;
import com.emily.infrastructure.common.helper.StringHelper;

import java.text.MessageFormat;

/**
 * @description: bean获取帮助类
 * @author: Emily
 * @create: 2021/07/11
 */
public class BeanHelper {
    /**
     * 获取容器中指定接口的实现类，beanName=接口类名首字母小写+suffix
     *
     * @param clazz  接口class实例对象
     * @param suffix bean名称后缀
     * @param <T>    实例类型
     * @return
     */
    public static <T> T getBean(Class<T> clazz, String suffix) {
        if (!clazz.isInterface()) {
            throw new BusinessException(AppHttpStatus.EXCEPTION.getStatus(), "必须为接口类型");
        }
        String beanName = MessageFormat.format("{0}{1}", StringHelper.toLowerFirstCase(clazz.getSimpleName()), suffix);
        if (IOCContext.containsBean(beanName)) {
            return IOCContext.getBean(beanName, clazz);
        }
        throw new BusinessException(AppHttpStatus.EXCEPTION.getStatus(), "实例对象不存在");
    }
}

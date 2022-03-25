package com.emily.infrastructure.test.service.context;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BusinessException;
import com.emily.infrastructure.common.utils.StrUtils;
import com.emily.infrastructure.core.context.ioc.IOCContext;
import org.apache.commons.lang3.StringUtils;

import java.text.MessageFormat;
import java.util.Map;

/**
 * @Description : 获取Mapper实例对象帮助类
 * @Author :  Emily
 * @CreateDate :  Created in 2022/3/24 4:23 下午
 */
public class MapperContext {
    /**
     * 获取Mapper实例对象
     *
     * @param originClass mapper实例对象
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> originClass) {
        return getBean(null, originClass, null);
    }

    /**
     * 获取Mapper实例对象
     *
     * @param param       判定条件参数
     * @param originClass Mapper class对象
     * @param targetClass 转换Mapper class对象
     * @param <T>
     * @return
     */
    public static <T> T getBean(String param, Class<T> originClass, Class targetClass) {
        //获取Mapper实例对象名
        String beanName = StrUtils.toLowerFirstCase(originClass.getSimpleName());
        //获取实例对象对应的所有bean集合
        Map<String, T> beanMaps = IOCContext.getBeansOfType(originClass);
        if (!beanMaps.containsKey(beanName)) {
            throw new BusinessException(AppHttpStatus.NOT_FOUND.getStatus(), MessageFormat.format("实例对象{0}不存在", beanName));
        }
        //todo 符合条件
        if (StringUtils.isEmpty(param) || isOracle(param)) {
            return beanMaps.get(beanName);
        }
        beanName = StrUtils.toLowerFirstCase(targetClass.getSimpleName());
        if (!beanMaps.containsKey(beanName)) {
            throw new BusinessException(AppHttpStatus.NOT_FOUND.getStatus(), MessageFormat.format("实例对象{0}不存在", beanName));
        }
        return beanMaps.get(beanName);
    }

    /**
     * 判定是否是oracle数据库
     *
     * @param param
     * @return
     */
    private static boolean isOracle(String param) {
        if (StringUtils.equals(param, "1")) {
            return true;
        }
        return false;
    }
}

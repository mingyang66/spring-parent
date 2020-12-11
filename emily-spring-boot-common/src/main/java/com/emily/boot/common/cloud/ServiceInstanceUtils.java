package com.emily.boot.common.cloud;

import com.emily.boot.common.enums.AppHttpStatus;
import com.emily.boot.common.exception.BusinessException;
import com.emily.boot.common.utils.constant.CharacterUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @program: spring-parent
 * @description: 服务实例名集合
 * @create: 2020/11/23
 */
public class ServiceInstanceUtils {
    /**
     * 获取服务请求URL
     * @param serviceName 服务名
     * @param apiName API名称
     * @return
     */
    public static String getRequestUrl(String serviceName, String apiName) {
        if (StringUtils.isEmpty(serviceName)) {
            throw new BusinessException(AppHttpStatus.DATA_EXCEPTION.getStatus(), "服务名不可以为空！");
        }
        if (StringUtils.isEmpty(apiName)) {
            throw new BusinessException(AppHttpStatus.DATA_EXCEPTION.getStatus(), "API不可以为空！");
        }
        if (StringUtils.startsWith(apiName, CharacterUtils.PATH_SEPARATOR)) {
            return StringUtils.join("http://", serviceName, apiName);
        } else {
            return StringUtils.join("http://", serviceName, CharacterUtils.PATH_SEPARATOR, apiName);
        }
    }
}

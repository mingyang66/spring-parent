package com.emily.infrastructure.autoconfigure.route.mapping;

/**
 * @Description :  自定义路由解析
 * @Author :  Emily
 * @CreateDate :  Created in 2023/2/2 1:30 下午
 */
public interface LookupPathCustomizer {
    /**
     * 解析指定的路由key
     * @param lookupPath 原始路由key
     * @return
     */
    default String resolveSpecifiedLookupPath(String lookupPath){
        return lookupPath;
    }
}

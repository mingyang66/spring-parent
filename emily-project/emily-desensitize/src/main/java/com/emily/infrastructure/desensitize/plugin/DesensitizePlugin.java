package com.emily.infrastructure.desensitize.plugin;

import com.emily.infrastructure.desensitize.DesensitizeType;

/**
 * 基于插件实现自定义脱敏注解插件
 *
 * @author :  Emily
 * @since :  2024/12/25 下午10:32
 */
public interface DesensitizePlugin<T> {
    /**
     * 判定是否支持处理指定的数据类型
     *
     * @param value 字段值
     * @return true-支持 false-不支持
     */
    boolean support(Object value);

    /**
     * 根据语言类型将值转换为对应的语言，参数都不会为空
     *
     * @param value           原字段值可以为任何类型，如:String、List<String>、Map<String,String></>
     * @param desensitizeType 脱敏类别
     * @return 转换后的字段语言值
     */
    T getPlugin(T value, DesensitizeType desensitizeType);
}

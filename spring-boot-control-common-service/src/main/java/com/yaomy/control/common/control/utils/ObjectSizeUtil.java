package com.yaomy.control.common.control.utils;

import org.apache.lucene.util.RamUsageEstimator;

/**
 * @Description: 计算java对象的大小
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
public class ObjectSizeUtil {
    /**
     * @Description 计算对象占用内存大小，单位：字节
     * @Version  1.0
     */
    public static long getObjectSize(Object o){
        return RamUsageEstimator.sizeOfObject(o);
    }
    /**
     * @Description 计算对象占用内存大小，数值后带单位：GB, MB, KB or bytes
     * @Version  1.0
     */
    public static String getObjectSizeReadable(Object o){
        return RamUsageEstimator.humanReadableUnits(getObjectSize(o));
    }
}

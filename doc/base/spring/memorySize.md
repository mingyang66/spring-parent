#### Java计算对象大小RamUsageEstimator工具类

#### 1.引入第三方jar

```
        <dependency>
            <groupId>org.apache.lucene</groupId>
            <artifactId>lucene-core</artifactId>
            <version>8.2.0</version>
        </dependency>
```

> 主要使用该包中的RamUsageEstimator工具类来计算内存大小

#### 2.自定义封装工具类

```
package com.yaomy.control.common.control.utils;

import org.apache.lucene.util.RamUsageEstimator;

/**
 *  计算java对象的大小
 * @ProjectName: spring-parent
 * @since 1.0
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
```

#### 3.工具方法使用说明

```
    public static long alignObjectSize(long size)
    
    //返回long类型的bytes大小
    public static long sizeOf(Long value) 
    
    //返回byte[]类型的bytes大小
    public static long sizeOf(byte[] arr) 
    
    //返回boolean[]类型的bytes大小
    public static long sizeOf(boolean[] arr) 
    
    //返回char[]类型的bytes大小
    public static long sizeOf(char[] arr)
  
    //返回short[]类型的bytes大小
    public static long sizeOf(short[] arr)
    
   //返回int[]类型的bytes大小
    public static long sizeOf(int[] arr)
    
    //返回float[]类型的bytes大小
    public static long sizeOf(float[] arr)
    
    //返回long[]类型的bytes大小
    public static long sizeOf(long[] arr)
    
    //返回double[]类型的bytes大小
    public static long sizeOf(double[] arr)
  
    //返回String[]类型的bytes大小
    public static long sizeOf(String[] arr) 
    //计算指定的map对象大小，单位字节
    public static long sizeOfMap(Map<?, ?> map)
    //计算指定对象的字节大小，单位字节
    public static long sizeOfObject(Object o)
    ...

```

> 还有其它一些方法没有一一说明，看源码注解就很清楚了

GitHub源码：[https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-common-service/memorySize.md](https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-common-service/memorySize.md)
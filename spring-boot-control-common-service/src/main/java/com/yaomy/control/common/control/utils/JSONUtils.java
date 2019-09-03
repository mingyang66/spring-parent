package com.yaomy.control.common.control.utils;

import com.fasterxml.jackson.annotation.JsonInclude.*;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.yaomy.control.common.control.enums.DateFormatEnum;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * @Description: JSON工具类
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
public class JSONUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //对象的所有字段全部序列化
        objectMapper.setSerializationInclusion(Include.ALWAYS);
        //取消默认转换timestamps
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat()));
        //忽略空Bean转json的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //忽略，在json字符串中存在但是在java对象中不存在的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    /**
     * @Description 对象转换为json字符串,支持List、Map、Collection、字符串
     * @Version  1.0
     */
    public static <T> String toJSONString(T o){
       return toJSONString(o, Include.ALWAYS);
    }
    /**
     * @Description 对象转换为json字符串,支持List、Map、Collection、字符串
     * @param include 定义javaBean的那些属性需要序列化
     *              ALWAYS：始终包含javaBean的值，与属性的值无关。
     *              NON_NULL：表示只包含非null的属性值。
     *              NON_ABSENT：表示属性值为null,或者JAVA8、Guava中的Optional
     *              NON_EMPTY：表示非null、""和数组集合isEmpty()=false都将会被忽略
     *              NON_DEFAULT：表示POJO类属性的值为缺省值是不序列化，如User类的 int age = 0; String username = null;
     *              CUSTOM:自定义，根据过滤器等
     *              USE_DEFAULTS：...
     * @Version  1.0
     */
    public static <T> String toJSONString(T o, Include include){
        try{
            if(null == include){
                include = Include.ALWAYS;
            }
            objectMapper.setSerializationInclusion(include);
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e){
            e.printStackTrace();
            return null;
        }
    }
    /**
     * @Description 带格式化， 对象转换为json字符串,支持List、Map、Collection、字符串
     * @Version  1.0
     */
    public static <T> String toJSONPrettyString(T o){
       return toJSONPrettyString(o, Include.ALWAYS);
    }
    /**
     * @Description 带格式化， 对象转换为json字符串,支持List、Map、Collection、字符串
     * @param include 定义javaBean的那些属性需要序列化
     *              ALWAYS：始终包含javaBean的值，与属性的值无关。
     *              NON_NULL：表示只包含非null的属性值。
     *              NON_ABSENT：表示属性值为null,或者JAVA8、Guava中的Optional
     *              NON_EMPTY：表示非null、""和数组集合isEmpty()=false都将会被忽略
     *              NON_DEFAULT：表示POJO类属性的值为缺省值是不序列化，如User类的 int age = 0; String username = null;
     *              CUSTOM:自定义，根据过滤器等
     *              USE_DEFAULTS：...
     * @Version  1.0
     */
    public static <T> String toJSONPrettyString(T o, Include include){
        try{
            if(null == include){
                include = Include.ALWAYS;
            }
            objectMapper.setSerializationInclusion(include);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
        } catch (JsonProcessingException e){
            e.printStackTrace();
            return null;
        }
    }
    /**
     * @Description JSON字符串转换为java对象,支持List、Map、Collection、字符串
     * @Version  1.0
     */
    public static <T> T toJavaBean(String str, Class<T> responseType){
        try{
            return objectMapper.readValue(str, responseType);
        } catch (JsonParseException e){
            e.printStackTrace();
            return null;
        } catch (JsonMappingException e){
            e.printStackTrace();
            return null;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

}

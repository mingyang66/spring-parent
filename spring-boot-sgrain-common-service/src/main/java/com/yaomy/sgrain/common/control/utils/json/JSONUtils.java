package com.yaomy.sgrain.common.control.utils.json;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.yaomy.sgrain.common.control.enums.DateFormatEnum;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

/**
 * @Description: JSON工具类
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
@SuppressWarnings("all")
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
    public static <T> T toJavaBean(String jsonString, Class<T> responseType){
        try {
            return objectMapper.readValue(jsonString, responseType);
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
    /**
     * @Description JSON字符串转换为java对象,支持List、Map、Collection、字符串
     * @Version  1.0
     */
    public static <T> T toJavaBean(File file, Class<T> responseType){
        try {
            return objectMapper.readValue(file, responseType);
        } catch (JsonMappingException e){
            e.printStackTrace();
            return null;
        } catch (JsonParseException e){
            e.printStackTrace();
            return null;
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 示例1：
     *         List<Map<Integer, String>> list = Lists.newArrayList();
     *         Map<Integer, String> map = Maps.newHashMap();
     *         map.put(12, "gg");
     *         map.put(34, "sd");
     *         list.add(map);
     *         List<Map<Integer, String>> list1 = toJavaBean(JSONUtils.toJSONString(list), ArrayList.class, HashMap.class);
     * 示例2：
     *         Map<Integer, String> map = Maps.newHashMap();
     *         map.put(12, "gg");
     *         map.put(34, "sd");
     *         Map<Integer, String> map1 = toJavaBean(JSONUtils.toJSONString(map), HashMap.class, Integer.class, String.class);
     * @param jsonString JSON字符串
     * @param parametrized 数据类型最外层class或者泛型实际的class, 如List<Map<String, Integer>>的List.class 或者Map<String, Integer>中的Map.class
     * @param parameterClasses 参数内部类型，如List<Map<String, Object>中的Map.class 或者Map<String, Integer>中的String.class、Integer.class
     * @param <T>
     * @return
     */
    public static <T> T toJavaBean(String jsonString, Class<?> parametrized, Class<?>... parameterClasses){
        try{
            JavaType javaType = javaType(parametrized, parameterClasses);
            return objectMapper.readValue(jsonString, javaType);
        } catch (JsonParseException e){
            e.printStackTrace();
        } catch (JsonMappingException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
    /**
     *  示例1：
     *  Map<Integer, String> map = Maps.newHashMap();
     *  JavaType javaType = javaType(Map.class, Integer.class, String.class);
     *  示例2：
     *   List<Map<Integer, String>> list = Lists.newArrayList();
     *   Map<Integer, String> map = Maps.newHashMap();
     *   map.put(12, "gg");
     *   map.put(34, "sd");
     *   list.add(map);
     *   JavaType javaType = javaType(List.class, Map.class);
     * @param parametrized 实际的数据类型，即最外层数据类型List
     * @param parameterClasses 内部参数类型，即Set.class Bean.class
     * @return
     */
    public static JavaType javaType(Class<?> parametrized, Class<?>... parameterClasses){
        return objectMapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
    }
    /**
     * @Description 将对象写入文件
     * @Version  1.0
     */
    public static boolean writeToFile(File file, Object o){
        try{
            objectMapper.writeValue(file, o);
            return true;
        } catch (JsonMappingException e){
            e.printStackTrace();
        } catch (JsonGenerationException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }
    /**
     * @Description 格式化，将对象写入文件
     * @Version  1.0
     */
    public static boolean writeToFilePretty(File file, Object o){
        try{
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, o);
            return true;
        } catch (JsonMappingException e){
            e.printStackTrace();
        } catch (JsonGenerationException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将对象转换为字节数组
     * @param value
     * @return
     */
    public static byte[] toByteArray(Object value){
        if(value == null){
            return new byte[]{};
        }
        try{
            return objectMapper.writeValueAsBytes(value);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return ArrayUtils.EMPTY_BYTE_ARRAY;
    }

    /**
     * 将字节数组转化为指定的对象
     * @param bytes 字节数组
     * @param responseType 返回值类型
     * @param <T>
     */
    public static <T> T toObject(byte[] bytes, Class<T> responseType){
        try {
            return objectMapper.readValue(bytes, responseType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 使用字节流将value对象输出
     * @param outputStream
     * @param value
     */
    public static void writeValue(OutputStream outputStream, Object value){
        try {
            objectMapper.writeValue(outputStream, value);
        } catch (IOException e){
            e.printStackTrace();
        }

    }
}

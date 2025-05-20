package com.emily.infrastructure.json;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Objects;

/**
 * JSON工具类
 *
 * @author Emily
 * @since 1.0
 */
public class JsonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    static {
        //对象的所有字段全部序列化
        objectMapper.setSerializationInclusion(Include.ALWAYS);
        //取消默认转换timestamps
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DATE_PATTERN));
        //忽略空Bean转json的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //忽略，在json字符串中存在但是在java对象中不存在的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //序列化和反序列化java.Time时间对象
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * 对象转换为json字符串, 支持List、Map、Collection、字符串
     *
     * @param o   参数对象
     * @param <T> 参数类型
     * @return json字符串
     * @since 1.0
     */
    public static <T> String toJSONString(T o) {
        return toJSONString(o, Include.ALWAYS);
    }

    /**
     * @param include 定义javaBean的那些属性需要序列化
     *                ALWAYS：始终包含javaBean的值，与属性的值无关。
     *                NON_NULL：表示只包含非null的属性值。
     *                NON_ABSENT：表示属性值为null,或者JAVA8、Guava中的Optional
     *                NON_EMPTY：表示非null、""和数组集合isEmpty()=false都将会被忽略
     *                NON_DEFAULT：表示POJO类属性的值为缺省值是不序列化，如User类的 int age = 0; String username = null;
     *                CUSTOM:自定义，根据过滤器等
     *                USE_DEFAULTS：...
     *                对象转换为json字符串, 支持List、Map、Collection、字符串
     * @param o       参数对象
     * @param <T>     参数类型
     * @return 转换后的json字符串
     * @since 1.0
     */
    public static <T> String toJSONString(T o, Include include) {
        try {
            if (null == include) {
                include = Include.ALWAYS;
            }
            objectMapper.setSerializationInclusion(include);
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("非法数据");
        }
    }

    /**
     * 带格式化， 对象转换为json字符串,支持List、Map、Collection、字符串
     *
     * @param o   对象
     * @param <T> 对象类型
     * @return 转换后的字符串对象
     * @since 1.0
     */
    public static <T> String toJSONPrettyString(T o) {
        return toJSONPrettyString(o, Include.ALWAYS);
    }

    /**
     * @param include 定义javaBean的那些属性需要序列化
     *                ALWAYS：始终包含javaBean的值，与属性的值无关。
     *                NON_NULL：表示只包含非null的属性值。
     *                NON_ABSENT：表示属性值为null,或者JAVA8、Guava中的Optional
     *                NON_EMPTY：表示非null、""和数组集合isEmpty()=false都将会被忽略
     *                NON_DEFAULT：表示POJO类属性的值为缺省值是不序列化，如User类的 int age = 0; String username = null;
     *                CUSTOM:自定义，根据过滤器等
     *                USE_DEFAULTS：...
     *                带格式化， 对象转换为json字符串,支持List、Map、Collection、字符串
     * @param o       对象
     * @param <T>     对象类型
     * @return 格式化后的json字符串对象
     * @since 1.0
     */
    public static <T> String toJSONPrettyString(T o, Include include) {
        try {
            if (null == include) {
                include = Include.ALWAYS;
            }
            objectMapper.setSerializationInclusion(include);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("非法数据");
        }
    }

    /**
     * JSON字符串转换为java对象, 支持List、Map、Collection、字符串
     *
     * @param file         文件对象
     * @param responseType 响应对象
     * @param <T>          实例对象
     * @return 转换后的对象
     * @since 1.0
     */
    public static <T> T toJavaBean(File file, Class<T> responseType) {
        try {
            return objectMapper.readValue(file, responseType);
        } catch (IOException e) {
            throw new IllegalArgumentException("非法数据");
        }
    }

    /**
     * JSON字符串转换为java对象, 支持List、Map、Collection、字符串
     *
     * @param jsonString   字符串对象
     * @param responseType 响应数据类型
     * @param <T>          实例对象
     * @return 转换后的实例对象
     * @since 1.0
     */
    public static <T> T toJavaBean(String jsonString, Class<T> responseType) {
        try {
            return objectMapper.readValue(jsonString, responseType);
        } catch (IOException e) {
            throw new IllegalArgumentException("非法数据");
        }
    }

    /**
     * 示例1：
     * <pre>
     *     {@code
     * List<Map<Integer, String>> list = Lists.newArrayList();
     * Map<Integer, String> map = Maps.newHashMap();
     * map.put(12, "gg");
     * map.put(34, "sd");
     * list.add(map);
     * List<Map<Integer, String>> list1 = toJavaBean(JsonUtils.toJSONString(list), ArrayList.class, HashMap.class);
     * }
     * </pre>
     * 示例2：
     * <pre>
     *     {@code
     * Map<Integer, String> map = Maps.newHashMap();
     * map.put(12, "gg");
     * map.put(34, "sd");
     * Map<Integer, String> map1 = toJavaBean(JsonUtils.toJSONString(map), HashMap.class, Integer.class, String.class);
     * }
     * </pre>
     *
     * @param jsonString       JSON字符串
     * @param parametrized     数据类型最外层class或者泛型实际的class, 如<pre>{@code List<Map<String, Integer>>的List.class 或者Map<String, Integer>中的Map.class}</pre>
     * @param parameterClasses 参数内部类型，如<pre>{@code List<Map<String, Object>中的Map.class 或者Map<String, Integer>中的String.class、Integer.class}</pre>
     * @param <T>              序列化目标类型
     * @return 转换后的实例对象
     */
    public static <T> T toJavaBean(String jsonString, Class<?> parametrized, Class<?>... parameterClasses) {
        try {
            JavaType javaType = javaType(parametrized, parameterClasses);
            return objectMapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            throw new IllegalArgumentException("非法数据");
        }
    }

    /**
     * json字符串反序列化
     * 示例：
     * <pre>
     *     {@code
     * Map<String, List<Integer>> map = Maps.newHashMap();
     * ObjectMapper mapper = new ObjectMapper();
     * JavaType javaType = JsonUtils.javaType(List.class, Integer.class);
     * JavaType javaType1 = JsonUtils.javaType(HashMap.class, String.class, javaType.getRawClass());
     * Map<String, List<Integer>> result = JsonUtils.toJavaBean(JsonUtils.toJSONString(map), javaType1);
     * }
     * </pre>
     *
     * @param jsonString json字符串
     * @param javaType   java数据类型 objectMapper.getTypeFactory().constructParametricType(parametrized, parameterClasses)
     * @param <T>        具体数据类型
     * @return 反序列化后的数据类型
     */
    public static <T> T toJavaBean(String jsonString, JavaType javaType) {
        try {
            return objectMapper.readValue(jsonString, javaType);
        } catch (IOException e) {
            throw new IllegalArgumentException("非法数据");
        }
    }

    /**
     * 将json字符串反序列化为指定的数据类型
     * 示例：
     * <pre>
     *     {@code
     *      List<Map<Long, Map<Integer, Integer>>> data2 = JsonUtils.toJavaBean(jsonString, new TypeReference<List<Map<Long, Map<Integer, Integer>>>>() {});
     *      }
     * </pre>
     *
     * @param jsonString    json字符串
     * @param typeReference TypeReference引用
     * @param <T>           转换的实际类型
     * @return 目标数据类型
     */
    public static <T> T toJavaBean(String jsonString, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(jsonString, typeReference);
        } catch (IOException e) {
            throw new IllegalArgumentException("非法数据");
        }
    }

    /**
     * 示例1：
     * <pre>
     *  {@code
     *      Map<Integer, String> map = Maps.newHashMap();
     *      JavaType javaType = javaType(Map.class, Integer.class, String.class);
     *  }
     * </pre>
     * 示例2：
     * <pre>
     *  {@code
     *      List<Map<Integer, String>> list = Lists.newArrayList();
     *      Map<Integer, String> map = Maps.newHashMap();
     *      map.put(12, "gg");
     *      map.put(34, "sd");
     *      list.add(map);
     *      JavaType javaType = javaType(List.class, Map.class);
     *  }
     * </pre>
     *
     * @param parametrized     实际的数据类型，即最外层数据类型List
     * @param parameterClasses 内部参数类型，即Set.class Bean.class
     * @return 数据对象
     */
    public static JavaType javaType(Class<?> parametrized, Class<?>... parameterClasses) {
        return objectMapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
    }

    /**
     * 将对象写入文件
     *
     * @param file 文件对象
     * @param o    待写入文件的对象
     * @since 1.0
     */
    public static void writeToFile(File file, Object o) {
        try {
            objectMapper.writeValue(file, o);
        } catch (IOException e) {
            throw new IllegalArgumentException("非法数据");
        }
    }

    /**
     * 格式化，将对象写入文件
     *
     * @param file 文件对象
     * @param o    待写入文件的对象
     * @since 1.0
     */
    public static void writeToFilePretty(File file, Object o) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, o);
        } catch (IOException e) {
            throw new IllegalArgumentException("非法数据");
        }
    }

    /**
     * 将对象转换为字节数组
     *
     * @param value 待转换对象
     * @return 字节数组
     */
    public static byte[] toByteArray(Object value) {
        if (value == null) {
            return new byte[]{};
        }
        try {
            return objectMapper.writeValueAsBytes(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("非法数据");
        }
    }

    /**
     * 从输入流InputStream中读取数据
     *
     * @param src          输入流对象
     * @param responseType 返回的数据对象
     * @param <T>          数据类型
     * @return 转换后的对象类型
     */
    public static <T> T toObject(InputStream src, Class<T> responseType) {
        try {
            return objectMapper.readValue(src, responseType);
        } catch (Exception e) {
            throw new IllegalArgumentException("非法数据");
        }
    }

    /**
     * 将字节数组转化为指定的对象
     *
     * @param bytes        字节数组
     * @param responseType 返回值类型
     * @param <T>          数据类型
     * @return 转换后的数据对象
     */
    public static <T> T toObject(byte[] bytes, Class<T> responseType) {
        if (bytes == null) {
            return null;
        }
        try {
            return objectMapper.readValue(bytes, responseType);
        } catch (IOException e) {
            throw new IllegalArgumentException("非法数据");
        }
    }

    /**
     * 使用字节流将value对象输出
     *
     * @param outputStream 输出流对象
     * @param value        值对象
     */
    public static void writeValue(OutputStream outputStream, Object value) {
        try {
            objectMapper.writeValue(outputStream, value);
        } catch (IOException e) {
            throw new IllegalArgumentException("非法数据");
        }

    }

    /**
     * 将指定的java对象转换为指定的class对象
     *
     * @param obj          原始对象
     * @param responseType 目标class对象
     * @param <T>          参数类型
     * @return 转换后对象
     */
    public static <T> T parseObject(Object obj, Class<T> responseType) {
        if (Objects.isNull(obj)) {
            return null;
        }
        try {
            return toJavaBean(toJSONString(obj), responseType);
        } catch (Exception exception) {
            return responseType.cast(obj);
        }
    }

    /**
     * 将字符串转换为JsonNode对象
     *
     * @param value 入参
     * @return 转换后的对象
     */
    public static JsonNode readTree(String value) {
        try {
            return objectMapper.readTree(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("非法参数");
        }
    }

    /**
     * 将字符串转换为JsonNode对象
     *
     * @param value 入参
     * @return 转换后的对象
     */
    public static JsonNode readTree(byte[] value) {
        try {
            return objectMapper.readTree(value);
        } catch (IOException e) {
            throw new IllegalArgumentException("非法参数");
        }
    }

    /**
     * 将对象类型转换为JsonNode
     *
     * @param value 对象数据类型
     * @param <T>   泛型类型
     * @return JsonNode
     */
    public static <T> JsonNode valueToTree(T value) {
        return objectMapper.valueToTree(value);
    }
}

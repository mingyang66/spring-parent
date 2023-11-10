package com.emily.infrastructure.redis.common;

import com.emily.infrastructure.date.DatePatternInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.text.SimpleDateFormat;

/**
 * Redis key value序列化方式工具
 *
 * @author :  Emily
 * @since :  2023/10/21 9:21 PM
 */
public class SerializationUtils {
    /**
     * 初始化string序列化对象
     *
     * @return 字符串序列化对象
     */
    public static StringRedisSerializer stringSerializer() {
        return new StringRedisSerializer();
    }

    /**
     * 初始化jackson序列化对象
     *
     * @return jackson序列化对象
     */
    public static Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        //对象的所有字段全部序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        //取消默认转换timestamps
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DatePatternInfo.YYYY_MM_DD_HH_MM_SS));
        //忽略空Bean转json的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //忽略，在json字符串中存在但是在java对象中不存在的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //序列化和反序列化java.Time时间对象
        objectMapper.registerModule(new JavaTimeModule());
        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        return new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
    }
}

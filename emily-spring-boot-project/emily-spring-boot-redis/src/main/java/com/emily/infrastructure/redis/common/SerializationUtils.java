package com.emily.infrastructure.redis.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.TimeZone;

/**
 * Redis key value序列化方式工具
 *
 * @author :  Emily
 * @since :  2023/10/21 9:21 PM
 */
public class SerializationUtils {
    /**
     * 日期格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

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
    public static JacksonJsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
        /*ObjectMapper objectMapper = new ObjectMapper();
        //对象的所有字段全部序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        //取消默认转换timestamps
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
        objectMapper.setDateFormat(new SimpleDateFormat(DATE_FORMAT));
        //忽略空Bean转json的错误
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //忽略，在json字符串中存在但是在java对象中不存在的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //序列化和反序列化java.Time时间对象
        objectMapper.registerModule(new JavaTimeModule());*/
        ObjectMapper objectMapper = JsonMapper.builder()
                //对象的所有字段全部序列化 objectMapper.setSerializationInclusion(Include.ALWAYS);
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(JsonInclude.Include.ALWAYS))
                //忽略空Bean转json的错误 objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                ////取消默认转换timestamps objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
                .configure(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                //时区
                .defaultTimeZone(TimeZone.getTimeZone(ZoneId.of("GMT+8")))
                //所有的java.util.Date、java.util.Calendar日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss objectMapper.setDateFormat(new SimpleDateFormat(DATE_PATTERN));
                .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                //忽略，在json字符串中存在但是在java对象中不存在的属性 objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .build();
        //使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        return new JacksonJsonRedisSerializer<>(objectMapper, Object.class);
    }
}

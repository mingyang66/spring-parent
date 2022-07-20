package com.emily.infrastructure.common.sensitive.serializer;

import com.emily.infrastructure.common.sensitive.annotation.Sensitive;
import com.emily.infrastructure.common.sensitive.strategy.SensitiveStrategy;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;
import java.util.Objects;

/**
 * @Description :  序列化注解自定义实现
 * @Author :  Emily
 * @CreateDate :  Created in 2022/7/19 5:24 下午
 */
public class SensitiveJsonSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private SensitiveStrategy strategy;

    @Override
    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
        jsonGenerator.writeString(strategy.desensitizer().apply(value));
    }

    /**
     * 获取属性上的注解属性
     */
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        Sensitive sensitive = property.getAnnotation(Sensitive.class);
        if (Objects.nonNull(sensitive) && Objects.equals(String.class, property.getType().getRawClass())) {
            this.strategy = sensitive.strategy();
            return this;
        }
        return prov.findValueSerializer(property.getType(), property);
    }
}

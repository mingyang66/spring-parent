package com.emily.infrastructure.security.utils;

import com.emily.infrastructure.security.annotation.SecurityModel;
import com.emily.infrastructure.security.annotation.SecurityProperty;
import com.emily.infrastructure.security.plugin.BasePlugin;
import com.emily.infrastructure.security.plugin.ComplexSecurityPlugin;
import com.emily.infrastructure.security.plugin.SecurityPluginRegistry;
import com.emily.infrastructure.security.plugin.SimpleSecurityPlugin;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.Ordered;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * 多语言解析
 *
 * @author Emily
 * @since Created in 2023/4/15 5:42 PM
 */
public class SecurityUtils {
    /**
     * 对实体类进行多语言翻译
     *
     * @param entity   实体类|普通对象
     * @param consumer 异常错误信息捕获处理
     * @param <T>      实体对象
     * @return 翻译后的实体类对象
     */
    public static <T> T securityElseGet(final T entity, Consumer<Throwable> consumer, final Class<?>... packClass) {
        Objects.requireNonNull(consumer, "consumer must not be null");
        try {
            return security(entity, packClass);
        } catch (Throwable ex) {
            consumer.accept(ex);
            return entity;
        }
    }

    /**
     * 对实体类进行多语言翻译
     *
     * @param entity 实体类|普通对象
     * @param <T>    实体对象
     * @return 翻译后的实体类对象
     * @throws Throwable 非法访问异常
     */
    public static <T> T security(final T entity, final Class<?>... packClass) throws Throwable {
        if (JavaBeanUtils.isFinal(entity)) {
            return entity;
        }
        if (entity instanceof Collection) {
            for (Object o : (Collection<?>) entity) {
                security(o, packClass);
            }
        } else if (entity instanceof Map) {
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) entity).entrySet()) {
                security(entry.getValue(), packClass);
            }
        } else if (entity.getClass().isArray()) {
            if (!entity.getClass().getComponentType().isPrimitive()) {
                for (Object v : (Object[]) entity) {
                    security(v, packClass);
                }
            }
        } else if (entity.getClass().isAnnotationPresent(SecurityModel.class)) {
            doSetField(entity);
        } else if (packClass.length > 0 && entity.getClass().isAssignableFrom(packClass[0])) {
            doSetField(entity, ArrayUtils.remove(packClass, 0));
        }
        return entity;
    }

    /**
     * 对实体类entity的属性及父类的属性遍历并对符合条件的属性进行多语言翻译
     *
     * @param entity 实体类对象
     * @param <T>    实体对象
     * @throws Throwable 非法访问异常
     */
    protected static <T> void doSetField(final T entity, final Class<?>... packClass) throws Throwable {
        Field[] fields = FieldUtils.getAllFields(entity.getClass());
        //按照插件优先级排序
        fields = Stream.of(fields).sorted(Comparator.comparingInt(field -> -doGetSecurityPluginOrder((Field) field)).reversed()).toArray(Field[]::new);
        for (Field field : fields) {
            if (JavaBeanUtils.isModifierFinal(field)) {
                continue;
            }
            field.setAccessible(true);
            Object value = field.get(entity);
            if (ObjectUtils.isEmpty(value)) {
                continue;
            }
            if (value instanceof String) {
                doGetSecurityPlugin(field, entity, value);
            } else if (value instanceof Collection) {
                doGetEntityColl(field, entity, value);
            } else if (value instanceof Map) {
                doGetEntityMap(field, entity, value);
            } else if (value.getClass().isArray()) {
                doGetEntityArray(field, entity, value);
            } else {
                security(value, packClass);
            }
        }
    }

    /**
     * 对基于插件注解标记的属性进行加密
     *
     * @param field  实体类属性对象
     * @param entity 实体类对象
     * @param value  属性值对象
     * @throws Throwable 抛出非法访问异常
     */
    @SuppressWarnings("unchecked")
    protected static <T> void doGetSecurityPlugin(final Field field, final T entity, final Object value) throws Throwable {
        if (field.isAnnotationPresent(SecurityProperty.class)) {
            SecurityProperty securityProperty = field.getAnnotation(SecurityProperty.class);
            if (securityProperty.value().isInterface()) {
                return;
            }
            String pluginId = doGetFirstCharIsLowerCase(securityProperty.value().getSimpleName());
            if (!SecurityPluginRegistry.containsPlugin(pluginId)) {
                SecurityPluginRegistry.registerSecurityPlugin(pluginId, securityProperty.value().getDeclaredConstructor().newInstance());
            }
            BasePlugin plugin = SecurityPluginRegistry.getSecurityPlugin(pluginId);
            Object result;
            if (plugin instanceof ComplexSecurityPlugin) {
                result = ((ComplexSecurityPlugin<Object, Object>) plugin).getPlugin(entity, value);
            } else {
                result = ((SimpleSecurityPlugin<Object>) plugin).getPlugin(value);
            }
            field.set(entity, result);
        }
    }

    /**
     * 对基于插件注解标记的属性进行加密
     *
     * @param field 实体类属性对象
     */
    protected static int doGetSecurityPluginOrder(final Field field) {
        try {
            if (field.isAnnotationPresent(SecurityProperty.class)) {
                SecurityProperty securityProperty = field.getAnnotation(SecurityProperty.class);
                if (securityProperty.value().isInterface()) {
                    return Ordered.LOWEST_PRECEDENCE;
                }
                String pluginId = doGetFirstCharIsLowerCase(securityProperty.value().getSimpleName());
                if (!SecurityPluginRegistry.containsPlugin(pluginId)) {
                    SecurityPluginRegistry.registerSecurityPlugin(pluginId, securityProperty.value().getDeclaredConstructor().newInstance());
                }
                BasePlugin plugin = SecurityPluginRegistry.getSecurityPlugin(pluginId);
                return plugin.getOrder();
            }
        } catch (Throwable ignored) {
        }
        return Ordered.LOWEST_PRECEDENCE;
    }

    /**
     * 对Collection集合中存储是字符串、实体对象进行多语言支持
     *
     * @param field  实体类属性对象
     * @param entity 实体类对象
     * @param value  属性值对象
     * @param <T>    实体对象类型
     * @throws Throwable 抛出非法访问异常
     */
    protected static <T> void doGetEntityColl(final Field field, final T entity, final Object value) throws Throwable {
        Collection<?> collection = ((Collection<?>) value);
        if (collection.stream().allMatch(o -> o instanceof String)) {
            doGetSecurityPlugin(field, entity, value);
            return;
        }
        for (Object v : collection) {
            if (ObjectUtils.isEmpty(v)) {
                continue;
            }
            security(v);
        }
    }

    /**
     * 对Map集合中存储是字符串、实体对象进行多语言支持
     *
     * @param field 实体类属性对象
     * @param value 属性值对象
     * @throws Throwable 抛出非法访问异常
     */
    protected static <T> void doGetEntityMap(final Field field, final T entity, final Object value) throws Throwable {
        @SuppressWarnings("unchecked")
        Map<Object, Object> dMap = (Map<Object, Object>) value;
        if (dMap.values().stream().allMatch(o -> o instanceof String)) {
            doGetSecurityPlugin(field, entity, value);
            return;
        }
        for (Map.Entry<?, ?> entry : dMap.entrySet()) {
            //Object key = entry.getKey();
            Object v = entry.getValue();
            if (ObjectUtils.isEmpty(v)) {
                continue;
            }
            if (v instanceof String) {
                continue;
            }
            security(v);
        }
    }

    /**
     * 对数组中存储是字符串、实体对象进行多语言支持
     *
     * @param field 实体类属性对象
     * @param value 属性值对象
     * @throws Throwable 抛出非法访问异常
     */
    protected static <T> void doGetEntityArray(final Field field, final T entity, final Object value) throws Throwable {
        if (value.getClass().getComponentType().isPrimitive()) {
            return;
        }
        Object[] arrays = ((Object[]) value);
        if (arrays instanceof String[]) {
            doGetSecurityPlugin(field, entity, value);
            return;
        }
        for (Object v : arrays) {
            if (ObjectUtils.isEmpty(v)) {
                continue;
            }
            security(value);
        }
    }


    /**
     * 将输入字符串的首字母转换为小写。
     *
     * @param input 需要转换的字符串
     * @return 首字母小写后的新字符串
     */
    static String doGetFirstCharIsLowerCase(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }
        // 获取首字母并转换为小写，然后拼接剩余部分
        return String.format("%s%s", Character.toLowerCase(input.charAt(0)), input.substring(1));
    }
}

package com.emily.infrastructure.security.utils;

import com.emily.infrastructure.security.annotation.SecurityModel;
import com.emily.infrastructure.security.annotation.SecurityProperty;
import com.emily.infrastructure.security.plugin.SecurityPlugin;
import com.emily.infrastructure.security.plugin.SecurityPluginRegistry;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

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
                doGetEntityArray(field, value);
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
    protected static <T> void doGetSecurityPlugin(final Field field, final T entity, final Object value) throws Throwable {
        if (field.isAnnotationPresent(SecurityProperty.class)) {
            SecurityProperty encryptionProperty = field.getAnnotation(SecurityProperty.class);
            if (encryptionProperty.value().isInterface()) {
                return;
            }
            String pluginId = doGetFirstCharIsLowerCase(encryptionProperty.value().getSimpleName());
            if (!SecurityPluginRegistry.containsPlugin(pluginId)) {
                SecurityPluginRegistry.registerSecurityPlugin(pluginId, encryptionProperty.value().getDeclaredConstructor().newInstance());
            }
            SecurityPlugin<Object, Object> plugin = SecurityPluginRegistry.getSecurityPlugin(pluginId);
            Object result = plugin.getPlugin(entity, value);
            field.set(entity, Objects.isNull(result) ? value : result);
        }
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
        Collection<Object> list = null;
        Collection<?> collection = ((Collection<?>) value);
        for (Object v : collection) {
            if (ObjectUtils.isEmpty(v)) {
                continue;
            }
            list = (list == null) ? new ArrayList<>() : list;
            list.add(security(v));
        }
        if (Objects.nonNull(list)) {
            field.set(entity, list);
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
        for (Map.Entry<?, ?> entry : dMap.entrySet()) {
            Object key = entry.getKey();
            Object v = entry.getValue();
            if (ObjectUtils.isEmpty(v)) {
                continue;
            }
            if (v instanceof String) {
                if (field.isAnnotationPresent(SecurityProperty.class)) {
                    continue;
                }
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
    protected static void doGetEntityArray(final Field field, final Object value) throws Throwable {
        if (value.getClass().getComponentType().isPrimitive()) {
            return;
        }
        Object[] arrays = ((Object[]) value);
        for (int i = 0; i < arrays.length; i++) {
            Object v = arrays[i];
            if (ObjectUtils.isEmpty(v)) {
                continue;
            }
           /* if ((v instanceof String) && field.isAnnotationPresent(I18nProperty.class)) {
                arrays[i] = doGetProperty((String) v, languageType);
            } else {
                translate(value);
            }*/
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

package com.emily.infrastructure.desensitize;

import com.emily.infrastructure.desensitize.annotation.*;
import com.emily.infrastructure.desensitize.plugin.DesensitizePlugin;
import com.emily.infrastructure.desensitize.plugin.DesensitizePluginRegistry;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

/**
 * 对实体类镜像脱敏，返回结构相同的非同一个对象
 *
 * @author Emily
 * @since :  Created in 2022/7/19 11:13 下午
 */
public class SensitizeUtils {

    /**
     * 支持指定外层包装类为标记脱敏标记的类对内层标记了敏感字段的类进行脱敏
     * 脱敏过程中如果发生异常，则原样返回
     *
     * @param entity    脱敏实体类对象
     * @param consumer  异常信息暴露处理接口
     * @param packClass 需脱敏的实体类对象外层包装类
     * @return 脱敏后的数据
     */
    public static Object acquireElseGet(final Object entity, Consumer<Throwable> consumer, final Class<?>... packClass) {
        Objects.requireNonNull(consumer, "consumer must not be null");
        try {
            return acquire(entity, packClass);
        } catch (Throwable ex) {
            consumer.accept(ex);
            return entity;
        }
    }

    /**
     * 对实体类镜像脱敏，返回结构相同的非同一个对象
     *
     * @param entity    需要脱敏的实体类对象，如果是数据值类型则直接返回
     * @param packClass 需脱敏的实体类对象外层包装类
     * @return 脱敏后的实体类对象
     * @throws Throwable 抛出非法访问异常
     */
    public static Object acquire(final Object entity, final Class<?>... packClass) throws Throwable {
        if (JavaBeanUtils.isFinal(entity)) {
            return entity;
        }
        if (entity instanceof Collection) {
            Collection<Object> coll = new ArrayList<>();
            for (Object o : (Collection<?>) entity) {
                coll.add(acquire(o, packClass));
            }
            return coll;
        } else if (entity instanceof Map) {
            Map<Object, Object> dMap = new HashMap<>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) entity).entrySet()) {
                Object key = entry.getKey();
                Object value = entry.getValue();
                dMap.put(key, acquire(value, packClass));
            }
            return dMap;
        } else if (entity.getClass().isArray()) {
            if (entity.getClass().getComponentType().isPrimitive()) {
                return entity;
            } else {
                Object[] v = (Object[]) entity;
                Object[] t = new Object[v.length];
                for (int i = 0; i < v.length; i++) {
                    t[i] = acquire(v[i], packClass);
                }
                return t;
            }
        } else if (entity.getClass().isAnnotationPresent(DesensitizeModel.class)) {
            return doSetField(entity);
        } else if (packClass.length > 0 && entity.getClass().isAssignableFrom(packClass[0])) {
            return doSetField(entity, ArrayUtils.remove(packClass, 0));
        }
        return entity;
    }

    /**
     * 获取实体类对象脱敏后的对象
     *
     * @param entity 需要脱敏的实体类对象
     * @return 实体类属性脱敏后的集合对象
     */
    protected static Map<String, Object> doSetField(final Object entity, final Class<?>... packClass) throws Throwable {
        Map<String, Object> fieldMap = new HashMap<>();
        Field[] fields = FieldUtils.getAllFields(entity.getClass());
        for (Field field : fields) {
            if (JavaBeanUtils.isModifierFinal(field)) {
                continue;
            }
            field.setAccessible(true);
            String name = field.getName();
            Object value = field.get(entity);
            if (ObjectUtils.isEmpty(value)) {
                continue;
            }
            if (field.isAnnotationPresent(DesensitizeNullProperty.class)) {
                fieldMap.put(name, doGetEntityNull(field, value));
            } else if (field.isAnnotationPresent(DesensitizePluginProperty.class)) {
                fieldMap.put(name, doGetEntityPlugin(field, value));
            } else if (value instanceof String) {
                fieldMap.put(name, doGetEntityStr(field, value));
            } else if (value instanceof Collection) {
                fieldMap.put(name, doGetEntityColl(field, value));
            } else if (value instanceof Map) {
                fieldMap.put(name, doGetEntityMap(field, value));
            } else if (value.getClass().isArray()) {
                fieldMap.put(name, doGetEntityArray(field, value));
            } else {
                fieldMap.put(name, acquire(value, packClass));
            }
        }
        fieldMap.putAll(doGetEntityFlexible(entity));
        return fieldMap;
    }

    /**
     * 判断是否将值设置为null
     *
     * @param field 字段对象
     * @param value 字段值
     * @return 字段值
     */
    protected static Object doGetEntityNull(final Field field, final Object value) {
        if (field.getType().isPrimitive()) {
            return value;
        }
        return null;
    }

    /**
     * @param field 实体类属性对象
     * @param value 属性值
     * @return 脱敏后的数据对象
     * @throws Throwable 抛出非法访问异常
     */
    protected static Object doGetEntityStr(final Field field, final Object value) throws Throwable {
        if (field.isAnnotationPresent(DesensitizeProperty.class)) {
            return DataMaskUtils.doGetProperty((String) value, field.getAnnotation(DesensitizeProperty.class).value());
        } else {
            return acquire(value);
        }
    }

    /**
     * @param field 实体类属性对象
     * @param value 属性值
     * @return 脱敏后的数据对象
     * @throws Throwable 抛出非法访问异常
     */
    protected static Object doGetEntityColl(final Field field, final Object value) throws Throwable {
        Collection<Object> list = new ArrayList<>();
        Collection<?> collection = (Collection<?>) value;
        for (Object v : collection) {
            if (Objects.isNull(v)) {
                list.add(null);
            } else if ((v instanceof String) && field.isAnnotationPresent(DesensitizeProperty.class)) {
                list.add(DataMaskUtils.doGetProperty((String) v, field.getAnnotation(DesensitizeProperty.class).value()));
            } else {
                list.add(acquire(v));
            }
        }
        return list;
    }

    /**
     * @param field 实体类属性对象
     * @param value 属性值
     * @return 脱敏后的数据对象
     * @throws Throwable 抛出非法访问异常
     */
    protected static Object doGetEntityMap(final Field field, final Object value) throws Throwable {
        Map<Object, Object> dMap = new HashMap<>();
        @SuppressWarnings("unchecked")
        Map<Object, Object> entryMap = ((Map<Object, Object>) value);
        for (Map.Entry<Object, Object> entry : entryMap.entrySet()) {
            Object key = entry.getKey();
            Object v = entry.getValue();
            if (Objects.isNull(v)) {
                dMap.put(key, null);
                continue;
            }
            if (v instanceof String) {
                if (field.isAnnotationPresent(DesensitizeMapProperty.class)) {
                    DesensitizeMapProperty desensitizeMapProperty = field.getAnnotation(DesensitizeMapProperty.class);
                    int index = (key instanceof String) ? Arrays.asList(desensitizeMapProperty.value()).indexOf(key) : -1;
                    if (index < 0) {
                        dMap.put(key, acquire(v));
                        continue;
                    }
                    DesensitizeType type = DesensitizeType.DEFAULT;
                    if (index <= desensitizeMapProperty.desensitizeType().length - 1) {
                        type = desensitizeMapProperty.desensitizeType()[index];
                    }
                    dMap.put(key, DataMaskUtils.doGetProperty((String) v, type));
                    continue;
                } else if (field.isAnnotationPresent(DesensitizeProperty.class)) {
                    dMap.put(key, DataMaskUtils.doGetProperty((String) v, field.getAnnotation(DesensitizeProperty.class).value()));
                }
            }
            dMap.put(key, acquire(v));
        }
        return dMap;
    }

    /**
     * @param field 实体类属性对象
     * @param value 属性值
     * @return 脱敏后的数据对象
     * @throws Throwable 抛出非法访问异常
     */
    protected static Object doGetEntityArray(final Field field, final Object value) throws Throwable {
        if (value.getClass().getComponentType().isPrimitive()) {
            return value;
        } else {
            Object[] v = (Object[]) value;
            Object[] t = new Object[v.length];
            for (int i = 0; i < v.length; i++) {
                if (Objects.isNull(v[i])) {
                    t[i] = null;
                } else if ((v[i] instanceof String) && field.isAnnotationPresent(DesensitizeProperty.class)) {
                    t[i] = DataMaskUtils.doGetProperty((String) v[i], field.getAnnotation(DesensitizeProperty.class).value());
                } else {
                    t[i] = acquire(v[i]);
                }
            }
            return t;
        }
    }

    /**
     * 通过两个字段key、value指定传递不同的值，灵活指定哪些字段值进行脱敏处理
     *
     * @param entity 实体类
     * @return 复杂类型字段脱敏后的数据集合
     * @throws Throwable 抛出非法访问异常
     */
    protected static Map<String, Object> doGetEntityFlexible(final Object entity) throws Throwable {
        Map<String, Object> flexFieldMap = null;
        Field[] fields = FieldUtils.getFieldsWithAnnotation(entity.getClass(), DesensitizeFlexibleProperty.class);
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(entity);
            if (Objects.isNull(value)) {
                continue;
            }
            DesensitizeFlexibleProperty desensitizeFlexibleProperty = field.getAnnotation(DesensitizeFlexibleProperty.class);
            if (ObjectUtils.isEmpty(desensitizeFlexibleProperty.value()) || StringUtils.isBlank(desensitizeFlexibleProperty.target())) {
                continue;
            }
            Field flexibleField = FieldUtils.getField(entity.getClass(), desensitizeFlexibleProperty.target(), true);
            if (Objects.isNull(flexibleField)) {
                continue;
            }
            Object flexibleValue = flexibleField.get(entity);
            if (Objects.isNull(flexibleValue) || !(flexibleValue instanceof String)) {
                continue;
            }
            int index = Arrays.asList(desensitizeFlexibleProperty.value()).indexOf((String) value);
            if (index < 0) {
                continue;
            }
            DesensitizeType desensitizeType = DesensitizeType.DEFAULT;
            if (index <= desensitizeFlexibleProperty.desensitizeType().length - 1) {
                desensitizeType = desensitizeFlexibleProperty.desensitizeType()[index];
            }
            flexFieldMap = Objects.isNull(flexFieldMap) ? new HashMap<>() : flexFieldMap;
            flexFieldMap.put(desensitizeFlexibleProperty.target(), DataMaskUtils.doGetProperty((String) flexibleValue, desensitizeType));
        }
        return Objects.isNull(flexFieldMap) ? Collections.emptyMap() : flexFieldMap;
    }

    /**
     * 对基于插件注解标记的属性进行脱敏
     *
     * @param field 实体类属性对象
     * @param value 属性值对象
     * @throws Throwable 抛出非法访问异常
     */
    protected static Object doGetEntityPlugin(final Field field, final Object value) throws Throwable {
        DesensitizePluginProperty desensitizePluginProperty = field.getAnnotation(DesensitizePluginProperty.class);
        if (desensitizePluginProperty.value().isInterface()) {
            return value;
        }
        String pluginId = doGetFirstCharIsLowerCase(desensitizePluginProperty.value().getSimpleName());
        if (!DesensitizePluginRegistry.containsPlugin(pluginId)) {
            DesensitizePluginRegistry.registerPlugin(pluginId, desensitizePluginProperty.value().getDeclaredConstructor().newInstance());
        }
        DesensitizePlugin<Object> plugin = DesensitizePluginRegistry.getPlugin(pluginId);
        if (plugin.support(value)) {
            Object result = plugin.getPlugin(value, desensitizePluginProperty.desensitizeType());
            return Objects.isNull(result) ? value : result;
        } else {
            throw new UnsupportedOperationException(String.format("字段%s和插件%s不匹配", field.getName(), desensitizePluginProperty.value()));
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

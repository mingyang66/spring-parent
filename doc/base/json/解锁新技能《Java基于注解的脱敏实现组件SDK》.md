#### 解锁新技能《Java基于注解的脱敏实现组件SDK》

> 平时开发的过程中经常会遇到对一些敏感的字段进行脱敏处理，防止信息泄漏，如：邮箱、用户名、密码等；做为一个优秀的程序员我们不应该遇到这种问题时就做特殊处理，重复做相同的工作，所以我们应该写一个基础库SDK，解决重复的问题；

##### 一、定义注解

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonSensitive {
}
```

> @JsonSensitive标注在类上，表示此类需要进行脱敏处理；

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonSimField {
    /**
     * 脱敏类型，见枚举类型{@link SensitiveType}
     *
     * @return
     */
    SensitiveType value() default SensitiveType.DEFAULT;
}
```

> @JsonSimField标注在类的String、Collection<String>、String[]字段上，表示对这些字段值进行脱敏处理；

```java
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonFlexField {
    /**
     * 要隐藏的参数key名称
     *
     * @return
     */
    String[] fieldKeys() default {};

    /**
     * 要隐藏的参数值的key名称
     *
     * @return
     */
    String fieldValue();

    /**
     * 脱敏类型，见枚举类型{@link SensitiveType}
     *
     * @return
     */
    SensitiveType[] types() default {};
}
```

> @JsonFlexField注解标注在复杂数据类型字段上，具体的使用方法会在后面举例说明；

##### 二、实现对字段脱敏处理的核心实现类

```java
public class DeSensitiveUtils {

    public static final Logger logger = LoggerFactory.getLogger(DeSensitiveUtils.class);

    /**
     * @param entity 实体类|普通对象
     * @return 对实体类进行脱敏，返回原来的实体类对象
     */
    public static <T> T acquire(final T entity) {
        try {
            if (JavaBeanUtils.isFinal(entity)) {
                return entity;
            }
            if (entity instanceof Collection) {
                for (Iterator it = ((Collection) entity).iterator(); it.hasNext(); ) {
                    acquire(it.next());
                }
            } else if (entity instanceof Map) {
                for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) entity).entrySet()) {
                    acquire(entry.getValue());
                }
            } else if (entity.getClass().isArray()) {
                if (!entity.getClass().getComponentType().isPrimitive()) {
                    for (Object v : (Object[]) entity) {
                        acquire(v);
                    }
                }
            } else if (entity instanceof BaseResponse) {
                acquire(((BaseResponse) entity).getData());
            } else if (entity.getClass().isAnnotationPresent(JsonSensitive.class)) {
                doSetField(entity);
            }
        } catch (IllegalAccessException exception) {
            logger.error(PrintExceptionInfo.printErrorInfo(exception));
        }
        return entity;
    }

    /**
     * @param entity 实体类对象
     * @throws IllegalAccessException 非法访问异常
     * @Description 对实体类entity的属性及父类的属性遍历并对符合条件的属性进行多语言翻译
     */
    protected static <T> void doSetField(final T entity) throws IllegalAccessException {
        Field[] fields = FieldUtils.getAllFields(entity.getClass());
        for (Field field : fields) {
            if (JavaBeanUtils.isModifierFinal(field)) {
                continue;
            }
            field.setAccessible(true);
            Object value = field.get(entity);
            if (Objects.isNull(value)) {
                continue;
            }
            if (value instanceof String) {
                doGetEntityStr(field, entity, value);
            } else if (value instanceof Collection) {
                doGetEntityColl(field, entity, value);
            } else if (value instanceof Map) {
                doGetEntityMap(field, entity, value);
            } else if (value.getClass().isArray()) {
                doGetEntityArray(field, entity, value);
            } else {
                acquire(value);
            }
        }
        doGetEntityFlex(entity);
    }

    /**
     * @param field  实体类属性对象
     * @param entity 实体类对象
     * @param value  属性值对象
     * @throws IllegalAccessException 抛出非法访问异常
     * @Description 对字符串进行多语言支持
     */
    protected static <T> void doGetEntityStr(final Field field, final T entity, final Object value) throws IllegalAccessException {
        if (field.isAnnotationPresent(JsonSimField.class)) {
            field.set(entity, DataMaskUtils.doGetProperty((String) value, field.getAnnotation(JsonSimField.class).value()));
        } else {
            acquire(value);
        }
    }

    /**
     * @param field  实体类属性对象
     * @param entity 实体类对象
     * @param value  属性值对象
     * @throws IllegalAccessException 抛出非法访问异常
     * @Description 对Collection集合中存储是字符串、实体对象进行多语言支持
     */
    protected static <T> void doGetEntityColl(final Field field, final T entity, final Object value) throws IllegalAccessException {
        Collection<Object> list = null;
        Collection collection = ((Collection) value);
        for (Iterator it = collection.iterator(); it.hasNext(); ) {
            Object v = it.next();
            if (Objects.isNull(v)) {
                continue;
            }
            if ((v instanceof String) && field.isAnnotationPresent(JsonSimField.class)) {
                list = (list == null) ? Lists.newArrayList() : list;
                list.add(DataMaskUtils.doGetProperty((String) v, field.getAnnotation(JsonSimField.class).value()));
            } else {
                acquire(v);
            }
        }
        if (Objects.nonNull(list)) {
            field.set(entity, list);
        }
    }

    /**
     * @param field  实体类属性对象
     * @param entity 实体类对象
     * @param value  属性值对象
     * @throws IllegalAccessException 抛出非法访问异常
     * @Description 对Map集合中存储是字符串、实体对象进行多语言支持
     */
    protected static <T> void doGetEntityMap(final Field field, final T entity, final Object value) throws IllegalAccessException {
        Map<Object, Object> dMap = ((Map<Object, Object>) value);
        for (Map.Entry<Object, Object> entry : dMap.entrySet()) {
            Object key = entry.getKey();
            Object v = entry.getValue();
            if (Objects.isNull(v)) {
                continue;
            }
            if ((v instanceof String) && field.isAnnotationPresent(JsonSimField.class)) {
                dMap.put(key, DataMaskUtils.doGetProperty((String) v, field.getAnnotation(JsonSimField.class).value()));
            } else {
                acquire(value);
            }
        }
    }

    /**
     * @param field  实体类属性对象
     * @param entity 实体类对象
     * @param value  属性值对象
     * @throws IllegalAccessException 抛出非法访问异常
     * @Description 对数组中存储是字符串、实体对象进行多语言支持
     */
    protected static <T> void doGetEntityArray(final Field field, final T entity, final Object value) throws IllegalAccessException {
        if (value.getClass().getComponentType().isPrimitive()) {
            return;
        }
        Object[] arrays = ((Object[]) value);
        for (int i = 0; i < arrays.length; i++) {
            Object v = arrays[i];
            if (Objects.isNull(v)) {
                continue;
            }
            if ((v instanceof String) && field.isAnnotationPresent(JsonSimField.class)) {
                arrays[i] = DataMaskUtils.doGetProperty((String) v, field.getAnnotation(JsonSimField.class).value());
            } else {
                acquire(value);
            }
        }
    }

    /**
     * @param entity 实体类对象
     * @throws IllegalAccessException 抛出非法访问异常
     */
    protected static <T> void doGetEntityFlex(final T entity) throws IllegalAccessException {
        Field[] fields = FieldUtils.getFieldsWithAnnotation(entity.getClass(), JsonFlexField.class);
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(entity);
            if (Objects.isNull(value)) {
                continue;
            }
            JsonFlexField jsonFlexField = field.getAnnotation(JsonFlexField.class);
            if (Objects.isNull(jsonFlexField.fieldValue())) {
                return;
            }
            Field flexField = FieldUtils.getField(entity.getClass(), jsonFlexField.fieldValue(), true);
            if (Objects.isNull(flexField)) {
                return;
            }
            Object flexValue = flexField.get(entity);
            if (Objects.isNull(flexValue) || !(flexValue instanceof String)) {
                return;
            }
            int index = Arrays.asList(jsonFlexField.fieldKeys()).indexOf((String) value);
            if (index < 0) {
                return;
            }
            SensitiveType type;
            if (index >= jsonFlexField.types().length) {
                type = SensitiveType.DEFAULT;
            } else {
                type = jsonFlexField.types()[index];
            }
            flexField.set(entity, DataMaskUtils.doGetProperty((String) flexValue, type));
        }
    }
}

```

##### 三、基于注解的脱敏SDK使用案例

- 对实体类中字段为字符串类型脱敏处理

```java
@JsonSensitive
public class PubRequest {
    @JsonSimField(SensitiveType.USERNAME)
    public String username;
    @JsonSimField
    public String password;
    }
```

- 对实体类中字段是List<String>、Map<String,String>、String[]集合类型进行脱敏处理

```java
@JsonSensitive
public class PubRequest {
    @JsonSimField
    public Map<String, String> work;
    @JsonSimField
    public List<String> jobList;
    @JsonSimField
    public String[] jobs;
}
```

- 实体类中的字段是复杂数据类型脱敏处理

```java
@JsonSensitive
public class JsonRequest extends Animal{
    @JsonFlexField(fieldKeys = {"email", "phone"}, fieldValue = "fieldValue", types = {SensitiveType.EMAIL, SensitiveType.PHONE})
    private String fieldKey;
    private String fieldValue;
}
```

> 复杂数据类型其实就是fieldKey可以指定多个不同的字段名，fieldValue是具体的字段值，如果fieldKey是email时fieldValue传递的就是邮箱，就按照types中指定脱敏策略为邮箱的策略脱敏；

- 实体类中的属性字段是集合类型，集合中存放的是嵌套的实体类

```java
    @JsonSensitive
    public static class Job {
        @JsonSimField(SensitiveType.DEFAULT)
        private String work;
        @JsonSimField(SensitiveType.EMAIL)
        private String email;
    }
```

嵌套实体类属性字段

```java
    public Job job;
    public Map<String, Object> work;
    public List<PubResponse.Job> jobList;
    public PubResponse.Job[] jobs;
```

>
如果实体类中的集合中存放的是实体类，并且这个实体类标注了@JsonSensitive注解，则会对嵌套实体类中标注了@JsonSimField、@JsonFlexField注解的字段进行脱敏处理；同样如果最外层是集合、数组、key-value类型则也会对内部嵌套的实体类进行脱敏处理；

本文只对脱敏SDK做大概的阐述，如果你需要源码可以到个人GitHub上去拉；本文的示例是对当前实体类对象本身进行脱敏处理，返回的还是原来的对象本身，个人GitHub示例中还有一个返回是非当前对象的SDK工具类SensitiveUtils；

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
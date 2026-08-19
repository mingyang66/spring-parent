- pom依赖引用

```xml
            <dependency>
                <groupId>io.github.mingyang66</groupId>
                <artifactId>emily-spring-boot-tracing</artifactId>
                <version>${revision}</version>
            </dependency>
```

- 组件开关及SystemNumberHelper帮助类

```properties
# 组件开关
spring.emily.tracing.enabled=true
# 系统标识配置
spring.emily.tracing.system-number=EMILY-Tracing
```



- ###### 持有上下文属性类TracingHolder

```java
public class TracingHolder {
    /**
     * 事务唯一编号
     */
    private String traceId;
    /**
     * 系统编号|标识
     */
    private String systemNumber;
    /**
     * 语言
     */
    private String language;
    /**
     * 开启时间
     */
    private Instant startTime;
    /**
     * API接口耗时
     */
    private long spentTime;
    /**
     * 客户端IP
     */
    private String clientIp;
    /**
     * 服务端IP
     */
    private String serverIp;
    /**
     * 版本类型，com.emily.android
     */
    private String appType;
    /**
     * 版本号，4.1.4
     */
    private String appVersion;
    /**
     * (逻辑)是否servlet容器上下文，默认：false
     */
    private boolean servlet;
    /**
     * 当前上下文所处阶段标识
     */
    private TracingStage tracingStage;
}
```

- 请求阶段标识TracingStage

```java
public enum TracingStage {
    //参数校验之前
    PARAMETER,
    //控制器方法调用之前
    CONTROLLER,
    OTHER;
}
```

- 请求上下文存储类LocalContextHolder

```java
public class LocalContextHolder {

    private static final ThreadLocal<TracingHolder> CONTEXT = new TransmittableThreadLocal<>() {
        @Override
        protected TracingHolder initialValue() {
            return TracingHolder.newBuilder().build();
        }

        /**
         * 将子线程的初始上下文值设置为null
         * ----------------------------------------------------------------------
         * 关闭父子线程之间的继承关系，为什么要关闭继承关系？
         * 1. 在线程池的场景下会触发父线程已经remove掉上下文值，子线程还持有从父线程继承的上下文值，子线程结束后会将线程归还给线程池，归还后线程有可能会被复用，
         * 这样就可能会导致一部分值一直无法被GC收回，如果复用的数量过多可能导致OOM，而且还有可能导致其它线程拿到了本不属于当前线程的数据；
         * ----------------------------------------------------------------------
         * @param parentValue 父线程的值对象
         * @return 子线程的初始值对象
         * @see <a href="https://github.com/alibaba/transmittable-thread-local/issues/521">...</a>
         */
        @Override
        protected TracingHolder childValue(TracingHolder parentValue) {
            //调用父类的初始化方法可以确保子类初始化为null
            return super.initialValue();
        }
    };

    /**
     * 设置当前线程持有的数据源
     *
     * @param holder 上下文对象
     */
    public static void bind(TracingHolder holder) {
        CONTEXT.set(holder);
    }

    /**
     * 获取当前线程持有的数据源
     *
     * @return 上下文对象
     */
    public static TracingHolder current() {
        return CONTEXT.get();
    }

    /**
     * 是否移除上下文中文存储的值
     *
     * @param servlet 是否servlet上下文
     */
    public static void unbind(boolean servlet) {
        if (servlet) {
            CONTEXT.remove();
        }
    }

    /**
     * 如果当前上下文是非servlet上下文场景才会移除上下文中存储的数据
     */
    public static void unbind() {
        if (!current().isServlet()) {
            CONTEXT.remove();
        }
    }
}

```

- @TracingOperation标记非servlet上下文方法执行完毕后移除上下文

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TracingOperation {
    /**
     * 描述：标记继承上下文属性注解，此属性无其他作用
     */
    String message() default "标记继承上下文属性注解";

}

```
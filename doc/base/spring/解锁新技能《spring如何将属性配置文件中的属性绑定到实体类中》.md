#### 解锁新技能《spring如何将属性配置文件中的属性绑定到实体类中》

> 在springboot中将配置文件中的属性绑定到指定的实体类上可以通过自动化配置的方式实现，也可以通过手动方式从Environment环境变量中取出再赋值给实体类；但是在有些场景下自动化配置这种方案是行不通的，例如：ApplicationListener、ApplicationContextInitializer进行初始化调用时属性配置只在Environment环境变量中存在，还未绑定到实体类上时，而我们又不想通过手动方式一个个属性赋值，那我们又如何操作呢？

##### 一、定义一个属性配置类

```java
@ConfigurationProperties(prefix = LogbackProperties.PREFIX)
public class LogbackProperties  {
    /**
     * 前缀
     */
    public static final String PREFIX = "spring.emily.logback";
    
    private String root;
    private String level;

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
```

##### 二、在ApplicationContextInitializer实现类中进行属性绑定

```java
public class LogbackApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {

    /**
     * 初始化优先级低于org.springframework.cloud.bootstrap.config.PropertySourceBootstrapConfiguration类
     *
     * @return 优先级
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 11;
    }

    @Override
    public void initialize(ConfigurableApplicationContext context) {
        // 将属性配置绑定到配置类上
        LogbackProperties properties = Binder.get(context.getEnvironment()).bindOrCreate(LogbackProperties.PREFIX, LogbackProperties.class);
        // 初始化日志SDK上下文
        LoggerContextManager.init(properties);
    }
}
```

> 通过Binder类可以将environment环境变量中前缀为spring.emily.logback的属性绑定到LogbackProperties实体类上，这样既简单又优雅的实现了我们预期的操作；

GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
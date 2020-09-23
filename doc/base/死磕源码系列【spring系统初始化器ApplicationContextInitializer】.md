### 死磕源码系列【spring系统初始化器ApplicationContextInitializer】

> ApplicationContextInitializer初始化器接口是Spring boot应用上下文（ConfigurableApplicationContext）一个回调接口，会在refresh方法之前调用；通常用于对应用上下文容器进行一些属性初始化的web应用程序中,应用上下文类必须是ConfigurableApplicationContext的子类，springboot中使用的实际是org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext容器，通常用于设置环境配置、监听器、工厂钩子BeanFactoryPostProcessor

参考Springboot SPI加载接口实现类：[https://mingyang.blog.csdn.net/article/details/108681609](https://mingyang.blog.csdn.net/article/details/108681609)

##### 1.springboot初始化器使用非常简单，首先定义一个实现了ApplicationContextInitializer接口的类

```java
public class SmallGrainApplicationContenxtInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>, Ordered {
     @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        LoggerUtils.info(SmallGrainApplicationContenxtInitializer.class, "Small Grain【小米粒】初始化器开始初始化IOC容器了,容器名为："+applicationContext.getClass().getName());
    }
}
```



##### 2.在META-INF/spring.factories配置文件中配置初始化器

```java
#Initializers
org.springframework.context.ApplicationContextInitializer=\
  com.sgrain.boot.autoconfigure.initializers.SmallGrainApplicationContenxtInitializer
```

配置完成后启动项目在控制台上就会输出如下：

```java
2020-09-22 18:22:33.222  INFO 59417 --- [           main] SmallGrainApplicationContenxtInitializer : Small Grain【小米粒】初始化器开始初始化IOC容器了：org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext
```

##### 3.源码解析

初始化器的使用很简单，但是初始化器是在哪里加载，哪里调用呢？这个疑问我们接下来一步步的分析：

首先要知道springboot使用的是SPI技术加载定义好的ApplicationContextInitializer接口实现类，加载的详细过程参考：[https://mingyang.blog.csdn.net/article/details/108681609](https://mingyang.blog.csdn.net/article/details/108681609)；我们进入SpringApplication#run方法，里面有一个prepareContext方法调用，进入prepareContext方法：

```java
private void prepareContext(ConfigurableApplicationContext context, ConfigurableEnvironment environment,
			SpringApplicationRunListeners listeners, ApplicationArguments applicationArguments, Banner printedBanner) {
		context.setEnvironment(environment);
		postProcessApplicationContext(context);
  	//此处调用应用上下文的初始化器
		applyInitializers(context);
		listeners.contextPrepared(context);
		}
```

进入applyInitializers方法：

```java
	protected void applyInitializers(ConfigurableApplicationContext context) {
		for (ApplicationContextInitializer initializer : getInitializers()) {
			Class<?> requiredType = GenericTypeResolver.resolveTypeArgument(initializer.getClass(),
					ApplicationContextInitializer.class);
			Assert.isInstanceOf(requiredType, context, "Unable to call initializer.");
      //调用初始化器方法
			initializer.initialize(context);
		}
	}
```



GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)


### 浅谈Spring @Order注解的使用

注解@Order或者接口Ordered的作用是定义Spring IOC容器中Bean的执行顺序的优先级，而不是定义Bean的加载顺序，Bean的加载顺序不受@Order或Ordered接口的影响；

#### 1.@Order的注解源码解读
```
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface Order {

	/**
	 * 默认是最低优先级,值越小优先级越高
	 */
	int value() default Ordered.LOWEST_PRECEDENCE;

}
```
* 注解可以作用在类(接口、枚举)、方法、字段声明（包括枚举常量）；
* 注解有一个int类型的参数，可以不传，默认是最低优先级；
* 通过常量类的值我们可以推测参数值越小优先级越高；

#### 2.Ordered接口类
```
package org.springframework.core;

public interface Ordered {
    int HIGHEST_PRECEDENCE = -2147483648;
    int LOWEST_PRECEDENCE = 2147483647;

    int getOrder();
}
```

#### 3.创建BlackPersion、YellowPersion类，这两个类都实现CommandLineRunner

>实现CommandLineRunner接口的类会在Spring IOC容器加载完毕后执行，适合预加载类及其它资源；也可以使用ApplicationRunner,使用方法及效果是一样的
```
package com.yaomy.common.order;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
@Component
@Order(1)
public class BlackPersion implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("----BlackPersion----");
    }
}
```

```
package com.yaomy.common.order;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
@Component
@Order(0)
public class YellowPersion implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        System.out.println("----YellowPersion----");
    }
}
```

#### 4.启动应用程序打印出结果
```
----YellowPersion----
----BlackPersion----
```

>我们可以通过调整@Order的值来调整类执行顺序的优先级，即执行的先后；当然也可以将@Order注解更换为Ordered接口，效果是一样的

#### 5.到这里可能会疑惑IOC容器是如何根据优先级值来先后执行程序的，那接下来看容器是如何加载component的

* 看如下的启动main方法
```
@SpringBootApplication
public class CommonBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(CommonBootStrap.class, args);
    }
}
```
>这个不用过多的解释，进入run方法...
```

    public ConfigurableApplicationContext run(String... args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ConfigurableApplicationContext context = null;
        Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList();
        this.configureHeadlessProperty();
        SpringApplicationRunListeners listeners = this.getRunListeners(args);
        listeners.starting();

        Collection exceptionReporters;
        try {
            ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
            ConfigurableEnvironment environment = this.prepareEnvironment(listeners, applicationArguments);
            this.configureIgnoreBeanInfo(environment);
            Banner printedBanner = this.printBanner(environment);
            context = this.createApplicationContext();
            exceptionReporters = this.getSpringFactoriesInstances(SpringBootExceptionReporter.class, new Class[]{ConfigurableApplicationContext.class}, context);
            this.prepareContext(context, environment, listeners, applicationArguments, printedBanner);
            this.refreshContext(context);
            this.afterRefresh(context, applicationArguments);
            stopWatch.stop();
            if (this.logStartupInfo) {
                (new StartupInfoLogger(this.mainApplicationClass)).logStarted(this.getApplicationLog(), stopWatch);
            }

            listeners.started(context);
            //这里是重点，调用具体的执行方法
            this.callRunners(context, applicationArguments);
        } catch (Throwable var10) {
            this.handleRunFailure(context, var10, exceptionReporters, listeners);
            throw new IllegalStateException(var10);
        }

        try {
            listeners.running(context);
            return context;
        } catch (Throwable var9) {
            this.handleRunFailure(context, var9, exceptionReporters, (SpringApplicationRunListeners)null);
            throw new IllegalStateException(var9);
        }
    }
   private void callRunners(ApplicationContext context, ApplicationArguments args) {
        List<Object> runners = new ArrayList();
        runners.addAll(context.getBeansOfType(ApplicationRunner.class).values());
        runners.addAll(context.getBeansOfType(CommandLineRunner.class).values());
        //重点来了，按照定义的优先级顺序排序
        AnnotationAwareOrderComparator.sort(runners);
        Iterator var4 = (new LinkedHashSet(runners)).iterator();
        //循环调用具体方法
        while(var4.hasNext()) {
            Object runner = var4.next();
            if (runner instanceof ApplicationRunner) {
                this.callRunner((ApplicationRunner)runner, args);
            }

            if (runner instanceof CommandLineRunner) {
                this.callRunner((CommandLineRunner)runner, args);
            }
        }

    }

    private void callRunner(ApplicationRunner runner, ApplicationArguments args) {
        try {
            //执行方法
            runner.run(args);
        } catch (Exception var4) {
            throw new IllegalStateException("Failed to execute ApplicationRunner", var4);
        }
    }

    private void callRunner(CommandLineRunner runner, ApplicationArguments args) {
        try {
            //执行方法
            runner.run(args.getSourceArgs());
        } catch (Exception var4) {
            throw new IllegalStateException("Failed to execute CommandLineRunner", var4);
        }
    }
```

>到这里优先级类的示例及其执行原理都分析完毕；不过还是要强调下@Order、Ordered不影响类的加载顺序而是影响Bean加载如IOC容器之后执行的顺序（优先级）；


个人理解是加载代码的底层要支持优先级执行程序，否则即使配置上Ordered、@Order也是不起任何作用的，
个人的力量总是很微小的，欢迎大家来讨论，一起努力成长！！

GitHub源码：[https://github.com/mingyang66/spring-parent/blob/master/spring-common-service/README.md](https://github.com/mingyang66/spring-parent/blob/master/spring-common-service/README.md)

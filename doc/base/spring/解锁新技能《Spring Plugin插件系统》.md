#### 解锁新技能《Spring Plugin插件系统》

平时工作过程中很少使用Spring Plugin插件，最近因为在学习springfox源码的过程中发现有大量用到，先来学习下插件的使用方法。

GitHub地址：[https://github.com/spring-projects/spring-plugin](https://github.com/spring-projects/spring-plugin)

截止20230426日，GitHub的Star为403，fork数为107.

官方说Spring Plugin是世界上最小规模的插件系统，果然名不虚产，用户数够小。

Spring Plugin通过提供扩展核心系统功能的插件实现核心的灵活性，可以满足我们对模块化可扩展程序的需要。

##### 示例程序

Spring Plugin提供了一个标准的Plugin<S>接口供开发人员声明自己的插件的机制，然后通过@EnablePluginRegistries注解注入到Spring IOC容器，Spring容器会为我们自动匹配到插件的所有实现子对象，最终我们的代码在使用时通过注入PluginRegistry<T extends Plugin<S>,S>对象拿到插件实例进行操作。

Plugin<S>接口声明一个实现，标注该插件是否支持，因为可能会有多个接口的实现：

```java
public interface PeoplePlugin extends Plugin<PeoplePluginType> {
    String eat();
}
```

定义一个黄种人插件实现类：

```java
@Service
public class YellowPeoplePluginImpl implements PeoplePlugin {
    @Override
    public String eat() {
        return "馒头、大米";
    }

    @Override
    public boolean supports(PeoplePluginType peopleType) {
        return PeoplePluginType.YELLOW.equals(peopleType);
    }
}

```

定义一个白种人插件实现类：

```java
@Service
public class WhitePeoplePluginImpl implements PeoplePlugin {
    @Override
    public String eat() {
        return "面包";
    }

    @Override
    public boolean supports(PeoplePluginType peopleType) {
        return PeoplePluginType.WHITE.equals(peopleType);
    }
}

```

定义一个控制器类，通过@EnablePluginRegistries注解将PeoplePlugin插件及其实现类注入到IOC容器中：

```java
@EnablePluginRegistries(value = PeoplePlugin.class)
@RestController
@RequestMapping("api/plugin")
public class PluginController {
    @Autowired
    private PluginRegistry<PeoplePlugin, PeoplePluginType> pluginRegistry;

    @GetMapping("eat")
    public void eat() {
        List<PeoplePlugin> list = pluginRegistry.getPlugins();
        for (PeoplePlugin people : list) {
            String s = people.eat();
            System.out.println(s);
        }
    }
}
```



GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
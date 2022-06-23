### 死磕源码系列【springboot之@Import注解多个类引入同一个类源码解析】

> @Import注解的作用是将一个类注入到IOC容器之中，那么我在同一个程序的多个不同配置类中引入同一个类会不会造成冲突呢？带着这样的疑问我做了几个测试及源码分析。

##### 1.新建TestService接口

```java
public interface TestService {
    String getStr(String name);
}
```

##### 2.新建TestService接口实现类TestServiceImpl

```java
public class TestServiceImpl implements TestService {
    @Override
    public String getStr(String name) {
        System.out.println("-----------------"+name);
        return name;
    }
}
```

##### 3.新建配置类Test1Configuration和Test2Configuration

```java
@Configuration(proxyBeanMethods = false)
@Import(TestServiceImpl.class)
public class Test1Configuration {

    @Autowired
    private TestServiceImpl testService;
    @PostConstruct
    public void pstTest(){
        System.out.println(testService.getStr("liming"));
    }
}
```

```java
@Configuration(proxyBeanMethods = false)
@Import(TestServiceImpl.class)
public class Test2Configuration {

    @Autowired
    private TestServiceImpl testService;
    @PostConstruct
    public void pstTest(){
        System.out.println(testService.getStr("xiaollll"));
    }

}
```

运行上述程序发现居然可以成功运行，我们知道容器中的类都是单例模式，那么底层又做了哪些处理呢，猜测肯定是做了去重的机制。

##### 3.源码分析在ConfigurationClassParser#processConfigurationClass方法中找到了原因

```java
	protected void processConfigurationClass(ConfigurationClass configClass, Predicate<String> filter) throws IOException {
		if (this.conditionEvaluator.shouldSkip(configClass.getMetadata(), ConfigurationPhase.PARSE_CONFIGURATION)) {
			return;
		}
		//从配置类集合中获取配置类对象
		ConfigurationClass existingClass = this.configurationClasses.get(configClass);
    //判定配置类是否已经存在
		if (existingClass != null) {
      //如果当前配置类已经存在并且是通过@Import注解引入
			if (configClass.isImported()) {
        //如果已经存在的类也是通过@Import注解引入的，则将配置类中的importedBy属性设置为引入类对象
				if (existingClass.isImported()) {
					existingClass.mergeImportedBy(configClass);
				}
				//配置类已经存在集合中,则忽略当前的引入
				return;
			}
			else {
				// 如果存在显示的配置类，则可以替换掉已经存在的配置类
				// 删除已经存在的配置类
				this.configurationClasses.remove(configClass);
				this.knownSuperclasses.values().removeIf(configClass::equals);
			}
		}

		// Recursively process the configuration class and its superclass hierarchy.
		SourceClass sourceClass = asSourceClass(configClass, filter);
		do {
			sourceClass = doProcessConfigurationClass(configClass, sourceClass, filter);
		}
		while (sourceClass != null);

		this.configurationClasses.put(configClass, configClass);
	}
```

经上述源码分析可以得出以下结论：

- 系统中某一个类全部通过@Import注解引入，可以正常运行，后引入的类将会忽略，但是配置类会在被引入类的importedBy中记录（可以保证IOC容器之中只有一个实例对象）；
- 系统先加载通过@Import注解引入的类，还有显式定义的类（如：@Component），则会覆盖掉通过@Import引入的配置类；
- 系统先加载显式定义的类（如：@Component），还有通过@Import引入此类，则会忽略此类添加到配置集合；

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
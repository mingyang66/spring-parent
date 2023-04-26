### 死磕源码系列【org.springframework.beans.factory.Aware】

> 使用spring及springboot做开发及看源码的时候经常会看到很多Aware类，如EnvironmentAware、ApplicationContextAware等等，那么这些类又是些什么类呢？接下来我们就来分析；

##### 1.首先看下Aware接口源码

```java
/**
 * A marker superinterface indicating that a bean is eligible to be notified by the
 * Spring container of a particular framework object through a callback-style method.
 * The actual method signature is determined by individual subinterfaces but should
 * typically consist of just one void-returning method that accepts a single argument.
 *
 * <p>Note that merely implementing {@link Aware} provides no default functionality.
 * Rather, processing must be done explicitly, for example in a
 * {@link org.springframework.beans.factory.config.BeanPostProcessor}.
 * Refer to {@link org.springframework.context.support.ApplicationContextAwareProcessor}
 * for an example of processing specific {@code *Aware} interface callbacks.
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 */
public interface Aware {

}
```

>
Aware接口其实就是一个空接口，没有任何的方法定义，解释下上面的注解：一个标记父接口，指示了一个bean有资格被spring容器通过一个回调风格的方法调用框架的特定对象。实际的方法签名（即方法）由各个子接口确定的，通常应该仅包含一个接受单个参数、返回值为void的方法。
>
>
请注意，仅仅实现Aware接口不会提供默认功能。相反，处理必须明确的进行，例如在一个org.springframework.beans.factory.config.BeanPostProcessor中，请参考org.springframework.context.support.ApplicationContextAwareProcessor类对部分Aware接口类的处理实例；

##### 2.示例

参考示例文档中的org.springframework.context.support.ApplicationContextAwareProcessor类的实现：

```java
class ApplicationContextAwareProcessor implements BeanPostProcessor {

	private final ConfigurableApplicationContext applicationContext;

	private final StringValueResolver embeddedValueResolver;


	/**
	 * Create a new ApplicationContextAwareProcessor for the given context.
	 */
	public ApplicationContextAwareProcessor(ConfigurableApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		this.embeddedValueResolver = new EmbeddedValueResolver(applicationContext.getBeanFactory());
	}


	@Override
	@Nullable
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if (!(bean instanceof EnvironmentAware || bean instanceof EmbeddedValueResolverAware ||
				bean instanceof ResourceLoaderAware || bean instanceof ApplicationEventPublisherAware ||
				bean instanceof MessageSourceAware || bean instanceof ApplicationContextAware)){
			return bean;
		}

		AccessControlContext acc = null;

		if (System.getSecurityManager() != null) {
			acc = this.applicationContext.getBeanFactory().getAccessControlContext();
		}

		if (acc != null) {
			AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
				invokeAwareInterfaces(bean);
				return null;
			}, acc);
		}
		else {
			invokeAwareInterfaces(bean);
		}

		return bean;
	}

	private void invokeAwareInterfaces(Object bean) {
		if (bean instanceof EnvironmentAware) {
			((EnvironmentAware) bean).setEnvironment(this.applicationContext.getEnvironment());
		}
		if (bean instanceof EmbeddedValueResolverAware) {
			((EmbeddedValueResolverAware) bean).setEmbeddedValueResolver(this.embeddedValueResolver);
		}
		if (bean instanceof ResourceLoaderAware) {
			((ResourceLoaderAware) bean).setResourceLoader(this.applicationContext);
		}
		if (bean instanceof ApplicationEventPublisherAware) {
			((ApplicationEventPublisherAware) bean).setApplicationEventPublisher(this.applicationContext);
		}
		if (bean instanceof MessageSourceAware) {
			((MessageSourceAware) bean).setMessageSource(this.applicationContext);
		}
		if (bean instanceof ApplicationContextAware) {
			((ApplicationContextAware) bean).setApplicationContext(this.applicationContext);
		}
	}

}
```

>
BeanPostProcessor接口为IOC容器类提供了钩子方法，在Bean实例化之前和实例化之后分别调用before和after方法调用，具体使用方法请参考：https://blog.csdn.net/yaomingyang/article/details/108628357，当回调方法postProcessBeforeInitialization被调用时，会调用invokeAwareInterfaces方法，那么就会调用Aware接口中的方法，可能会传入一个参数，就是上述文档中提到的框架对象，如：applicationContext。

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

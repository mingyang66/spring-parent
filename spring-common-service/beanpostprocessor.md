### Spring之BeanPostProcessor、InitializingBean、init-method解析

TIPS:Spring在初始化完Bean之后其实已经给我们提供了一些钩子方法，方便去做一些后置处理业务，所谓的后置处理就是这篇文章要讲的BeanPostProcessor，
BeanPostProcessor提供了两个回调方法，你可以在这两个方法中实现一些定制化的业务逻辑；

#### 1.BeanPostProcessor源码解析
```
public interface BeanPostProcessor {

	/**
	 * 这个方法会在InitializingBean的方法afterPropertiesSet以及自定义了 init-method方法执行之前被回调，返回值可能是个原始对象的包装类
	 */
	@Nullable
	default Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/**
	 * 这个方法会在InitializingBean的方法afterPropertiesSet以及自定义了 init-method方法执行之后被回调，返回值可能是个原始对象的包装类
	 */
	@Nullable
	default Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
```
上面的两个方法都是在Bean初始化完成后调用的，是Spring提供的钩子方法，看方法名好像是在Bean初始化之前和初始化之后调用，但是实际上不是这样，而是
在Bean初始化完成后提供的两个后置处理方法，那这两个钩子方法在哪里被调用呢？看下面的示例。

#### 2.看下AbstractAutowireCapableBeanFactory类中initializeBean方法，当然不止这一个类会调用后置处理器
```
protected Object initializeBean(final String beanName, final Object bean, @Nullable RootBeanDefinition mbd) {
		if (System.getSecurityManager() != null) {
			AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
				invokeAwareMethods(beanName, bean);
				return null;
			}, getAccessControlContext());
		}
		else {
		    //若果Bean实现了BeanNameAware、BeanClassLoaderAware、BeanFactoryAware则初始化Bean的属性值
			invokeAwareMethods(beanName, bean);
		}

		Object wrappedBean = bean;
		if (mbd == null || !mbd.isSynthetic()) {
		    //applyBeanPostProcessorsBeforeInitialization方法是AutowireCapableBeanFactory接口中的方法，用来调用后置处理器BeanPostProcessor的postProcessBeforeInitialization方法
			wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
		}

		try {
		    //Bean如果继承了InitializingBean类则会调用afterPropertiesSet方法，如果设置了init-method方法，则调用init-method方法，afterPropertiesSet方法在init-method方法之前调用
			invokeInitMethods(beanName, wrappedBean, mbd);
		}
		catch (Throwable ex) {
			throw new BeanCreationException(
					(mbd != null ? mbd.getResourceDescription() : null),
					beanName, "Invocation of init method failed", ex);
		}
		if (mbd == null || !mbd.isSynthetic()) {
		//applyBeanPostProcessorsAfterInitialization方法是AutowireCapableBeanFactory接口中的方法，用来调用后置处理器BeanPostProcessor的postProcessAfterInitialization方法
			wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
		}

		return wrappedBean;
	}
```
调用Bean的applyBeanPostProcessorsBeforeInitialization后置处理方法
```
	@Override
	public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
			throws BeansException {

		Object result = existingBean;
		for (BeanPostProcessor processor : getBeanPostProcessors()) {
			Object current = processor.postProcessBeforeInitialization(result, beanName);
			if (current == null) {
				return result;
			}
			result = current;
		}
		return result;
	}
```
调用Bean的postProcessAfterInitialization后置处理方法
```
	@Override
	public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
			throws BeansException {

		Object result = existingBean;
		for (BeanPostProcessor processor : getBeanPostProcessors()) {
			Object current = processor.postProcessAfterInitialization(result, beanName);
			if (current == null) {
				return result;
			}
			result = current;
		}
		return result;
	}
```
初始化Bean的Aware属性值
```
	private void invokeAwareMethods(final String beanName, final Object bean) {
		if (bean instanceof Aware) {
			if (bean instanceof BeanNameAware) {
				((BeanNameAware) bean).setBeanName(beanName);
			}
			if (bean instanceof BeanClassLoaderAware) {
				ClassLoader bcl = getBeanClassLoader();
				if (bcl != null) {
					((BeanClassLoaderAware) bean).setBeanClassLoader(bcl);
				}
			}
			if (bean instanceof BeanFactoryAware) {
				((BeanFactoryAware) bean).setBeanFactory(AbstractAutowireCapableBeanFactory.this);
			}
		}
	}
```

到这里后置处理器BeanPostProcessor、InitializingBean、init-method调用逻辑已经分析完毕，有不理解或者文章有误的可以提出来一起努力成长！

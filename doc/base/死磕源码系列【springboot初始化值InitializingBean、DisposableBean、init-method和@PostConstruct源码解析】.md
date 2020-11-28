### 死磕源码系列【springboot初始化值InitializingBean、DisposableBean、init-method和@PostConstruct源码解析】

Spring容器中的Bean是有生命周期的，spring允许在bean的初始化完成之后以及Bean的销毁执行之前执行特定的操作，常用的设定方式有以下三种：

- 通过实现InitializingBean和DisposableBean接口来定制初始化之后及销毁之前的操作方法；
- 通过在@Bean注解或<Bean>元素上的initMethod、destroyMethod属性指定初始化之后及销毁之前调用的操作方法；
- 在指定的方法上加@PostConstruct、@PreDestroy注解来指定该方法在初始化之前及初始化之后调用

##### 当一个Bean初始化完成之后会回调AbstractAutowireCapableBeanFactory#initializeBean方法，在此方法中就会对这些初始化方法进行调用：

```java
	protected Object initializeBean(String beanName, Object bean, @Nullable RootBeanDefinition mbd) {
    //对Aware相关接口实现方法调用，如：BeanNameAware、ApplicationContextAware
		if (System.getSecurityManager() != null) {
			AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
				invokeAwareMethods(beanName, bean);
				return null;
			}, getAccessControlContext());
		}
		else {
			invokeAwareMethods(beanName, bean);
		}

		Object wrappedBean = bean;
		if (mbd == null || !mbd.isSynthetic()) {
      //对BeaPostProcessors后置处理器的before方法进行调用，此处会调用InitDestroyAnnotationBeanPostProcessor后置处理器对@PostConstruct标注的方法进行调用
			wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
		}

		try {
      //如果类实现了InitializingBean接口则对afterPropertiesSet方法进行调用
      //如果bean配置了initMethod属性，则对此方法进行调用
			invokeInitMethods(beanName, wrappedBean, mbd);
		}
		catch (Throwable ex) {
			throw new BeanCreationException(
					(mbd != null ? mbd.getResourceDescription() : null),
					beanName, "Invocation of init method failed", ex);
		}
		if (mbd == null || !mbd.isSynthetic()) {
			wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
		}

		return wrappedBean;
	}
```

##### invokeInitMethods方法调用初始化方法：

```java
	protected void invokeInitMethods(String beanName, Object bean, @Nullable RootBeanDefinition mbd)
			throws Throwable {
		//判定当前bean是否实现了InitializingBean接口
		boolean isInitializingBean = (bean instanceof InitializingBean);
		if (isInitializingBean && (mbd == null || !mbd.isExternallyManagedInitMethod("afterPropertiesSet"))) {
			if (logger.isTraceEnabled()) {
				logger.trace("Invoking afterPropertiesSet() on bean with name '" + beanName + "'");
			}
			if (System.getSecurityManager() != null) {
				try {
					AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () -> {
            //调用bean的afterPropertiesSet方法
						((InitializingBean) bean).afterPropertiesSet();
						return null;
					}, getAccessControlContext());
				}
				catch (PrivilegedActionException pae) {
					throw pae.getException();
				}
			}
			else {
        //调用bean的afterPropertiesSet方法
				((InitializingBean) bean).afterPropertiesSet();
			}
		}
		//如果Bean配置了initMethod方法则调用对应的初始化方法
		if (mbd != null && bean.getClass() != NullBean.class) {
			String initMethodName = mbd.getInitMethodName();
			if (StringUtils.hasLength(initMethodName) &&
					!(isInitializingBean && "afterPropertiesSet".equals(initMethodName)) &&
					!mbd.isExternallyManagedInitMethod(initMethodName)) {
				invokeCustomInitMethod(beanName, bean, mbd);
			}
		}
	}
```

##### invokeCustomInitMethod方法调用自定义初始化方法，通过反射的方式调用初始化方法：

```java
	protected void invokeCustomInitMethod(String beanName, Object bean, RootBeanDefinition mbd)
			throws Throwable {
		//获取初始化方法名
		String initMethodName = mbd.getInitMethodName();
		Assert.state(initMethodName != null, "No init method set");
    //获取初始化方法Method对象
		Method initMethod = (mbd.isNonPublicAccessAllowed() ?
				BeanUtils.findMethod(bean.getClass(), initMethodName) :
				ClassUtils.getMethodIfAvailable(bean.getClass(), initMethodName));

		if (initMethod == null) {
			if (mbd.isEnforceInitMethod()) {
				throw new BeanDefinitionValidationException("Could not find an init method named '" +
						initMethodName + "' on bean with name '" + beanName + "'");
			}
			else {
				if (logger.isTraceEnabled()) {
					logger.trace("No default init method named '" + initMethodName +
							"' found on bean with name '" + beanName + "'");
				}
				// Ignore non-existent default lifecycle methods.
				return;
			}
		}

		if (logger.isTraceEnabled()) {
			logger.trace("Invoking init method  '" + initMethodName + "' on bean with name '" + beanName + "'");
		}
		Method methodToInvoke = ClassUtils.getInterfaceMethodIfPossible(initMethod);

		if (System.getSecurityManager() != null) {
			AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
				ReflectionUtils.makeAccessible(methodToInvoke);
				return null;
			});
			try {
				AccessController.doPrivileged((PrivilegedExceptionAction<Object>)
						() -> methodToInvoke.invoke(bean), getAccessControlContext());
			}
			catch (PrivilegedActionException pae) {
				InvocationTargetException ex = (InvocationTargetException) pae.getException();
				throw ex.getTargetException();
			}
		}
		else {
			try {
				ReflectionUtils.makeAccessible(methodToInvoke);
        //通过反射的方式调用初始化方法
				methodToInvoke.invoke(bean);
			}
			catch (InvocationTargetException ex) {
				throw ex.getTargetException();
			}
		}
	}
```

##### 总结：spring bean的初始化执行顺序是：构造函数—>@PostConstruct注解方法—>InitializingBean接口实现方法—>initMethod指定的方法；

GitHub地址:[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
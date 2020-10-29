### 死磕源码系列【ConfigurationWarningsApplicationContextInitializer源码解析】

> ConfigurationWarningsApplicationContextInitializer是ApplicationContextInitializer初始化器接口的实现类，用作报告常见错误配置的警告（通过分析源码实际情况是如果系统配置包扫描（@ComponentScan）到了org或者org.springframework包就会发出警告并停止系统启动）

##### ConfigurationWarningsApplicationContextInitializer初始化器源码：

```java
public class ConfigurationWarningsApplicationContextInitializer
		implements ApplicationContextInitializer<ConfigurableApplicationContext> {

	private static final Log logger = LogFactory.getLog(ConfigurationWarningsApplicationContextInitializer.class);
	//初始化方法会在容器启动时调用，并将ConfigurationWarningsPostProcessor后置处理器注入到应用上下文中
	@Override
	public void initialize(ConfigurableApplicationContext context) {
		context.addBeanFactoryPostProcessor(new ConfigurationWarningsPostProcessor(getChecks()));
	}

	/**
	 * 返回有问题的扫描包（@ComponentScan）Check对象
	 * @return the checks to apply
	 */
	protected Check[] getChecks() {
		return new Check[] { new ComponentScanPackageCheck() };
	}
	}
```

##### ComponentScanPackageCheck是ConfigurationWarningsApplicationContextInitializer的一个内部类，源码分析

```java
/**
	 * 可以应用的单一检查
	 */
	@FunctionalInterface
	protected interface Check {

		/**
		 * 返回检查结果，如果检查失败，则返回警告，如果没有问题，则返回null
		 * @param registry the {@link BeanDefinitionRegistry}
		 * @return a warning message or {@code null}
		 */
		String getWarning(BeanDefinitionRegistry registry);

	}

	/**
	 * 检查@ComponentScan注解扫描有问题的包
	 */
	protected static class ComponentScanPackageCheck implements Check {

		private static final Set<String> PROBLEM_PACKAGES;
		//定义扫描有问题的包
		static {
			Set<String> packages = new HashSet<>();
			packages.add("org.springframework");
			packages.add("org");
			PROBLEM_PACKAGES = Collections.unmodifiableSet(packages);
		}
		//检查@ComponentScan注解扫描的包是否有问题，如果有，则返回警告，否则返回null
		@Override
		public String getWarning(BeanDefinitionRegistry registry) {
      //获取@ComponentScan注解扫描的包集合
			Set<String> scannedPackages = getComponentScanningPackages(registry);
      //获取有问题的扫描包集合
			List<String> problematicPackages = getProblematicPackages(scannedPackages);
			if (problematicPackages.isEmpty()) {
				return null;
			}
			return "Your ApplicationContext is unlikely to start due to a @ComponentScan of "
					+ StringUtils.collectionToDelimitedString(problematicPackages, ", ") + ".";
		}
		//获取@ComponentScan注解扫描的包
		protected Set<String> getComponentScanningPackages(BeanDefinitionRegistry registry) {
			Set<String> packages = new LinkedHashSet<>();
      //获取容器中所有bean定义名称
			String[] names = registry.getBeanDefinitionNames();
			for (String name : names) {
        //获取name对应的bean定义对象
				BeanDefinition definition = registry.getBeanDefinition(name);
				if (definition instanceof AnnotatedBeanDefinition) {
					AnnotatedBeanDefinition annotatedDefinition = (AnnotatedBeanDefinition) definition;
					addComponentScanningPackages(packages, annotatedDefinition.getMetadata());
				}
			}
			return packages;
		}
		//将bean实例上注解@ComponentScan扫描包
		private void addComponentScanningPackages(Set<String> packages, AnnotationMetadata metadata) {
			AnnotationAttributes attributes = AnnotationAttributes
					.fromMap(metadata.getAnnotationAttributes(ComponentScan.class.getName(), true));
			if (attributes != null) {
				addPackages(packages, attributes.getStringArray("value"));
				addPackages(packages, attributes.getStringArray("basePackages"));
				addClasses(packages, attributes.getStringArray("basePackageClasses"));
				if (packages.isEmpty()) {
					packages.add(ClassUtils.getPackageName(metadata.getClassName()));
				}
			}
		}

		private void addPackages(Set<String> packages, String[] values) {
			if (values != null) {
				Collections.addAll(packages, values);
			}
		}

		private void addClasses(Set<String> packages, String[] values) {
			if (values != null) {
				for (String value : values) {
					packages.add(ClassUtils.getPackageName(value));
				}
			}
		}
		//获取有问题的扫描包集合，即包名是：org或org.springframework
		private List<String> getProblematicPackages(Set<String> scannedPackages) {
			List<String> problematicPackages = new ArrayList<>();
			for (String scannedPackage : scannedPackages) {
        //判定包名是否有问题，即包名是：org或org.springframework
				if (isProblematicPackage(scannedPackage)) {
					problematicPackages.add(getDisplayName(scannedPackage));
				}
			}
			return problematicPackages;
		}
		//判定包名是否有问题，即包名是：org或org.springframework
		private boolean isProblematicPackage(String scannedPackage) {
			if (scannedPackage == null || scannedPackage.isEmpty()) {
				return true;
			}
			return PROBLEM_PACKAGES.contains(scannedPackage);
		}

		private String getDisplayName(String scannedPackage) {
			if (scannedPackage == null || scannedPackage.isEmpty()) {
				return "the default package";
			}
			return "'" + scannedPackage + "'";
		}

	}

```

------

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)


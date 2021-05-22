### springboot之AbstractRoutingDataSource抽象类实现动态多数据源切换原理解析

之前写过两篇实现动态多数据源的文章：

第一种：半自动化模式[https://blog.csdn.net/yaomingyang/article/details/100807675](https://blog.csdn.net/yaomingyang/article/details/100807675)

第二种：完全自动化配置模式：[https://blog.csdn.net/yaomingyang/article/details/117089979](https://blog.csdn.net/yaomingyang/article/details/117089979)

但是一直未对其实现的核心抽象类AbstractRoutingDataSource进行讲解，今天我们就来看下这个抽象类的源码实现原理；先看下部分源码：

```java
/**
 * Abstract {@link javax.sql.DataSource} implementation that routes {@link #getConnection()}
 * calls to one of various target DataSources based on a lookup key. The latter is usually
 * (but not necessarily) determined through some thread-bound transaction context.
 *
 * @author Juergen Hoeller
 * @since 2.0.1
 * @see #setTargetDataSources 设置目标数据源的方法
 * @see #setDefaultTargetDataSource 设置默认数据源的方法
 * @see #determineCurrentLookupKey() 通过此方法获取当前线程需要绑定数据源
 */
public abstract class AbstractRoutingDataSource extends AbstractDataSource implements InitializingBean {
	// 目标多数据源集合
	@Nullable
	private Map<Object, Object> targetDataSources;
	// 默认数据源对象
	@Nullable
	private Object defaultTargetDataSource;
	// 通过JNDI查找数据源，如果数据源不存在是否回滚到默认数据源，默认：true
	private boolean lenientFallback = true;
	// 通过JNDI查找多数据源对象默认实现类
	private DataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
	// targetDataSources 数据源集合的解析后的key-value对象
	@Nullable
	private Map<Object, DataSource> resolvedDataSources;
	// 解析后的默认数据源对象
	@Nullable
	private DataSource resolvedDefaultDataSource;
```

AbstractRoutingDataSource类实现多数据源切换的核心逻辑是，在程序运行时通过AOP切面动态切换当前线程绑定的数据源对象，即数据库事物上下文来实现的；

- defaultTargetDataSource属性指定默认的数据源，无任何切换操作时使用默认数据库及事物上下文
- targetDataSources是指所有数据库配置集合，可以通过key来切换所使用的的数据库
- lenientFallback属性设置当切换的数据库不存在时是否回退到默认数据库，默认是：true
- dataSourceLookup属性是动态切换数据源是通过JNDI寻找数据源的默认实现

三个核心方法：

- setTargetDataSources方法设置整个项目配置的所有数据库，key是动态切换的唯一标识，value是数据源配置对象
- setDefaultTargetDataSource默认数据源对象
- determineCurrentLookupKey()方法获取要使用数据源的key

##### determineTargetDataSource方法用来获取当前线程对应的数据源

```java
	protected DataSource determineTargetDataSource() {
		Assert.notNull(this.resolvedDataSources, "DataSource router not initialized");
    // 获取当前线程对应数据源的标识key
		Object lookupKey = determineCurrentLookupKey();
    // 从数据源集合中获取数据源对象
		DataSource dataSource = this.resolvedDataSources.get(lookupKey);
    // 如果lenientFallback回退属性为true
		if (dataSource == null && (this.lenientFallback || lookupKey == null)) {
      // 如果数据源不存在，则回退到默认数据源
			dataSource = this.resolvedDefaultDataSource;
		}
    // 如果数据源不存在，则抛出异常
		if (dataSource == null) {
			throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + lookupKey + "]");
		}
		return dataSource;
	}
```

##### afterPropertiesSet方法

```java
	@Override
	public void afterPropertiesSet() {
		if (this.targetDataSources == null) {
			throw new IllegalArgumentException("Property 'targetDataSources' is required");
		}
		this.resolvedDataSources = CollectionUtils.newHashMap(this.targetDataSources.size());
		this.targetDataSources.forEach((key, value) -> {
			Object lookupKey = resolveSpecifiedLookupKey(key);
			DataSource dataSource = resolveSpecifiedDataSource(value);
			this.resolvedDataSources.put(lookupKey, dataSource);
		});
		if (this.defaultTargetDataSource != null) {
			this.resolvedDefaultDataSource = resolveSpecifiedDataSource(this.defaultTargetDataSource);
		}
	}
```

上述方法是动态切换数据源的一个核心，会将targetDataSources、defaultTargetDataSource两个数据源属性对象解析为DataSource对象；其核心方法是resolveSpecifiedDataSource，源码如下：

```java
	protected DataSource resolveSpecifiedDataSource(Object dataSource) throws IllegalArgumentException {
    // 如果数据源对象是DataSource的实例对象，直接返回
		if (dataSource instanceof DataSource) {
			return (DataSource) dataSource;
		}
    // 如果是字符串对象，则视其为dataSourceName，则调用JndiDataSourceLookup的getDataSource方法
		else if (dataSource instanceof String) {
			return this.dataSourceLookup.getDataSource((String) dataSource);
		}
		else {
			throw new IllegalArgumentException(
					"Illegal data source value - only [javax.sql.DataSource] and String supported: " + dataSource);
		}
	}
```

上述核心是dataSourceLookup，即：JndiDataSourceLookup类，通过数据源名查找数据源对象，其是DataSourceLookup接口的实现类：

```java
@FunctionalInterface
public interface DataSourceLookup {

	/**
	 * Retrieve the DataSource identified by the given name.
	 * @param dataSourceName the name of the DataSource
	 * @return the DataSource (never {@code null})
	 * @throws DataSourceLookupFailureException if the lookup failed
	 */
	DataSource getDataSource(String dataSourceName) throws DataSourceLookupFailureException;

}
```

DataSourceLookup是一个函数式接口，提供了一个通过数据源名称查找DataSource对象的接口；其实现类则是JndiDataSourceLookup来实现通过数据源名查找数据源对象，JNDI不是本文重点，接下来我会单独写一篇文章来讲解JNDI；

GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)


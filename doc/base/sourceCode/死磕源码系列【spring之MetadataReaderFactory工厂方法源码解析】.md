死磕源码系列【spring之MetadataReaderFactory工厂方法源码解析】

##### MetadataReader是用于访问类的元数据的门面模式读取器类，其实通过MetadataReaderFactory工厂方法获取，允许为每个原始资源缓存元数据读取器；

```java
public interface MetadataReaderFactory {

	/**
	 * 根据类名获取MetadataReader对象
	 */
	MetadataReader getMetadataReader(String className) throws IOException;

	/**
	 * 通过给定的Resource对象获取MetadataReader对象
	 */
	MetadataReader getMetadataReader(Resource resource) throws IOException;

}

```

##### SimpleMetadataReaderFactory类是MetadataReaderFactory接口的简单实现，为每个请求创建一个ASM ClassReader:

```java
public class SimpleMetadataReaderFactory implements MetadataReaderFactory {
	//资源加载器对象
	private final ResourceLoader resourceLoader;


	/**
	 * 为默认的类加载器创建一个SimpleMetadataReaderFactory对象
	 */
	public SimpleMetadataReaderFactory() {
		this.resourceLoader = new DefaultResourceLoader();
	}

	/**
	 * 根据指定的资源加载器创建SimpleMetadataReaderFactory对象
	 */
	public SimpleMetadataReaderFactory(@Nullable ResourceLoader resourceLoader) {
		this.resourceLoader = (resourceLoader != null ? resourceLoader : new DefaultResourceLoader());
	}

	/**
	 * 根据给定的类加载器创建SimpleMetadataReaderFactory对象
	 */
	public SimpleMetadataReaderFactory(@Nullable ClassLoader classLoader) {
		this.resourceLoader =
				(classLoader != null ? new DefaultResourceLoader(classLoader) : new DefaultResourceLoader());
	}


	/**
	 * 获取当前MetadataReaderFactory对象的资源加载器
	 */
	public final ResourceLoader getResourceLoader() {
		return this.resourceLoader;
	}

	//根据类名获取对应的元数据读取器对象
	@Override
	public MetadataReader getMetadataReader(String className) throws IOException {
		try {
      //获取类资源的路径
			String resourcePath = ResourceLoader.CLASSPATH_URL_PREFIX +
					ClassUtils.convertClassNameToResourcePath(className) + ClassUtils.CLASS_FILE_SUFFIX;
			//获取指定的资源对象
      Resource resource = this.resourceLoader.getResource(resourcePath);
      //根据指定的资源对象获取元数据阅读器
			return getMetadataReader(resource);
		}
		catch (FileNotFoundException ex) {
			// 内部类解析
			// ClassUtils.forName has an equivalent check for resolution into Class references later on.
			int lastDotIndex = className.lastIndexOf('.');
			if (lastDotIndex != -1) {
				String innerClassName =
						className.substring(0, lastDotIndex) + '$' + className.substring(lastDotIndex + 1);
				String innerClassResourcePath = ResourceLoader.CLASSPATH_URL_PREFIX +
						ClassUtils.convertClassNameToResourcePath(innerClassName) + ClassUtils.CLASS_FILE_SUFFIX;
				Resource innerClassResource = this.resourceLoader.getResource(innerClassResourcePath);
				if (innerClassResource.exists()) {
					return getMetadataReader(innerClassResource);
				}
			}
			throw ex;
		}
	}
	//根据资源对象获取元数据阅读器
	@Override
	public MetadataReader getMetadataReader(Resource resource) throws IOException {
		return new SimpleMetadataReader(resource, this.resourceLoader.getClassLoader());
	}

}
```

##### ConcurrentReferenceCachingMetadataReaderFactory是SimpleMetadataReaderFactory的子类，用于缓存获取到的元数据阅读器到字典当中

```java
public class ConcurrentReferenceCachingMetadataReaderFactory extends SimpleMetadataReaderFactory {
	//字典，用于存储字典及元数据阅读器
	private final Map<Resource, MetadataReader> cache = new ConcurrentReferenceHashMap<>();

	/**
	 * 默认实例构造方法
	 */
	public ConcurrentReferenceCachingMetadataReaderFactory() {
	}

	/**
	 * 根据指定的资源加载器创建实例对象
	 */
	public ConcurrentReferenceCachingMetadataReaderFactory(ResourceLoader resourceLoader) {
		super(resourceLoader);
	}

	/**
	 * 根据给定的类加载器创建实例对象
	 */
	public ConcurrentReferenceCachingMetadataReaderFactory(ClassLoader classLoader) {
		super(classLoader);
	}

	@Override
	public MetadataReader getMetadataReader(Resource resource) throws IOException {
    //获取字典中的元数据阅读器
		MetadataReader metadataReader = this.cache.get(resource);
    //如果不存在
		if (metadataReader == null) {
      //创建一个新的元数据阅读器
			metadataReader = createMetadataReader(resource);
      //将元数据阅读器放入字典中
			this.cache.put(resource, metadataReader);
		}
		return metadataReader;
	}

	/**
	 * 创建元数据阅读器
	 */
	protected MetadataReader createMetadataReader(Resource resource) throws IOException {
		return super.getMetadataReader(resource);
	}

	/**
	 * 清空字典中的所有数据
	 */
	public void clearCache() {
		this.cache.clear();
	}

}

```

GitHub地址：[https://github.com/mingyang66/spring-parent]（https://github.com/mingyang66/spring-parent）


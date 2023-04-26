### 死磕源码系列【PropertySourceLoader加载properties、yml配置文件源码详解】

>
PropertySourceLoader接口实现类用来在项目启动的时候加载properties、xml、yml配置文件，其实现类一共有两个PropertiesPropertySourceLoader、YamlPropertySourceLoader，分别用来加载
properties文件及yml配置文件；

PropertiesPropertySourceLoader、YamlPropertySourceLoader两个实现类是通过springboot
SPI机制在ConfigFileApplicationListener监听器类中被加载，并通过不同的逻辑加载classpath环境变量及其它配置方式制定地址下的配置文件（本文重点讲解PropertiesPropertySourceLoader实现类的源码分析，YamlPropertySourceLoader加载逻辑类似不在重复讲解），看下spring.factories配置：

```java
# PropertySource Loaders
org.springframework.boot.env.PropertySourceLoader=\
org.springframework.boot.env.PropertiesPropertySourceLoader,\
org.springframework.boot.env.YamlPropertySourceLoader
```

##### PropertySourceLoader属性源加载接口源码

```java
public interface PropertySourceLoader {

	/**
	 * 返回加载程序支持的文件扩展名（不包括"."）
	 * @return the file extensions
	 */
	String[] getFileExtensions();

	/**
	 * 将资源加载到一个或者多个属性源中（PropertySource）. 实现可以返回一个包含单个属性源的列表，或者在多文档
	 * 格式（如：yaml）的情况下，为资源中的每个文档返回一个源。
	 * @param name 属性源的根名称,如：applicationConfig: [classpath:/application-redis.properties]
	 * @param 要被加载的资源
	 * @return a list property sources
	 * @throws IOException if the source cannot be loaded
	 */
	List<PropertySource<?>> load(String name, Resource resource) throws IOException;

}
```

##### 先看下PropertiesPropertySourceLoader加载properties配置文件的实现类源码(将".properties"文件加载到PropertySource中的策略实现类)

```java
public class PropertiesPropertySourceLoader implements PropertySourceLoader {

	private static final String XML_FILE_EXTENSION = ".xml";
	//加载程序支持的配置文件格式，默认支持：properties、xml两种；xml配置方式没有用过，本文重点讲解properties配  	 //置文件
	@Override
	public String[] getFileExtensions() {
		return new String[] { "properties", "xml" };
	}
	/**
	* 加载属性源到PropertySource实现方法
	* name:配置文件
	* resourse:配置文件资源类ClassPathSource
	**/
	@Override
	public List<PropertySource<?>> load(String name, Resource resource) throws IOException {
		//加载properties配置文件（或者xml配置文件）配置属性到字典中
    Map<String, ?> properties = loadProperties(resource);
		if (properties.isEmpty()) {
			return Collections.emptyList();
		}
    //将配置文件字典类型转换为存储一个PropertySource属性源配置的List列表
		return Collections
				.singletonList(new OriginTrackedMapPropertySource(name, Collections.unmodifiableMap(properties), true));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, ?> loadProperties(Resource resource) throws IOException {
    //获取文件名，包括后缀，如：application-config.properties
		String filename = resource.getFilename();
    //判定是否是xml文件，如果是就解析xml配置
		if (filename != null && filename.endsWith(XML_FILE_EXTENSION)) {
			return (Map) PropertiesLoaderUtils.loadProperties(resource);
		}
    //解析properties配置文件，并返回字典类型
		return new OriginTrackedPropertiesLoader(resource).load();
	}

}

```

> 上述代码每次解析一个properties配置文件为字典Map类型；里面有几个类需要详细的说一下Resource、OriginTrackedMapPropertySource、OriginTrackedPropertiesLoader

##### 按照顺序先看下Resource,上述加载程序实际使用的是ClassPathResource类

Resource接口是InputStreamSource接口的子接口，InputStreamSource接口提供了一个getInputStream方法返回资源的输入流，可以用于读取指定的资源文件；Resource接口继承了InputStreamSource接口的能力并提供了底层资源的实际类型（如：文件或类路径资源）抽象的资源描述符的能力；ClassPathResource是类路径资源Resource接口的实现，使用给定的ClassLoader或给定的Class来加载资源，如果类路劲资源驻留在文件系统中，而不是JAR中的资源，支持解析为File文件，并且支持解析为URL资源。

##### 接下来是OriginTrackedMapPropertySource原始追踪属性源实现类，先看下类关系图

![在这里插入图片描述](https://img-blog.csdnimg.cn/20201022145555685.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3lhb21pbmd5YW5n,size_16,color_FFFFFF,t_70#pic_center)

OriginTrackedMapPropertySource实现了多个接口和类，先看下PropertySoruce抽象类；PropertySoruce类提供了两个属性name和source,name用来存储资源名（如：applicationConfig: [classpath:/application-redis.properties]
），source用来存储属性源的配置信息(如：java.util.Properties、java.util.Map对象)
；EnumerablePropertySource抽象类是PropertySource抽象类的一个实现，提供了一个getPropertyNames抽象方法枚举配置文件中的key名称，并且提供了containsProperty方法实现通过调用getPropertyNames枚举方法判定是否包含指定的配置；MapPropertySource类是EnumerablePropertySource抽象类的子类，提供了一个带有两个参数的构造函数（String类型的name，及Map类型的source，并且source不可以为null），对getPropertyNames方法枚举配置字典key方法实现；OriginTrackedMapPropertySource类继承了MapPropertySource类，新增了一个immutable属性（设定资源属性是否可变），提供了带有三个个参数name、Map类型的source及immutable的构造函数。

##### 最后是OriginTrackedPropertiesLoader类加载.properties文件到 Map<String, OriginTrackedValue>集合，并且支持扩展样式，如：name[]=a,b,c 源码分析如下

首先提供了一个带有一个参数Resource构造函数：

```java
	private final Resource resource;

	/**
	 * 用来创建OriginTrackedPropertiesLoader实例
	 * @param .properties配置文件的资源
	 */
	OriginTrackedPropertiesLoader(Resource resource) {
		Assert.notNull(resource, "Resource must not be null");
		this.resource = resource;
	}
```

load方法用来加载指定的properties配置文件：

```java
	/**
	 * 加载一个.properties配置文件，并返回一个Map {@code String} -> {@link OriginTrackedValue}.
	 * @return 要被加载的properties配置文件
	 * @throws IOException on read error
	 */
	Map<String, OriginTrackedValue> load() throws IOException {
		return load(true);
	}

	/**
	 * 加载一个.properties配置文件，并返回一个Map {@code String} ->{@link OriginTrackedValue}.
	 * @param expandLists 如果参数列表{@code name[]=a,b,c}应打开快捷方式
	 * @return the loaded properties
	 * @throws IOException on read error
	 */
	Map<String, OriginTrackedValue> load(boolean expandLists) throws IOException {
    //读取配置文件资源
		try (CharacterReader reader = new CharacterReader(this.resource)) {
			Map<String, OriginTrackedValue> result = new LinkedHashMap<>();
			StringBuilder buffer = new StringBuilder();
			while (reader.read()) {
        //获取配置文件属性key值
				String key = loadKey(buffer, reader).trim();
        //如果属性key是数组类型
				if (expandLists && key.endsWith("[]")) {
					key = key.substring(0, key.length() - 2);
					int index = 0;
					do {
            //获取属性配置文件value值
						OriginTrackedValue value = loadValue(buffer, reader, true);
            //将解析后的key-value存入Map集合
						put(result, key + "[" + (index++) + "]", value);
						if (!reader.isEndOfLine()) {
							reader.read();
						}
					}
					while (!reader.isEndOfLine());
				}
				else {
          //获取属性配置文件value值
					OriginTrackedValue value = loadValue(buffer, reader, false);
          //将解析后的key-value存入Map集合
					put(result, key, value);
				}
			}
			return result;
		}
	}
```

put方法如下(将指定的key-value加入到指定的map字典中)：

```java
    private void put(Map<String, OriginTrackedValue> result, String key, OriginTrackedValue value) {
        if (!key.isEmpty()) {
            result.put(key, value);
        }
    }
```

loadKey读取配置文件中的key:

```java
    private String loadKey(StringBuilder buffer, OriginTrackedPropertiesLoader.CharacterReader reader) throws IOException {
        buffer.setLength(0);
        boolean previousWhitespace = false;
        while (!reader.isEndOfLine()) {
            if (reader.isPropertyDelimiter()) {
                reader.read();
                return buffer.toString();
            }
            if (!reader.isWhiteSpace() && previousWhitespace) {
                return buffer.toString();
            }
            previousWhitespace = reader.isWhiteSpace();
            buffer.append(reader.getCharacter());
            reader.read();
        }
        return buffer.toString();
    }
```

loadValue加载配置文件中的value值：

```java
	private OriginTrackedValue loadValue(StringBuilder buffer, CharacterReader reader, boolean splitLists)
			throws IOException {
		buffer.setLength(0);
		while (reader.isWhiteSpace() && !reader.isEndOfLine()) {
			reader.read();
		}
    //Location类有两个属性line和column
    //资源在配置文件中的位置（行号和列号）
		Location location = reader.getLocation();
		while (!reader.isEndOfLine() && !(splitLists && reader.isListDelimiter())) {
			buffer.append(reader.getCharacter());
			reader.read();
		}
    //TextResourceOrigin类是Origin接口的实现类，拥有两个属性Resource和Location
		Origin origin = new TextResourceOrigin(this.resource, location);
    //存储指定对象值及Origin对象
		return OriginTrackedValue.of(buffer.toString(), origin);
	}
```

上述代码返回值是OriginTrackedValue类，拥有两个属性Object类型的value及Origin类型的origin，看下源码：

```java
public class OriginTrackedValue implements OriginProvider {

	private final Object value;

	private final Origin origin;

	private OriginTrackedValue(Object value, Origin origin) {
		this.value = value;
		this.origin = origin;
	}
	}
```

至此PropertiesPropertySourceLoader加载类已经分析完毕，拓展看下CharacterReader类的构造函数：

```java
		CharacterReader(Resource resource) throws IOException {
			this.reader = new LineNumberReader(
					new InputStreamReader(resource.getInputStream(), StandardCharsets.ISO_8859_1));
		}
```

可以清晰的看到读取properties配置文件的编码方式是ISO_8859_1,这样会导致配置文件中的中文乱码，我的解决方案是重写PropertiesPropertySourceLoader、OriginTrackedPropertiesLoader类（直接复制这两个类只需将上述编码更改为UTF-8就可以，另外需要在spring.factories配置，这样springboot
SPI就可以加载到了），并将PropertiesPropertySourceLoader加载类的优先级高于框架自带的优先级，这样读取自定义的加载程序后就不会读取系统自带的加载程序。

------

GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)


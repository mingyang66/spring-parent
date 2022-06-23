### 死磕源码系列【PlaceholdersResolver、PropertyPlaceholderHelper配置文件占位符解析源码分析】

springboot配置文件可以使用占位符获取属性，如下示例(可以使用占位符${}获取其它的属性配置，也可以使用":"冒号连接，拿这些底层源码是如何解析的呢？)：

```java
test.city=上海
test.country=中国
test.address=${test.city}:${test.country}
test.cityadd=${test.city}
```

##### PlaceholdersResolver接口是Binder用于解析属性占位符的可选策略

```java
@FunctionalInterface
public interface PlaceholdersResolver {

	/**
	 * No-op {@link PropertyResolver}.
	 */
	PlaceholdersResolver NONE = (value) -> value;

	/**
	 * 解析给定值中的任何占位符
	 * @param value 配置属性源值
	 * @return 返回一个已解析占位符的值
	 */
	Object resolvePlaceholders(Object value);

}
```

##### PropertySourcesPlaceholdersResolver类是PlaceholdersResolver接口的唯一实现，占位符解析的能力都在此类中完成：

```java
package org.springframework.boot.context.properties.bind;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;
import org.springframework.util.Assert;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.SystemPropertyUtils;

/**
 * PlaceholdersResolver用于解析PropertySources属性配置的占位符
 */
public class PropertySourcesPlaceholdersResolver implements PlaceholdersResolver {
	//属性配置源
	private final Iterable<PropertySource<?>> sources;
	//占位符解析帮助类
	private final PropertyPlaceholderHelper helper;
	//创建PropertySourcesPlaceholdersResolver实例
	public PropertySourcesPlaceholdersResolver(Environment environment) {
		this(getSources(environment), null);
	}
	//创建PropertySourcesPlaceholdersResolver实例
	public PropertySourcesPlaceholdersResolver(Iterable<PropertySource<?>> sources) {
		this(sources, null);
	}

	public PropertySourcesPlaceholdersResolver(Iterable<PropertySource<?>> sources, PropertyPlaceholderHelper helper) {
		this.sources = sources;
    //如果帮助类为null,则创建默认的帮助类PropertyPlaceholderHelper
		this.helper = (helper != null) ? helper : new PropertyPlaceholderHelper(SystemPropertyUtils.PLACEHOLDER_PREFIX,
				SystemPropertyUtils.PLACEHOLDER_SUFFIX, SystemPropertyUtils.VALUE_SEPARATOR, true);
	}
	//解析配置文件属性value中包含的所有占位符
	@Override
	public Object resolvePlaceholders(Object value) {
		if (value instanceof String) {
      //调用帮助类解析配置文件中的占位符，并将解析占位符的值方法作为参数传入
			return this.helper.replacePlaceholders((String) value, this::resolvePlaceholder);
		}
		return value;
	}
	//作为helper帮助类中回调方法查找占位符的值
	protected String resolvePlaceholder(String placeholder) {
		if (this.sources != null) {
			for (PropertySource<?> source : this.sources) {
				Object value = source.getProperty(placeholder);
				if (value != null) {
					return String.valueOf(value);
				}
			}
		}
		return null;
	}
	//获取环境配置中的所有属性源集合
	private static PropertySources getSources(Environment environment) {
		Assert.notNull(environment, "Environment must not be null");
		Assert.isInstanceOf(ConfigurableEnvironment.class, environment,
				"Environment must be a ConfigurableEnvironment");
		return ((ConfigurableEnvironment) environment).getPropertySources();
	}

}

```

##### PropertyPlaceholderHelper占位符帮助类：

```java
package org.springframework.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.lang.Nullable;

/**
 * 用于处理配置文件包含占位符的应用程序类,占位符的格式如：${name}
 */
public class PropertyPlaceholderHelper {

	private static final Log logger = LogFactory.getLog(PropertyPlaceholderHelper.class);

	private static final Map<String, String> wellKnownSimplePrefixes = new HashMap<>(4);
	//已知的简单占位符前缀和后缀
	static {
		wellKnownSimplePrefixes.put("}", "{");
		wellKnownSimplePrefixes.put("]", "[");
		wellKnownSimplePrefixes.put(")", "(");
	}

	//占位符前缀
	private final String placeholderPrefix;
	//占位符后缀
	private final String placeholderSuffix;
	//简单前缀
	private final String simplePrefix;
	//占位符之间的分隔符
	@Nullable
	private final String valueSeparator;
	//是否忽略掉未解析的占位符
	private final boolean ignoreUnresolvablePlaceholders;


	/**
	 * 使用提供的前缀和后缀创建PropertyPlaceholderHelper实例，未解析成功的占位符将会被忽略掉
	 * @param placeholderPrefix 表示占位符的开头前缀
	 * @param placeholderSuffix 表示占位符结尾的后缀
	 */
	public PropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix) {
		this(placeholderPrefix, placeholderSuffix, null, true);
	}

	/**
	 * 使用提供的前缀和后缀创建PropertyPlaceholderHelper实例，未解析成功的占位符将会被忽略掉
	 * @param placeholderPrefix 表示占位符的开头前缀
	 * @param placeholderSuffix 表示占位符结尾的后缀
	 * @param valueSeparator 占位符变量和关联的默认值（如果有）之间的分隔符
	 * @param ignoreUnresolvablePlaceholders 指示是应忽略不可解析的占位符（true）,还是抛出异常（false）
	 */
	public PropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix,
			@Nullable String valueSeparator, boolean ignoreUnresolvablePlaceholders) {

		Assert.notNull(placeholderPrefix, "'placeholderPrefix' must not be null");
		Assert.notNull(placeholderSuffix, "'placeholderSuffix' must not be null");
		//占位符前缀
    this.placeholderPrefix = placeholderPrefix;
    //占位符后缀
		this.placeholderSuffix = placeholderSuffix;
    //获取占位符后缀对应的前缀
		String simplePrefixForSuffix = wellKnownSimplePrefixes.get(this.placeholderSuffix);
		if (simplePrefixForSuffix != null && this.placeholderPrefix.endsWith(simplePrefixForSuffix)) {
			this.simplePrefix = simplePrefixForSuffix;
		}
		else {
			this.simplePrefix = this.placeholderPrefix;
		}
    //占位符之间的分隔符
		this.valueSeparator = valueSeparator;
    //指示是应忽略不可解析的占位符（true）,还是抛出异常（false）
		this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
	}


	/**
	 * 将格式为{@code ${name}}的所有占位符替换为提供的属性配置中的值，
	 * @param value 包含要替换占位符的值
	 * @param properties 要替换占位符的{@code Properties}对象
	 * @return 替换占位符后的值
	 */
	public String replacePlaceholders(String value, final Properties properties) {
		Assert.notNull(properties, "'properties' must not be null");
		return replacePlaceholders(value, properties::getProperty);
	}

	/**
	 * 将格式为{@code ${name}}的所有占位符替换为提供的属性配置中的值，
	 * @param value 包含要替换占位符的值
	 * @param properties 要替换占位符的{@code Properties}对象
	 * @return 替换占位符后的值
	 */
	public String replacePlaceholders(String value, PlaceholderResolver placeholderResolver) {
		Assert.notNull(value, "'value' must not be null");
		return parseStringValue(value, placeholderResolver, null);
	}
	//替换占位符的
	protected String parseStringValue(
			String value, PlaceholderResolver placeholderResolver, @Nullable Set<String> visitedPlaceholders) {
		//获取占位符前缀的索引，如果为-1，说明字符串中不包含占位符，直接返回原值
		int startIndex = value.indexOf(this.placeholderPrefix);
		if (startIndex == -1) {
			return value;
		}

		StringBuilder result = new StringBuilder(value);
		while (startIndex != -1) {
      //查找占位符后缀的索引
			int endIndex = findPlaceholderEndIndex(result, startIndex);
			if (endIndex != -1) {
        //获取占位符内属性字段，如:${test.name}，返回结果是test.name
				String placeholder = result.substring(startIndex + this.placeholderPrefix.length(), endIndex);
				String originalPlaceholder = placeholder;
				if (visitedPlaceholders == null) {
					visitedPlaceholders = new HashSet<>(4);
				}
        //判定占位符是否发生循环应用现象，如果是则抛出异常
				if (!visitedPlaceholders.add(originalPlaceholder)) {
					throw new IllegalArgumentException(
							"Circular placeholder reference '" + originalPlaceholder + "' in property definitions");
				}
				//递归调用，解析占位符键中包含的占位符
				placeholder = parseStringValue(placeholder, placeholderResolver, visitedPlaceholders);
				//获取占位符属性配置对应的值（通过回调函数的方式）
				String propVal = placeholderResolver.resolvePlaceholder(placeholder);
        //占位符属性配置不存在，即，没有配置，这时候解析看是否有默认值配置
        //如：${test.name:defaultValue}
				if (propVal == null && this.valueSeparator != null) {
          //获取分割符的索引
					int separatorIndex = placeholder.indexOf(this.valueSeparator);
					if (separatorIndex != -1) {
            //获取占位符实际的属性配置
						String actualPlaceholder = placeholder.substring(0, separatorIndex);
            //获取占位符的实际默认 值
						String defaultValue = placeholder.substring(separatorIndex + this.valueSeparator.length());
            //递归解析实际的占位符
						propVal = placeholderResolver.resolvePlaceholder(actualPlaceholder);
            //如果占位符解析后的值是null,则给占位符赋予默认值
						if (propVal == null) {
							propVal = defaultValue;
						}
					}
				}
				if (propVal != null) {
					// 递归调用，解析先前解析的占位符值中包含的占位符
					propVal = parseStringValue(propVal, placeholderResolver, visitedPlaceholders);
          //将字符串中的占位符替换为解析后的值
					result.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal);
					if (logger.isTraceEnabled()) {
						logger.trace("Resolved placeholder '" + placeholder + "'");
					}
          //获取占位符的开始索引，如果不等于-1，则继续解析
					startIndex = result.indexOf(this.placeholderPrefix, startIndex + propVal.length());
				}
				else if (this.ignoreUnresolvablePlaceholders) {
					// 继续处理未处理的值
					startIndex = result.indexOf(this.placeholderPrefix, endIndex + this.placeholderSuffix.length());
				}
				else {
					throw new IllegalArgumentException("Could not resolve placeholder '" +
							placeholder + "'" + " in value \"" + value + "\"");
				}
				visitedPlaceholders.remove(originalPlaceholder);
			}
			else {
				startIndex = -1;
			}
		}
		return result.toString();
	}
	//查找占位符后缀的索引
	private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
		int index = startIndex + this.placeholderPrefix.length();
		int withinNestedPlaceholder = 0;
		while (index < buf.length()) {
			if (StringUtils.substringMatch(buf, index, this.placeholderSuffix)) {
				if (withinNestedPlaceholder > 0) {
					withinNestedPlaceholder--;
					index = index + this.placeholderSuffix.length();
				}
				else {
					return index;
				}
			}
			else if (StringUtils.substringMatch(buf, index, this.simplePrefix)) {
				withinNestedPlaceholder++;
				index = index + this.simplePrefix.length();
			}
			else {
				index++;
			}
		}
		return -1;
	}


	/**
	 * 策略接口，用于解析字符串中包含的占位符的替换值
	 */
	@FunctionalInterface
	public interface PlaceholderResolver {

		/**
		 * 将提供的占位符名称解析为替换值
		 * @param placeholderName 占位符名称
		 * @return 替换值, 或者为null，如果不进行替换
		 */
		@Nullable
		String resolvePlaceholder(String placeholderName);
	}

}

```

------

GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)


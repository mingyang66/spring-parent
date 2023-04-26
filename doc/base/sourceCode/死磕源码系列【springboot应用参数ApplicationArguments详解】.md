### 死磕源码系列【springboot应用参数ApplicationArguments详解】

> ApplicationArguments接口提供对用于运行org.springframework.boot.SpringApplication的参数访问，此接口只有一个实现类DefaultApplicationArguments。

如下示例(获取应用程序的args参数)：

```java
@SpringBootApplication
public class QuartzBootStrap {
    public static void main(String[] args) {
        SpringApplication.run(QuartzBootStrap.class, args);
    }
}
```

##### 1.ApplicationArguments接口源码

```java
public interface ApplicationArguments {

	/**
	 * 返回传递给应用程序的原始未处理参数
	 */
	String[] getSourceArgs();

	/**
	 * 返回所有选项参数的名称，例如，如果参数是"--foo=bar --debug"将会返回["foo", "debug"]
	 * @return the option names or an empty set
	 */
	Set<String> getOptionNames();

	/**
	 * 返回解析后的选项参数集合中是否包含给定名称的选项
	 * @param name the name to check
	 * @return {@code true} if the arguments contain an option with the given name
	 */
	boolean containsOption(String name);

	/**
	 * 返回与具有给定名称的arguments选项关联的值的集合
	 * 如果该选项存在并且没有参数值（例如：”--foo“）,则返回一个空集合
	 * 如果该选项存在并且只有一个值 (例如: "--foo=bar"), 则返回一个包含一个元素的集合["bar"]
	 * 如果该选项存在并且有多个值（例如："--foo=bar --foo=baz"）,则返回一个包含每个值元素的集合["bar", "baz"]
	 * 如果选项不存在，则返回null
	 * @param name 选项名称
	 * @return 返回选项值的集合（不存在返回null）
	 */
	List<String> getOptionValues(String name);

	/**
	 * 返回已解析的非选项参数，不存在返回空集合
	 * @return the non-option arguments or an empty list
	 */
	List<String> getNonOptionArgs();

}
```

> 看过上面的源码，可能会有一个疑问，什么是选项参数？什么是非选项参数？这个选项参数留到后面在解答。

##### 2.ApplicationArguments接口唯一实现类DefaultApplicationArguments源码

```java
public class DefaultApplicationArguments implements ApplicationArguments {
	//内部类，获取参数的主要操作都在此类中
	private final Source source;
	//应用程序原始未处理的参数
	private final String[] args;
	//构造函数，args不可以为null
	public DefaultApplicationArguments(String... args) {
		Assert.notNull(args, "Args must not be null");
		this.source = new Source(args);
		this.args = args;
	}
	//获取应用程序原始未处理的参数
	@Override
	public String[] getSourceArgs() {
		return this.args;
	}
	//获取应用程序解析后的所有选项参数名
	@Override
	public Set<String> getOptionNames() {
		String[] names = this.source.getPropertyNames();
		return Collections.unmodifiableSet(new HashSet<>(Arrays.asList(names)));
	}
	//判定解析后的选项是否包含指定的参数
	@Override
	public boolean containsOption(String name) {
		return this.source.containsProperty(name);
	}
	//获取所有解析后的选项参数值
	@Override
	public List<String> getOptionValues(String name) {
		List<String> values = this.source.getOptionValues(name);
		return (values != null) ? Collections.unmodifiableList(values) : null;
	}
	//获取所有非选项参数集合，如：[ "foo=bar", "foo=baz", "foo1=biz" ]
	@Override
	public List<String> getNonOptionArgs() {
		return this.source.getNonOptionArgs();
	}
	//内部类，继承了SimpleCommandLinePropertySource对命令行参数进行操作
	private static class Source extends SimpleCommandLinePropertySource {

		Source(String[] args) {
			super(args);
		}

		@Override
		public List<String> getNonOptionArgs() {
			return super.getNonOptionArgs();
		}

		@Override
		public List<String> getOptionValues(String name) {
			return super.getOptionValues(name);
		}

	}

}
```

##### 3.SimpleCommandLinePropertySource类源码

与所有的CommandLinePropertySource实现一样，命令行参数分为两个不同的组（其另外一个实现JOptCommandLinePropertySource是使用第三方实现[https://github.com/jopt-simple/jopt-simple](https://github.com/jopt-simple/jopt-simple)
）：选项参数和非选项参数；

使用选项参数必须遵循精确的语法，也就是说，选项必须以”--“为前缀，并且可以指定值，也可以不指定值；如果指定了一个值，则名称和值必须用”=“号分割，且不带空格，值可以是空字符串。

选项参数的有效示例：

```
 --foo
 --foo=
 --foo=""
 --foo=bar
 --foo="bar then baz"
 --foo=bar,baz,biz
```

选项参数的无效示例：

```
 -foo
 --foo bar
 --foo = bar
 --foo=bar --foo=baz --foo=biz
```

非选项参数：

在命令行中指定的没有”--“选项前缀的任何参数都将被视为非选项参数。

```java
public class SimpleCommandLinePropertySource extends CommandLinePropertySource<CommandLineArgs> {

	/**
	 * 创建一个拥有默认名”commandLineArgs“和String[]参数的SimpleCommandLinePropertySource对象
	 */
	public SimpleCommandLinePropertySource(String... args) {
    //SimpleCommandLineArgsParser类用来解析命令行参数String[]数组，然后返回CommandLineArgs对象
		super(new SimpleCommandLineArgsParser().parse(args));
	}

	/**
	 * 创建一个指定name和String[]参数的SimpleCommandLinePropertySource对象
	 */
	public SimpleCommandLinePropertySource(String name, String[] args) {
		super(name, new SimpleCommandLineArgsParser().parse(args));
	}
	...

}

```

##### 4.SimpleCommandLineArgsParser解析类源码

使用选项参数必须遵循精确的语法，也就是说，选项必须以”--“为前缀，并且可以指定值，也可以不指定值；如果指定了一个值，则名称和值必须用”=“号分割，且不带空格，值可以是空字符串。

选项参数的有效示例：

```
 --foo
 --foo=
 --foo=""
 --foo=bar
 --foo="bar then baz"
 --foo=bar,baz,biz
```

选项参数的无效示例：

```
 -foo
 --foo bar
 --foo = bar
 --foo=bar --foo=baz --foo=biz
```

非选项参数：

在命令行中指定的没有”--“选项前缀的任何参数都将被视为非选项参数。

```java
class SimpleCommandLineArgsParser {

	/**
	 * 解析给定的String[]数组参数根据上面描述的规则，返回一个CommandLineArgs对象
	 * {@link CommandLineArgs} object.
	 * @param args command line arguments, typically from a {@code main()} method
	 */
	public CommandLineArgs parse(String... args) {
    //创建命令行参数对象
		CommandLineArgs commandLineArgs = new CommandLineArgs();
    //循环命令行参数
		for (String arg : args) {
      //如果是以”--“为前缀，则判定为选项参数
			if (arg.startsWith("--")) {
				String optionText = arg.substring(2);
				String optionName;
				String optionValue = null;
				int indexOfEqualsSign = optionText.indexOf('=');
				if (indexOfEqualsSign > -1) {
					optionName = optionText.substring(0, indexOfEqualsSign);
					optionValue = optionText.substring(indexOfEqualsSign + 1);
				}
				else {
					optionName = optionText;
				}
				if (optionName.isEmpty()) {
					throw new IllegalArgumentException("Invalid argument syntax: " + arg);
				}
        //新增选项参数
				commandLineArgs.addOptionArg(optionName, optionValue);
			}
			else {
         //新增非选项参数
				commandLineArgs.addNonOptionArg(arg);
			}
		}
		return commandLineArgs;
	}

}
```

##### 5.org.springframework.core.env.CommandLineArgs类源码

命令行参数的简单表示，分为”选项参数“和”非选项参数“；可以通过SimpleCommandLineArgsParser解析类解析得到。

```java
class CommandLineArgs {
	//存放选项参数，参数名：参数值
	private final Map<String, List<String>> optionArgs = new HashMap<>();
  //存放非选项参数
	private final List<String> nonOptionArgs = new ArrayList<>();

	/**
	 * 添加给定的选项名和选项值
	 */
	public void addOptionArg(String optionName, @Nullable String optionValue) {
		if (!this.optionArgs.containsKey(optionName)) {
			this.optionArgs.put(optionName, new ArrayList<>());
		}
		if (optionValue != null) {
			this.optionArgs.get(optionName).add(optionValue);
		}
	}

	/**
	 * 返回命令行中所有选项参数的集合
	 */
	public Set<String> getOptionNames() {
		return Collections.unmodifiableSet(this.optionArgs.keySet());
	}

	/**
	 * 返回命令行中是否存在具有给定的选项名
	 */
	public boolean containsOption(String optionName) {
		return this.optionArgs.containsKey(optionName);
	}

	/**
	 * 返回与给定选项名关联的值列表，null表示该选项不存在，空列表表示没有值与该选项关联
	 */
	@Nullable
	public List<String> getOptionValues(String optionName) {
		return this.optionArgs.get(optionName);
	}

	/**
	 * 将给定的值添加到非选项列表
	 */
	public void addNonOptionArg(String value) {
		this.nonOptionArgs.add(value);
	}

	/**
	 * 返回在命令行上指定的非选项参数的列表
	 */
	public List<String> getNonOptionArgs() {
		return Collections.unmodifiableList(this.nonOptionArgs);
	}

}
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)


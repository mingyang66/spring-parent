### Spring Boot2.1.6学习笔记：元数据文件（properties|yml文件自动提示）

> 参考： https://docs.spring.io/spring-boot/docs/2.1.6.RELEASE/reference/html/configuration-metadata.html#configuration-metadata-annotation-processor 
>
> Spring Boot Jars包含元数据文件，这些文件提供了所有支持的配置属性的详细信息。这些文件旨在让IDE开发人员在使用application.properties或application.yml文件时提供提示及自动完成功能；当然开发人员也可以自定义元数据文件，这样我们自己定义的配置属性可以像系统的属性一样拥有提示功能，想想也是很美好的事情，接下来我们就看下如何实现这样的功能。

##### 1.配置处理器

通过使用spring boot提供的配置处理器jar包，你可以使用@ConfigurationProperties注释轻松的生成自己的配置元数据文件，jar包包含一个Java注释处理器，在编译项目时调用它。要使用处理器，请引入对如下spring boot配置处理器的依赖关系：

```java
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-configuration-processor</artifactId>
	<optional>true</optional>
</dependency>
```

处理器同时获取用@ConfigurationProperties注释的类和方法。配置类中字段值的Javadoc用于填充description属性。

> 只应将简单文本与@ConfigurationProperties字段Javadoc一起使用，因为在将它们添加到JSON之前不会对它们进行处理

属性是通过标准getter和setter来发现的，他们对集合类型进行了特殊处理（即使只有getter存在，也会检测到）。注释处理器还支持使用@Data、@Getter、@Setter lombok注释。



如果你正在使用AspectJ在你的项目中，你需要确保注释处理器只运行一次；这里有几种方法处理，如果你使用的是maven，你可以显式配置maven-apt-plugin，并仅在哪里像注释处理器添加依赖项。你可以让AspectJ插件运行所有处理，并在maven编译器插件配置中禁用注释处理。

```
<plugin>
	<groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-compiler-plugin</artifactId>
	<configuration>
		<proc>none</proc>
	</configuration>
</plugin>
```

##### 2.元数据格式

配置元数据文件在jar包的 META-INF/spring-configuration-metadata.json 里，它们使用一种简单的JSON格式，将项目分类在“groups”或“properties”下，将附加值提示分类在“hints”下，如下示例所示：

```
{"groups": [
	{
		"name": "server",
		"type": "org.springframework.boot.autoconfigure.web.ServerProperties",
		"sourceType": "org.springframework.boot.autoconfigure.web.ServerProperties"
	},
	{
		"name": "spring.jpa.hibernate",
		"type": "org.springframework.boot.autoconfigure.orm.jpa.JpaProperties$Hibernate",
		"sourceType": "org.springframework.boot.autoconfigure.orm.jpa.JpaProperties",
		"sourceMethod": "getHibernate()"
	}
	...
],"properties": [
	{
		"name": "server.port",
		"type": "java.lang.Integer",
		"sourceType": "org.springframework.boot.autoconfigure.web.ServerProperties"
	},
	{
		"name": "server.address",
		"type": "java.net.InetAddress",
		"sourceType": "org.springframework.boot.autoconfigure.web.ServerProperties"
	},
	{
		  "name": "spring.jpa.hibernate.ddl-auto",
		  "type": "java.lang.String",
		  "description": "DDL mode. This is actually a shortcut for the \"hibernate.hbm2ddl.auto\" property.",
		  "sourceType": "org.springframework.boot.autoconfigure.orm.jpa.JpaProperties$Hibernate"
	}
	...
],"hints": [
	{
		"name": "spring.jpa.hibernate.ddl-auto",
		"values": [
			{
				"value": "none",
				"description": "Disable DDL handling."
			},
			{
				"value": "validate",
				"description": "Validate the schema, make no changes to the database."
			},
			{
				"value": "update",
				"description": "Update the schema if necessary."
			},
			{
				"value": "create",
				"description": "Create the schema and destroy previous data."
			},
			{
				"value": "create-drop",
				"description": "Create and then destroy the schema at the end of the session."
			}
		]
	}
]}
```

每个“property”都是用户用给定值指定的配置项。例如可以在application.properties中指定server.port和server.address，如下所示：

```
server.port=9090
server.address=127.0.0.1
```

group是更高级别的项，它们本身并不指定值，而是为属性提供上线文分组。例如，server.port和server.address属性是服务器组的一部分。

> 并不要求每一个property拥有一个group，一些properties可能单独存在。

最后，hints是用于帮助用户配置给定属性的附加信息。例如，当开发人员配置spring.jpa.hibernate.ddl-auto属性时，工具可以使用提示为none、validate、update、create和create drop值提供一些自动完成帮助。

##### 3.生成元数据文件示例

- 引入第一步中的maven依赖
- 定义一个使用@ConfigurationProperties注解标注的java类

> 该类包含普通的自动提示、枚举、Map对应keyvalue提示、配置属性值、属性过期、引入外部类

```
package com.yaomy.control.common.control.conf;

import com.yaomy.control.common.control.test.School;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

/**
 * 配置元数据文件.
 */
@SuppressWarnings("all")
@ConfigurationProperties(prefix = "spring.yaomy", ignoreInvalidFields = true)
public class MetaDataProperties {

    private HttpClient httpClient = new HttpClient();

    private Test test = new Test();

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public Test getTest() {
        return test;
    }

    /**
     * HttpClient网络请求属性配置类
     */
    public static class HttpClient{
        /**
         * HttpClientService read timeout (in milliseconds),default:5000
         */
        private Integer readTimeOut = 5000;
        /**
         * HttpClientService connect timeout (in milliseconds),default:10000
         */
        private Integer connectTimeOut = 10000;

        public Integer getReadTimeOut() {
            return readTimeOut;
        }

        public void setReadTimeOut(Integer readTimeOut) {
            this.readTimeOut = readTimeOut;
        }

        public Integer getConnectTimeOut() {
            return connectTimeOut;
        }

        public void setConnectTimeOut(Integer connectTimeOut) {
            this.connectTimeOut = connectTimeOut;
        }
    }

    /**
     * test metadata
     */
    public static class Test{
        /**
         * context
         */
        private Map<String, String> context;
        /**
         * test tip
         */
        private String testTip;
        /**
         * test type
         */
        private Type testType = Type.ADD;
        /**
         * school
         */
        @NestedConfigurationProperty
        private School school;

        /**
         * enable or disable default false
         */
        private Boolean testEnable = Boolean.FALSE;
        /**
         * deprecated test
         */
        private String deprecated;

        public Map<String, String> getContext() {
            return context;
        }

        public void setContext(Map<String, String> context) {
            this.context = context;
        }

        public String getTestTip() {
            return testTip;
        }

        public void setTestTip(String testTip) {
            this.testTip = testTip;
        }

        public Type getTestType() {
            return testType;
        }

        public void setTestType(Type testType) {
            this.testType = testType;
        }

        public School getSchool() {
            return school;
        }

        public void setSchool(School school) {
            this.school = school;
        }

        public Boolean getTestEnable() {
            return testEnable;
        }

        public void setTestEnable(Boolean testEnable) {
            this.testEnable = testEnable;
        }
        @DeprecatedConfigurationProperty(replacement = "app.acme.name", reason = "not a userful property")
        @Deprecated
        public String getDeprecated() {
            return deprecated;
        }
        @Deprecated
        public void setDeprecated(String deprecated) {
            this.deprecated = deprecated;
        }
    }
    public enum  Type{
        CREATE,UPDATE,DEL,ADD
    }
}

```

- 重新构建项目会生成target/classes/META-INF/spring-configuration-metadata.json文件

```
{
  "groups": [
    {
      "name": "spring.yaomy",
      "type": "com.yaomy.control.common.control.conf.MetaDataProperties",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties"
    },
    {
      "name": "spring.yaomy.http-client",
      "type": "com.yaomy.control.common.control.conf.MetaDataProperties$HttpClient",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties",
      "sourceMethod": "getHttpClient()"
    },
    {
      "name": "spring.yaomy.test",
      "type": "com.yaomy.control.common.control.conf.MetaDataProperties$Test",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties",
      "sourceMethod": "getTest()"
    },
    {
      "name": "spring.yaomy.test.school",
      "type": "com.yaomy.control.common.control.conf.School",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties$Test",
      "sourceMethod": "getSchool()"
    }
  ],
  "properties": [
    {
      "name": "spring.yaomy.http-client.connect-time-out",
      "type": "java.lang.Integer",
      "description": "HttpClientService connect timeout (in milliseconds),default:10000",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties$HttpClient",
      "defaultValue": 10000
    },
    {
      "name": "spring.yaomy.http-client.read-time-out",
      "type": "java.lang.Integer",
      "description": "HttpClientService read timeout (in milliseconds),default:5000",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties$HttpClient",
      "defaultValue": 5000
    },
    {
      "name": "spring.yaomy.test.context",
      "type": "java.util.Map<java.lang.String,java.lang.String>",
      "description": "context",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties$Test"
    },
    {
      "name": "spring.yaomy.test.school.address",
      "type": "java.lang.String",
      "description": "address",
      "sourceType": "com.yaomy.control.common.control.conf.School"
    },
    {
      "name": "spring.yaomy.test.school.name",
      "type": "java.lang.String",
      "description": "name",
      "sourceType": "com.yaomy.control.common.control.conf.School"
    },
    {
      "name": "spring.yaomy.test.school.no",
      "type": "java.lang.Integer",
      "description": "学号",
      "sourceType": "com.yaomy.control.common.control.conf.School"
    },
    {
      "name": "spring.yaomy.test.test-enable",
      "type": "java.lang.Boolean",
      "description": "enable or disable default false",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties$Test",
      "defaultValue": false
    },
    {
      "name": "spring.yaomy.test.test-tip",
      "type": "java.lang.String",
      "description": "test tip",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties$Test"
    },
    {
      "name": "spring.yaomy.test.test-type",
      "type": "com.yaomy.control.common.control.conf.MetaDataProperties$Type",
      "description": "test type",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties$Test"
    },
    {
      "name": "spring.yaomy.test.deprecated",
      "type": "java.lang.String",
      "description": "deprecated test",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties$Test",
      "deprecated": true,
      "deprecation": {
        "reason": "not a userful property",
        "replacement": "app.acme.name"
      }
    }
  ],
  "hints": []
}
```

- 上一步我们生成了spring-configuration-metadata.json文件，但是文件中的hints提示属性为空，这就需要我们在META-INF目录下自定义一个additional-spring-configuration-metadata.json文件

```
{"hints": [
  {
    "name": "spring.yaomy.test.context.keys",
    "values": [
      {
        "value": "key1",
        "description": "key description1."
      },
      {
        "value": "key2",
        "description": "key description2."
      }
    ]
  },
  {
    "name": "spring.yaomy.test.context.values",
    "values": [
      {
        "value": "value1",
        "description": "value description1."
      },
      {
        "value": "value2",
        "description": "value description2."
      }
    ]
  },
  {
    "name": "spring.yaomy.test.test-tip",
    "values": [
      {
        "value": "add",
        "description": "add description1."
      },
      {
        "value": "del",
        "description": "del description2."
      },
      {
        "value": "update",
        "description": "del description2."
      },
      {
        "value": "query",
        "description": "del description2."
      }
    ]
  }
]}
```

- 重新编译项目生成如下文件

```
{
  "groups": [
    {
      "name": "spring.yaomy",
      "type": "com.yaomy.control.common.control.conf.MetaDataProperties",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties"
    },
    {
      "name": "spring.yaomy.http-client",
      "type": "com.yaomy.control.common.control.conf.MetaDataProperties$HttpClient",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties",
      "sourceMethod": "getHttpClient()"
    },
    {
      "name": "spring.yaomy.test",
      "type": "com.yaomy.control.common.control.conf.MetaDataProperties$Test",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties",
      "sourceMethod": "getTest()"
    },
    {
      "name": "spring.yaomy.test.school",
      "type": "com.yaomy.control.common.control.conf.School",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties$Test",
      "sourceMethod": "getSchool()"
    }
  ],
  "properties": [
    {
      "name": "spring.yaomy.http-client.connect-time-out",
      "type": "java.lang.Integer",
      "description": "HttpClientService connect timeout (in milliseconds),default:10000",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties$HttpClient",
      "defaultValue": 10000
    },
    {
      "name": "spring.yaomy.http-client.read-time-out",
      "type": "java.lang.Integer",
      "description": "HttpClientService read timeout (in milliseconds),default:5000",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties$HttpClient",
      "defaultValue": 5000
    },
    {
      "name": "spring.yaomy.test.context",
      "type": "java.util.Map<java.lang.String,java.lang.String>",
      "description": "context",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties$Test"
    },
    {
      "name": "spring.yaomy.test.school.address",
      "type": "java.lang.String",
      "description": "address",
      "sourceType": "com.yaomy.control.common.control.conf.School"
    },
    {
      "name": "spring.yaomy.test.school.name",
      "type": "java.lang.String",
      "description": "name",
      "sourceType": "com.yaomy.control.common.control.conf.School"
    },
    {
      "name": "spring.yaomy.test.school.no",
      "type": "java.lang.Integer",
      "description": "学号",
      "sourceType": "com.yaomy.control.common.control.conf.School"
    },
    {
      "name": "spring.yaomy.test.test-enable",
      "type": "java.lang.Boolean",
      "description": "enable or disable default false",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties$Test",
      "defaultValue": false
    },
    {
      "name": "spring.yaomy.test.test-tip",
      "type": "java.lang.String",
      "description": "test tip",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties$Test"
    },
    {
      "name": "spring.yaomy.test.test-type",
      "type": "com.yaomy.control.common.control.conf.MetaDataProperties$Type",
      "description": "test type",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties$Test"
    },
    {
      "name": "spring.yaomy.test.deprecated",
      "type": "java.lang.String",
      "description": "deprecated test",
      "sourceType": "com.yaomy.control.common.control.conf.MetaDataProperties$Test",
      "deprecated": true,
      "deprecation": {
        "reason": "not a userful property",
        "replacement": "app.acme.name"
      }
    }
  ],
  "hints": [
    {
      "name": "spring.yaomy.test.context.keys",
      "values": [
        {
          "value": "key1",
          "description": "key description1."
        },
        {
          "value": "key2",
          "description": "key description2."
        }
      ]
    },
    {
      "name": "spring.yaomy.test.context.values",
      "values": [
        {
          "value": "value1",
          "description": "value description1."
        },
        {
          "value": "value2",
          "description": "value description2."
        }
      ]
    },
    {
      "name": "spring.yaomy.test.test-tip",
      "values": [
        {
          "value": "add",
          "description": "add description1."
        },
        {
          "value": "del",
          "description": "del description2."
        },
        {
          "value": "update",
          "description": "del description2."
        },
        {
          "value": "query",
          "description": "del description2."
        }
      ]
    }
  ]
}
```

> 这样在application.properties配置文件中编写配置属性时不管是属性key还是value都会有对应的提示功能

##### 4.元数据文件生成好后，如何在项目中引入使用呢？这里有两种方案

- 直接使用的类上加上@EnableConfigurationProperties(MetaDataProperties.class)

```
@EnableConfigurationProperties(MetaDataProperties.class)
@Configuration
public class NetWorkConfig {
    /**
     * 读取配置属性服务类
     */
    @Autowired
    private MetaDataProperties metaDataProperties;

	...
    /**
     * 定义HTTP请求工厂方法
     */
    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        //读取超时5秒,默认无限限制,单位：毫秒
        factory.setReadTimeout(metaDataProperties.getHttpClient().getReadTimeOut());
        //连接超时10秒，默认无限制，单位：毫秒
        factory.setConnectTimeout(metaDataProperties.getHttpClient().getConnectTimeOut());
        return factory;
    }

}
```

- 第二种方案是在META-INF/spring.factories文件中加上如下配置

```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=com.yaomy.control.common.control.conf.MetaDataProperties
```

在具体的类中的使用方法如下：

```
@Configuration
public class NetWorkConfig {
    /**
     * 读取配置属性服务类
     */
    @Autowired
    private MetaDataProperties metaDataProperties;

	...
    /**
     * 定义HTTP请求工厂方法
     */
    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        //读取超时5秒,默认无限限制,单位：毫秒
        factory.setReadTimeout(metaDataProperties.getHttpClient().getReadTimeOut());
        //连接超时10秒，默认无限制，单位：毫秒
        factory.setConnectTimeout(metaDataProperties.getHttpClient().getConnectTimeOut());
        return factory;
    }

}
```

> 综上两种方法，建议选择第二种方法，第一种需要在每个用到类中加上一个注解，比较麻烦；第二种方法只需要配置在spring.factories文件中，容器启动的时候自动的将java bean加载进入容器中，跟使用普通的Java Bean一样。
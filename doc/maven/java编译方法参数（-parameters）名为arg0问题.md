### java编译方法参数（-parameters）名为arg0问题

##### 一、idea设置编译时获取方法参数名方案

Preferences->Build,Execution,Deployment->Compiler->Java Compiler

![在这里插入图片描述](https://img-blog.csdnimg.cn/b4d4d28fde384c9491469571f37ae245.png)

##### 二、maven插件编译时设置方案

版本在3.6.2之前设置：

```xml
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.6.1</version>
                <configuration>
                    <compilerArgs>-parameters</compilerArgs>
                </configuration>
            </plugin>
```

版本在3.6.2（包含）以后设置：

```xml
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <parameters>true</parameters>
                </configuration>
            </plugin>
```

##### 三、springboot项目继承自spring-boot-starter-parent，默认已开启，无需依赖引入

```xml
        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-compiler-plugin</artifactId>
          <configuration>
            <parameters>true</parameters>
          </configuration>
        </plugin>
```

如果项目中又添加了maven-compiler-plugin配置，并且设置了parameters配置，则以项目中的为准，如果未设置parameters属性，如果springboot自带设置方式跟当前版本一致，则没问题，否则会出现参数名为arg0问题。

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
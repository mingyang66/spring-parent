### javadoc注解及maven-javadoc-plugin插件

##### 一、javadoc注解标记：

-  @author：作者-类、接口级别


- @version：版本-类、接口级别 


- @deprecated：不推荐使用的方法


- @param：方法、构造函数的参数  


- @return：方法的返回类型


- @see：用于指定参考的内容，一般会带有链接或文本条目


- @exception：抛出的异常 

-  @throws：抛出的异常，和exception同义
-  @since: 标识此更改或功能子指定的版本、时间开始存在

##### 二、maven-javadoc-plugin插件

```xml
                    <!-- 生成API文档插件 -->
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-javadoc-plugin</artifactId>
                        <version>3.3.2</version>
                        <executions>
                            <execution>
                                <id>attach-javadocs</id>
                                <goals>
                                    <goal>jar</goal>
                                </goals>
                            </execution>
                        </executions>
                    </plugin>
```

在java项目中打包时会引入maven-javadoc-plugin插件将java注解打包到一个jar包中，但是如果java的注解不够标准的话则会抛出异常，无法正常的打包，如下异常：

```sh
Command line was: /Library/Java/JavaVirtualMachines/jdk-11.0.10.jdk/Contents/Home/bin/javadoc @options @packages

Refer to the generated Javadoc files in '/Users/xx/Documents/IDE/workplace-java/spring-parent/emily-spring-boot-logger/target/apidocs' dir.
```

解决方案，添加failOnError参数如下配置：

```xml
                    <!-- 生成API文档插件 -->
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-javadoc-plugin</artifactId>
                        <version>3.3.2</version>
                        <configuration>
                            <failOnError>false</failOnError>
                        </configuration>
                        <executions>
                            <execution>
                                <id>attach-javadocs</id>
                                <goals>
                                    <goal>jar</goal>
                                </goals>
                            </execution>
                        </executions>
                    </plugin>
```

> 将failOnError参数设置为false，即在生成文档时出现错误时不会停止构建过程。
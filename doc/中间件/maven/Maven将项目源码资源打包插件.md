### Maven将项目源码资源打包插件

通常我们将项目打包的时候使用的命令如下：

```
mvn clean install
或
./mvnw clean install
```

这样我们打包通常是只打包了一个jar包，打开源码的时候里面的注释都丢失了，那如何将注释也一起打进来呢？那我们就要引入一个生成源码资源包的插件：

```
 <!--配置生成源码包-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-source-plugin</artifactId>
                <version>3.0.1</version>
                <executions>
                    <execution>
                        <id>attach-sources</id>
                        <goals>
                            <goal>jar</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
```

将该项目引入pom文件中，然后再执行如上命令，仓库中就会出现两个jar:

```
_remote.repositories
emily-spring-boot-common-service-2.1.1.RELEASE-sources.jar
emily-spring-boot-common-service-2.1.1.RELEASE.jar
emily-spring-boot-common-service-2.1.1.RELEASE.pom
```

如上会多一个sources.jar包，这个包就是源码包，包含我们的注解，在查看注解的时候就可以从中央仓库download注解包了。

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/doc/maven](https://github.com/mingyang66/spring-parent/tree/master/doc/maven)

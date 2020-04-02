### Maven Wrapper插件安装使用

Maven Wrapper是一个maven插件，用于封装提供maven项目构建时所需的一切；使用maven wrapper就可以很好的确保所有参与项目者使用相同的maven版本，同时还不会影响其它项目；安装成功后会在项目中出现mvnw命令，是mvn的替代。



官方网站：https://github.com/takari/maven-wrapper

##### 1.安装maven wrapper简单方式是在idea控制台输入如下命令

```
mvn -N io.takari:maven:0.7.7:wrapper -Dmaven=3.6.3
```

命令执行成功后插件就会在.m2/wrapper目录下下载指定版本的maven;默认的maven仓库是：

```
https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.6.3/apache-maven-3.6.3-bin.zip
```

如果下载缓慢或者下载不下来可以指定本地路径，找到项目中.mvn/wrapper/maven-wrapper.properties文件，修改distributionUrl属性为(mac)：

```
distributionUrl=file:////Users/xxx/Documents/IDE/apache-maven-wrapper/apache-maven-3.6.3-bin.zip
```

然后再控制台输入./mvnw命令，但是报如下错误：

```
[ERROR] No goals have been specified for this build. You must specify a valid lifecycle phase or a goal in the format <plugin-prefix>:<goal> or <plugin-group-id>:<plugin-artifact-id>[:<plugin-version>]:<goal>. Available lifecycle phases are: validate, initialize, generate-sources, process-sources, generate-resources, process-resources, compile, process-classes, generate-test-sources, process-test-sources, generate-test-resources, process-test-resources, test-compile, process-test-classes, test, prepare-package, package, pre-integration-test, integration-test, post-integration-test, verify, install, deploy, pre-clean, clean, post-clean, pre-site, site, post-site, site-deploy. -> [Help 1]

```

这是因为构建的时候没有指定目标goal,解决方法是在pom文件的build中添加上如下：

```
<defaultGoal>compile</defaultGoal>
```

然后再执行mvnw命令就可以成功的执行；

##### 2.将当前项目打包

```
./mvnw clean install
```

测试可以成功的将项目打包

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/doc/maven](https://github.com/mingyang66/spring-parent/tree/master/doc/maven)
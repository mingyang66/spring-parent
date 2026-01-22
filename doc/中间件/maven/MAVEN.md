### maven父pom和子pom的版本号批量修改

##### 1 设置新的版本号

```
./mvnw versions:set -DnewVersion=4.4.8
```

##### 2 撤销设置

```
./mvnw versions:revert
```

##### 3 提交设置

```
./mvnw versions:commit
```

##### 4.项目打包(同时处理项目所依赖的包)

```
mvn clean install -pl emily-spring-boot-starter -am
```

或

```
./mvnw clean install -pl emily-spring-boot-starter -am
```

| 参数 | 全程                   | 说明                                                         |
| ---- | ---------------------- | ------------------------------------------------------------ |
| -pl  | --projects             | 选项后可跟随{groupId}:{artifactId}或者所选模块的相对路径(多个模块以逗号分隔) |
| -am  | --also-make            | 表示同时处理选定模块所依赖的模块                             |
| -amd | --also-make-dependents | 表示同时处理依赖选定模块的模块                               |
| -N   | --non-                 | 表示不递归子模块                                             |
| -rf  | --resume-frm           | 表示从指定模块开始继续处理                                   |
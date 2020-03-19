# spring-parent
### maven父pom和子pom的版本号批量修改

##### 1 设置新的版本号

```
mvn versions:set -DnewVersion=2.1.1.RELEASE
```

##### 2 撤销设置

```
mvn versions:revert
```

##### 3 提交设置

```
mvn versions:commit
```
------
### 关闭不希望启动的组件
##### 1.关闭Redisson组件
```
@SpringBootApplication(exclude = {RedissonAutoConfiguration.class})
```
------

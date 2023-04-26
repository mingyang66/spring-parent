#### druid数据库连接池物理连接超时时间phyTimeoutMills及物理最大连接数phyMaxUseCount

配置：

```properties
#物理超时时间，默认：-1
spring.emily.datasource.config.mysql.phy-timeout-millis=-1
#物理最大连接数，默认：-1（不建议配置）
spring.emily.datasource.config.mysql.phy-max-use-count=-1
```

> phy-timeout-millis是指连接的最大物理连接时长，超过则会被强制回收，phy-max-use-count则是最大的物理连接数，超过则会被强制回收，这两个连接都要慎用；

##### phy-timeout-millis最大物理连接时长在源码中生效场景一：

com.alibaba.druid.pool.DruidDataSource#recycle回收方法，在连接操作完数据库后，回收连接的方法中判定：

```java
#物理连接超时时间大于0
if (phyTimeoutMillis > 0) {
  //connectTimeMills是连接建立是的时间
  //当前连接的时长，如果大于最大物理连接时间，则丢弃连接
  long phyConnectTimeMillis = currentTimeMillis - holder.connectTimeMillis;
  if (phyConnectTimeMillis > phyTimeoutMillis) {
    discardConnection(holder);
    return;
  }
}
```

##### phy-timeout-millis最大物理连接时长在源码中生效场景二：

com.alibaba.druid.pool.DruidDataSource.DestroyTask守护线程的shrink方法中判定：

```java
//当前连接的市场大于物理连接时长则丢弃连接
if (phyTimeoutMillis > 0) {
  long phyConnectTimeMillis = currentTimeMillis - connection.connectTimeMillis;
  if (phyConnectTimeMillis > phyTimeoutMillis) {
    evictConnections[evictCount++] = connection;
    continue;
  }
}
```

##### phy-max-use-count物理连接最大使用数量使用场景com.alibaba.druid.pool.DruidDataSource#recycle连接回收方法：

```java

//物理连接数量大于0，并且当前连接使用的数量大于物理连接数量就会丢弃当前连接
if (phyMaxUseCount > 0 && holder.useCount >= phyMaxUseCount) {
discardConnection(holder);
return;
}
```

> 这两个属性配置都会损耗一定的性能，使用的时候慎重，不建议在生产环境配置；



GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)


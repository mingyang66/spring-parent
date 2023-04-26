#### druid数据库连接池如何维护

>
druid如何维护线程池，其实是我在翻看源码过程中产生的一个疑问，在com.alibaba.druid.pool.DruidDataSource.DestroyTask守护线程中会调用shrink方法，在此方法中会对线程池中的线程有效性进行维护，对线程池中的线程进行keepAlive保活机制检查；

其中有一个变量checkCount，即当前需要检查剔除连接的数量：

```java
final int checkCount = poolingCount - minIdle;
```

> poolingCount是当前线程池中的线程数量，minIdle是线程池的最小空闲连接；

在讲解之前先看下数据库连接池是什么？在初始化方法com.alibaba.druid.pool.DruidDataSource#init中可以发现这样一段代码：

```java
//数据库连接池对象数组，最大数量是maxActive            
connections = new DruidConnectionHolder[maxActive];
//剔除数据库连接数组
evictConnections = new DruidConnectionHolder[maxActive];
//保活数据库连接数组
keepAliveConnections = new DruidConnectionHolder[maxActive];
```

> 数据库连接池其实是connections数组，数组是有序的，并且长度固定，最先建立的数据库连接数组索引最小，获取数据连接从数组索引最大的取；

看下com.alibaba.druid.pool.DruidDataSource#shrink(boolean, boolean)方法中判定剔除连接及保活连接的逻辑代码：

```java
//连接池中需要被剔除的连接数量（当然不一定一定会剔除掉）
final int checkCount = poolingCount - minIdle;
final long currentTimeMillis = System.currentTimeMillis();
for (int i = 0; i < poolingCount; ++i) {
  DruidConnectionHolder connection = connections[i];
	//如果发生致命性异常将会把连接放入保活连接数组中，接下来检查判定是否要剔除关闭
  if ((onFatalError || fatalErrorIncrement > 0) && (lastFatalErrorTimeMillis > connection.connectTimeMillis))  {
    keepAliveConnections[keepAliveCount++] = connection;
    continue;
  }

  if (checkTime) {
    //如果设置了物理连接超时时间，那么连接超过设置的时间则会放入剔除连接数组，接下来会被关闭
    if (phyTimeoutMillis > 0) {
      long phyConnectTimeMillis = currentTimeMillis - connection.connectTimeMillis;
      if (phyConnectTimeMillis > phyTimeoutMillis) {
        evictConnections[evictCount++] = connection;
        continue;
      }
    }

    long idleMillis = currentTimeMillis - connection.lastActiveTimeMillis;
		//如果当前连接的空闲时间小于最小空闲剔除时间，并且小于连接保活间隔时间，则跳过
    if (idleMillis < minEvictableIdleTimeMillis
        && idleMillis < keepAliveBetweenTimeMillis
       ) {
      break;
    }
		//如果当前数据库连接空闲时间大于最小空闲剔除时间，并且数组索引小于检查剔除的数量，则加入剔除数组
    //如果当前连接空闲时间大于最大空闲剔除时间，则加入剔除数组；
    if (idleMillis >= minEvictableIdleTimeMillis) {
      if (checkTime && i < checkCount) {
        evictConnections[evictCount++] = connection;
        continue;
      } else if (idleMillis > maxEvictableIdleTimeMillis) {
        evictConnections[evictCount++] = connection;
        continue;
      }
    }
		//如果开启了保活机制，并且空闲时间大于保活时间间隔，则加入保活数组；
    if (keepAlive && idleMillis >= keepAliveBetweenTimeMillis) {
      keepAliveConnections[keepAliveCount++] = connection;
    }
  } else {
    if (i < checkCount) {
      evictConnections[evictCount++] = connection;
    } else {
      break;
    }
  }
}
//获取要剔除的数据库连接数量
int removeCount = evictCount + keepAliveCount;
if (removeCount > 0) {
  //复制数组，将连接池中无需剔除的连接复制到连接的前面
  System.arraycopy(connections, removeCount, connections, 0, poolingCount - removeCount);
  //将连接池中后半部分值设置为null
  Arrays.fill(connections, poolingCount - removeCount, poolingCount, null);
  //连接池数量设置为实际的数量
  poolingCount -= removeCount;
}
```

> 上述代码逻辑实际上是将保活数组、剔除连接数组中的连接都从数据库连接池中剔除掉了，接下来会将剔除连接数组中的连接全部关闭销毁，保活数组中的连接校验有效性，如果无效则关闭剔除，如果有效则重新加入数据库连接池；

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)


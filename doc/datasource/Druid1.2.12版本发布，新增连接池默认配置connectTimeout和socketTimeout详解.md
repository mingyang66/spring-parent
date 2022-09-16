### Druid1.2.12版本发布，新增连接池默认配置connectTimeout和socketTimeout详解

##### 新版本特性如下

这个版本连接池默认增加配置connectTimeout和socketTimeout，增强了SQL Parser

1. 连接池DruidDataSource支持新的配置connectTimeout和socketTimeout，分别都是10秒。这个默认值会减少因为网络丢包时导致的连接池无法创建链接。
2. 修复连接池DruidDataSource#handleFatalError方法判断是否关闭逻辑不对的问题 [#4724](https://github.com/alibaba/druid/pull/4724)
3. 修复StatFilter统计Statement执行SQL只记录第一条SQL的问题 [#4921](https://github.com/alibaba/druid/issues/4921)
4. 修复ParameterizedOutputVisitorUtils#restore结果不对的问题 [#4532](https://github.com/alibaba/druid/pull/4532)
5. SQL Parser增强对PolarDB-X的支持 [#4927](https://github.com/alibaba/druid/issues/4927)
6. SQL Parser增强对Oceanbase的支持 [#4833](https://github.com/alibaba/druid/issues/4833)
7. SQL Parser增强对MySQL的支持 [#4916](https://github.com/alibaba/druid/issues/4916) [#4817](https://github.com/alibaba/druid/issues/4817) [#4825](https://github.com/alibaba/druid/pull/4825)
8. SQL Parser增强对Clickhouse的支持 [#4833](https://github.com/alibaba/druid/issues/4833) [#4881](https://github.com/alibaba/druid/pull/4881)
9. SQL Parser增强对DB2的支持 [#4838](https://github.com/alibaba/druid/pull/4838)
10. SQL Parser增强对Oracle的支持

##### 连接池connectTimeout默认配置

在源码com.alibaba.druid.pool.DruidAbstractDataSource#createPhysicalConnection()方法中新增了如下代码：

```java
        if (connectTimeout > 0) {
            if (isMySql) {
                physicalConnectProperties.put("connectTimeout", connectTimeout);
            } else if (isOracle) {
                physicalConnectProperties.put("oracle.net.CONNECT_TIMEOUT", connectTimeout);
            } else if (driver != null && "org.postgresql.Driver".equals(driver.getClass().getName())) {
                physicalConnectProperties.put("loginTimeout", connectTimeout);
                physicalConnectProperties.put("socketTimeout", connectTimeout);
            }
        }
```

> 对mysql、oracle、postgresql数据库驱动配置连接超时时间，在连接的时候生效，默认值：10000ms

##### 连接池socket-timeout默认配置

在数据库出现宕机或网络宜昌市，jdbc的socket超时是必须的，由于TCP/IP结构，socket没有办法检测到网络错误，因此应用程序也不能检测到与数据库之间的连接是否已经断开。如果没有socket超时，应用程序会一直等待数据库返回结果。为了避免死连接，socket必须设置超时时间，通过设置超时时间，可以防止出现网络错误时一直等待的情况并缩短故障时间。

在源码com.alibaba.druid.pool.DruidAbstractDataSource#createPhysicalConnection()中新增如下代码：

```java
            if (socketTimeout > 0 && !netTimeoutError) {
                try {
                  //默认sql执行10s
                    conn.setNetworkTimeout(netTimeoutExecutor, socketTimeout);
                } catch (SQLFeatureNotSupportedException | AbstractMethodError e) {
                    netTimeoutError = true;
                } catch (Exception ignored) {
                    // ignored
                }
            }
```

其中netTimeoutExecutor是SynchronousExecutor的实例对象

```java
    class SynchronousExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            try {
                command.run();
            } catch (AbstractMethodError error) {
                netTimeoutError = true;
            } catch (Exception ignored) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("failed to execute command " + command);
                }
            }
        }
    }
```

> SynchronousExecutor是在com.alibaba.druid.pool.DruidDataSource#init方法中进行初始化

其中command的具体实现是com.mysql.cj.jdbc.ConnectionImpl.NetworkTimeoutSetter（mysql的具体实现）

```java
    private static class NetworkTimeoutSetter implements Runnable {
        private final WeakReference<JdbcConnection> connRef;
        private final int milliseconds;

        public NetworkTimeoutSetter(JdbcConnection conn, int milliseconds) {
            this.connRef = new WeakReference(conn);
            this.milliseconds = milliseconds;
        }

        public void run() {
            JdbcConnection conn = (JdbcConnection)this.connRef.get();
            if (conn != null) {
                synchronized(conn.getConnectionMutex()) {
                    ((NativeSession)conn.getSession()).setSocketTimeout(this.milliseconds);
                }
            }

        }
    }
```



GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
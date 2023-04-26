### Druid1.2.13版本发布，修复连接池默认指定connectTimeout和socketTimeout导致的问题

> 在druid连接池上一个版本新增了两个配置属性connectTimeout和socketTimeout两个超时配置，但是这两个配置默认是int类型，在一些场景下不生效；

##### 一、DruidAbstractDataSource#createPhysicalConnection()中两个配置使用源码如下

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

        Connection conn = null;

        long connectStartNanos = System.nanoTime();
        long connectedNanos, initedNanos, validatedNanos;

        Map<String, Object> variables = initVariants
                ? new HashMap<String, Object>()
                : null;
        Map<String, Object> globalVariables = initGlobalVariants
                ? new HashMap<String, Object>()
                : null;

        createStartNanosUpdater.set(this, connectStartNanos);
        creatingCountUpdater.incrementAndGet(this);
        try {
            conn = createPhysicalConnection(url, physicalConnectProperties);
            connectedNanos = System.nanoTime();

            if (conn == null) {
                throw new SQLException("connect error, url " + url + ", driverClass " + this.driverClass);
            }

            initPhysicalConnection(conn, variables, globalVariables);
            initedNanos = System.nanoTime();

            if (socketTimeout > 0 && !netTimeoutError) {
                try {
                    conn.setNetworkTimeout(netTimeoutExecutor, socketTimeout);
                } catch (SQLFeatureNotSupportedException | AbstractMethodError e) {
                    netTimeoutError = true;
                } catch (Exception ignored) {
                    // ignored
                }
            }

```

> 其中connectTimeout和socketTimeout两个属性定义是int类型；在druid连接池中设置int类型的Properties属性没问题，但是在数据库驱动中就会有问题了；

##### 二、举例mysql数据库驱动时会触发的问题

找到mysql驱动com.mysql.cj.jdbc.NonRegisteringDriver#connect方法如下：

```java
public Connection connect(String url, Properties info) throws SQLException {
        if (!ConnectionUrl.acceptsUrl(url)) {
          return null;
        } else {
          //此方法中会读取Druid数据源设置到Properties中的connectionTimeout或socketTimeout
          ConnectionUrl conStr = ConnectionUrl.getConnectionUrlInstance(url, info);
          ...
        }
    }
```

读取Properteis系统属性

```java
   public static ConnectionUrl getConnectionUrlInstance(String connString, Properties info) {
        if (connString == null) {
            throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("ConnectionString.0"));
        } else {
            String connStringCacheKey = buildConnectionStringCacheKey(connString, info);
            rwLock.readLock().lock();
            ConnectionUrl connectionUrl = (ConnectionUrl)connectionUrlCache.get(connStringCacheKey);
            if (connectionUrl == null) {
                rwLock.readLock().unlock();
                rwLock.writeLock().lock();

                try {
                    connectionUrl = (ConnectionUrl)connectionUrlCache.get(connStringCacheKey);
                    if (connectionUrl == null) {
                        ConnectionUrlParser connStrParser = ConnectionUrlParser.parseConnectionString(connString);
                        connectionUrl = ConnectionUrl.Type.getConnectionUrlInstance(connStrParser, info);
                        connectionUrlCache.put(connStringCacheKey, connectionUrl);
                    }

                    rwLock.readLock().lock();
                } finally {
                    rwLock.writeLock().unlock();
                }
            }

            rwLock.readLock().unlock();
            return connectionUrl;
        }
    }

    private static String buildConnectionStringCacheKey(String connString, Properties info) {
        StringBuilder sbKey = new StringBuilder(connString);
        sbKey.append("§");
      //此处通过Properties的getProperties读取系统属性
        sbKey.append(info == null ? null : (String)info.stringPropertyNames().stream().map((k) -> {
            return k + "=" + info.getProperty(k);
        }).collect(Collectors.joining(", ", "{", "}")));
        return sbKey.toString();
    }
```

java.util.Properties#getProperty(java.lang.String)方法源码如下：

```java
    public String getProperty(String key) {
        Object oval = map.get(key);
       //此处定义了只有属性值为String类型时才有效，否则值为null
        String sval = (oval instanceof String) ? (String)oval : null;
        Properties defaults;
        return ((sval == null) && ((defaults = this.defaults) != null)) ? defaults.getProperty(key) : sval;
    }
```

>
通过上面的方法我们知道如果超时时间connectioinTimeout和socketTimeout的Properties属性值设置为int类型，则获取到的值是null，驱动将会忽略掉设置；上个版本的两个超时时间将会影响mysql、oracle、postgresql

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
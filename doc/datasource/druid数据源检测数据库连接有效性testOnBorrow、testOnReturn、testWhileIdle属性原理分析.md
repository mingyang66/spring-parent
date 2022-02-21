#### druid数据源检测数据库连接有效性testOnBorrow、testOnReturn、testWhileIdle属性原理分析

> druid多数据源建立连接后，可以通过配置对连接的有效性进行检查，想要更好的运用好数据库连接检查配置就应该了解源码，了解控制原理。

##### druid多数据源检测数据库连接的有效性属性配置如下：

```properties
#mysql默认使用ping模式,可以通过设置系统属性System.getProperties().setProperty("druid.mysql.usePingMethod", "false")更改为sql模式
#用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。如果validationQuery为null，testOnBorrow、testOnReturn、testWhileIdle都不会起作用。默认：SELECT 1
spring.emily.datasource.config.mysql.validation-query="SELECT 1"
#单位：秒，检测连接是否有效的超时时间。底层调用jdbc Statement对象的void setQueryTimeout(int seconds)方法，默认：-1
spring.emily.datasource.config.mysql.validation-query-timeout=-1
#申请连接时执行validationQuery检测连接是否有效，这个配置会降低性能。默认：false(如果test-on-borrow为true,那么test-while-idle无效)
spring.emily.datasource.config.mysql.test-on-borrow=false
#建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。默认：true
spring.emily.datasource.config.mysql.test-while-idle=true
#归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。默认：false
spring.emily.datasource.config.mysql.test-on-return=false
```

##### 获取数据库连接DruidPooledConnection对象入口代码com.alibaba.druid.pool.DruidDataSource#getConnection()

```java
    @Override
    public DruidPooledConnection getConnection() throws SQLException {
        return getConnection(maxWait);
    }

    public DruidPooledConnection getConnection(long maxWaitMillis) throws SQLException {
      //对数据库连接池进行初始化  
      init();

        if (filters.size() > 0) {
            FilterChainImpl filterChain = new FilterChainImpl(this);
            return filterChain.dataSource_connect(this, maxWaitMillis);
        } else {
            //获取数据库连接对象
            return getConnectionDirect(maxWaitMillis);
        }
    }

```

##### com.alibaba.druid.pool.DruidDataSource#init初始化方法会对ValidConnectionChecker接口的实现类进行初始化

```java
    private void initValidConnectionChecker() {
        if (this.validConnectionChecker != null) {
            return;
        }

        String realDriverClassName = driver.getClass().getName();
        if (JdbcUtils.isMySqlDriver(realDriverClassName)) {
            this.validConnectionChecker = new MySqlValidConnectionChecker();

        } else if (realDriverClassName.equals(JdbcConstants.ORACLE_DRIVER)
                || realDriverClassName.equals(JdbcConstants.ORACLE_DRIVER2)) {
            this.validConnectionChecker = new OracleValidConnectionChecker();

        } else if (realDriverClassName.equals(JdbcConstants.SQL_SERVER_DRIVER)
                   || realDriverClassName.equals(JdbcConstants.SQL_SERVER_DRIVER_SQLJDBC4)
                   || realDriverClassName.equals(JdbcConstants.SQL_SERVER_DRIVER_JTDS)) {
            this.validConnectionChecker = new MSSQLValidConnectionChecker();

        } else if (realDriverClassName.equals(JdbcConstants.POSTGRESQL_DRIVER)
                || realDriverClassName.equals(JdbcConstants.ENTERPRISEDB_DRIVER)
                || realDriverClassName.equals(JdbcConstants.POLARDB_DRIVER)) {
            this.validConnectionChecker = new PGValidConnectionChecker();
        }
    }
```

> 此方法分别对不同种类的数据库连接检测有效性的实现类进行初始化，接下来会在建立连接、归还链接过程中用到；

##### com.alibaba.druid.pool.DruidDataSource#getConnectionDirect方法是获取数据库连接的具体实现：

```java
    public DruidPooledConnection getConnectionDirect(long maxWaitMillis) throws SQLException {
        int notFullTimeoutRetryCnt = 0;
        for (;;) {
            // handle notFullTimeoutRetry
            DruidPooledConnection poolableConnection;
            try {
               //从数据库连接池中获取连接对象句柄
                poolableConnection = getConnectionInternal(maxWaitMillis);
            } catch (GetConnectionTimeoutException ex) {
                if (notFullTimeoutRetryCnt <= this.notFullTimeoutRetryCount && !isFull()) {
                    notFullTimeoutRetryCnt++;
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("get connection timeout retry : " + notFullTimeoutRetryCnt);
                    }
                    continue;
                }
                throw ex;
            }
						//如果开启了获取连接有效性检查，则进行有效性检查
            if (testOnBorrow) {
               //有效性检查
                boolean validate = testConnectionInternal(poolableConnection.holder, poolableConnection.conn);
                if (!validate) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("skip not validate connection.");
                    }

                    discardConnection(poolableConnection.holder);
                    continue;
                }
            } else {
                if (poolableConnection.conn.isClosed()) {
                    discardConnection(poolableConnection.holder); // 传入null，避免重复关闭
                    continue;
                }
								//如果testOnBorrow为false,且testWhileIdle为ture,则在判定连接在空闲时间大于timeBetweenEvictionRunsMillis（心跳检查时间）时才做判定，对性能损耗几乎为0
                if (testWhileIdle) {
                    final DruidConnectionHolder holder = poolableConnection.holder;
                    long currentTimeMillis             = System.currentTimeMillis();
                    long lastActiveTimeMillis          = holder.lastActiveTimeMillis;
                    long lastExecTimeMillis            = holder.lastExecTimeMillis;
                    long lastKeepTimeMillis            = holder.lastKeepTimeMillis;

                    if (checkExecuteTime
                            && lastExecTimeMillis != lastActiveTimeMillis) {
                        lastActiveTimeMillis = lastExecTimeMillis;
                    }

                    if (lastKeepTimeMillis > lastActiveTimeMillis) {
                        lastActiveTimeMillis = lastKeepTimeMillis;
                    }

                    long idleMillis                    = currentTimeMillis - lastActiveTimeMillis;

                    long timeBetweenEvictionRunsMillis = this.timeBetweenEvictionRunsMillis;

                    if (timeBetweenEvictionRunsMillis <= 0) {
                        timeBetweenEvictionRunsMillis = DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;
                    }
										//连接空闲时间大于等于守护线程中心跳间隔时间时才进行有效性检查
                    if (idleMillis >= timeBetweenEvictionRunsMillis
                            || idleMillis < 0 // unexcepted branch
                            ) {
                        boolean validate = testConnectionInternal(poolableConnection.holder, poolableConnection.conn);
                        if (!validate) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("skip not validate connection.");
                            }

                            discardConnection(poolableConnection.holder);
                             continue;
                        }
                    }
                }
            }

            if (removeAbandoned) {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                poolableConnection.connectStackTrace = stackTrace;
                poolableConnection.setConnectedTimeNano();
                poolableConnection.traceEnable = true;

                activeConnectionLock.lock();
                try {
                    activeConnections.put(poolableConnection, PRESENT);
                } finally {
                    activeConnectionLock.unlock();
                }
            }

            if (!this.defaultAutoCommit) {
                poolableConnection.setAutoCommit(false);
            }

            return poolableConnection;
        }
    }
```

##### com.alibaba.druid.pool.DruidAbstractDataSource#testConnectionInternal(com.alibaba.druid.pool.DruidConnectionHolder, java.sql.Connection)方法对从数据库中获取到的连接有效性进行检查

```java
    protected boolean testConnectionInternal(DruidConnectionHolder holder, Connection conn) {
        String sqlFile = JdbcSqlStat.getContextSqlFile();
        String sqlName = JdbcSqlStat.getContextSqlName();

        if (sqlFile != null) {
            JdbcSqlStat.setContextSqlFile(null);
        }
        if (sqlName != null) {
            JdbcSqlStat.setContextSqlName(null);
        }
        try {
            //validConnectionChecker属性是init方法中对各数据库驱动进行初始化的有效性检查实现类
            if (validConnectionChecker != null) {
               //对连接进行有效性检查
                boolean valid = validConnectionChecker.isValidConnection(conn, validationQuery, validationQueryTimeout);
               ...
    }
```

##### 对mysql数据源进行连接有效性检查实现类MySqlValidConnectionChecker源码分析

```java
public class MySqlValidConnectionChecker extends ValidConnectionCheckerAdapter implements ValidConnectionChecker, Serializable {

    public static final int DEFAULT_VALIDATION_QUERY_TIMEOUT = 1;
    public static final String DEFAULT_VALIDATION_QUERY = "SELECT 1";

    private static final long serialVersionUID = 1L;
    private static final Log  LOG              = LogFactory.getLog(MySqlValidConnectionChecker.class);

    private Class<?> clazz;
    private Method   ping;
    
    private boolean  usePingMethod = false;

    public MySqlValidConnectionChecker(){
        try {
            //8.0之前的mysql驱动
            clazz = Utils.loadClass("com.mysql.jdbc.MySQLConnection");
            if (clazz == null) {
               //8.0之后的驱动
                clazz = Utils.loadClass("com.mysql.cj.jdbc.ConnectionImpl");
            }

            if (clazz != null) {
               //获取pingInternal方法示例
                ping = clazz.getMethod("pingInternal", boolean.class, int.class);
            }

            if (ping != null) {
               //实际是使用ping模式检测连接有效性
                usePingMethod = true;
            }
        } catch (Exception e) {
            LOG.warn("Cannot resolve com.mysql.jdbc.Connection.ping method.  Will use 'SELECT 1' instead.", e);
        }

        configFromProperties(System.getProperties());
    }

    @Override
    public void configFromProperties(Properties properties) {
        if (properties == null) {
            return;
        }
				//根据系统属性设置判定连接有效性的方法
        String property = properties.getProperty("druid.mysql.usePingMethod");
        if ("true".equals(property)) {
            setUsePingMethod(true);
        } else if ("false".equals(property)) {
            setUsePingMethod(false);
        }
    }

    public boolean isUsePingMethod() {
        return usePingMethod;
    }

    public void setUsePingMethod(boolean usePingMethod) {
        this.usePingMethod = usePingMethod;
    }

    public boolean isValidConnection(Connection conn, String validateQuery, int validationQueryTimeout) throws Exception {
        if (conn.isClosed()) {
            return false;
        }
				
        if (usePingMethod) {
            if (conn instanceof DruidPooledConnection) {
                conn = ((DruidPooledConnection) conn).getConnection();
            }

            if (conn instanceof ConnectionProxy) {
                conn = ((ConnectionProxy) conn).getRawObject();
            }

            if (clazz.isAssignableFrom(conn.getClass())) {
                if (validationQueryTimeout <= 0) {
                    validationQueryTimeout = DEFAULT_VALIDATION_QUERY_TIMEOUT;
                }

                try {
                  //使用ping的方式判定连接有效性
                    ping.invoke(conn, true, validationQueryTimeout * 1000);
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof SQLException) {
                        throw (SQLException) cause;
                    }
                    throw e;
                }
                return true;
            }
        }

        String query = validateQuery;
        if (validateQuery == null || validateQuery.isEmpty()) {
            query = DEFAULT_VALIDATION_QUERY;
        }

        Statement stmt = null;
        ResultSet rs = null;
        try {
          
          //使用sql的方式校验连接的有效性
            stmt = conn.createStatement();
            if (validationQueryTimeout > 0) {
                stmt.setQueryTimeout(validationQueryTimeout);
            }
            rs = stmt.executeQuery(query);
            return true;
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }

    }

}
```

> 默认情况下是使用ping的方式判定连接的有效性，但是可以通过druid.mysql.usePingMethod系统属性修改为sql判定连接有效性模式。

##### com.alibaba.druid.pool.vendor.OracleValidConnectionChecker是oracle校验连接有效性的实现类

```java
public class OracleValidConnectionChecker extends ValidConnectionCheckerAdapter implements ValidConnectionChecker, Serializable {

    private static final long serialVersionUID     = -2227528634302168877L;


    private int               timeout              = 1;

    private String            defaultValidateQuery = "SELECT 'x' FROM DUAL";

    public OracleValidConnectionChecker(){
        configFromProperties(System.getProperties());
    }

    @Override
    public void configFromProperties(Properties properties) {
        if (properties == null) {
            return;
        }
				//可以通过系统属性方式修改超时时间
        String property = properties.getProperty("druid.oracle.pingTimeout");
        if (property != null && property.length() > 0) {
            int value = Integer.parseInt(property);
            setTimeout(value);
        }
    }

    public void setTimeout(int seconds) {
        this.timeout = seconds;
    }
		//采用sql模式校验有效性
    public boolean isValidConnection(Connection conn, String validateQuery, int validationQueryTimeout) throws Exception {
        if (validateQuery == null || validateQuery.isEmpty()) {
            validateQuery = this.defaultValidateQuery;
        }

        if (conn.isClosed()) {
            return false;
        }

        if (conn instanceof DruidPooledConnection) {
            conn = ((DruidPooledConnection) conn).getConnection();
        }

        if (conn instanceof ConnectionProxy) {
            conn = ((ConnectionProxy) conn).getRawObject();
        }

        if (validateQuery == null || validateQuery.isEmpty()) {
            return true;
        }

        int queryTimeout = validationQueryTimeout <= 0 ? timeout : validationQueryTimeout;

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            stmt.setQueryTimeout(queryTimeout);
            rs = stmt.executeQuery(validateQuery);
            return true;
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }
    }
}

```

> oracle采用的是sql校验连接有效性的模式，默认超时时间是1s，可以通过系统属性druid.oracle.pingTimeout修改超时时间，也可以通过validation-query-timeout属性配置修改超时时间。

另外连接池会在com.alibaba.druid.pool.DruidDataSource#recycle方法中回收连接的时候通过属性testOnReturn判定是否需要判定连接的有效性。

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)


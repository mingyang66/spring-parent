#### druid数据库连接池driverClassName驱动类为何可以不用配置

> 在使用druid数据库连接池时driverClassName数据库驱动程序类可以配置，也可以不配置程序都可以正常运行，缺省配置是如何正确找到驱动程序呢？

##### com.alibaba.druid.pool.DruidDataSource#init数据库连接池初始化方法中：

```java
   public void init() throws SQLException {
           ...
            initFromSPIServiceLoader();
						//解析驱动程序
            resolveDriver();

            ...
    }

```

##### com.alibaba.druid.pool.DruidDataSource#resolveDriver解析驱动程序类：

```java
 protected void resolveDriver() throws SQLException {
       //重点，如果驱动程序配置为空，走接下来的逻辑
        if (this.driver == null) {
           //驱动程序没有配置会根据url获取对应的驱动类
            if (this.driverClass == null || this.driverClass.isEmpty()) {
                this.driverClass = JdbcUtils.getDriverClassName(this.jdbcUrl);
            }

            if (MockDriver.class.getName().equals(driverClass)) {
                driver = MockDriver.instance;
            } else if ("com.alibaba.druid.support.clickhouse.BalancedClickhouseDriver".equals(driverClass)) {
                Properties info = new Properties();
                info.put("user", username);
                info.put("password", password);
                info.putAll(connectProperties);
                driver = new BalancedClickhouseDriver(jdbcUrl, info);
            } else {
                if (jdbcUrl == null && (driverClass == null || driverClass.length() == 0)) {
                    throw new SQLException("url not set");
                }
               //创建获取到的驱动程序对象
                driver = JdbcUtils.createDriver(driverClassLoader, driverClass);
            }
        } else {
            if (this.driverClass == null) {
                this.driverClass = driver.getClass().getName();
            }
        }
    }
```

> 此处创建驱动程序类之后会自动的注册java.sql.DriverManager#registerDriver(java.sql.Driver, java.sql.DriverAction)驱动类；

##### com.alibaba.druid.util.JdbcUtils#getDriverClassName根据url获取驱动程序：

```java
    public static String getDriverClassName(String rawUrl) throws SQLException {
        if (rawUrl == null) {
            return null;
        }
        
        if (rawUrl.startsWith("jdbc:derby:")) {
            return "org.apache.derby.jdbc.EmbeddedDriver";
        } else if (rawUrl.startsWith("jdbc:mysql:")) {
            if (mysql_driver_version_6 == null) {
                mysql_driver_version_6 = Utils.loadClass("com.mysql.cj.jdbc.Driver") != null;
            }

            if (mysql_driver_version_6) {
                return MYSQL_DRIVER_6;
            } else {
                return MYSQL_DRIVER;
            }
        } else if (rawUrl.startsWith("jdbc:log4jdbc:")) {
            return LOG4JDBC_DRIVER;
        } else if (rawUrl.startsWith("jdbc:mariadb:")) {
            return MARIADB_DRIVER;
        } else if (rawUrl.startsWith("jdbc:oracle:") //
                   || rawUrl.startsWith("JDBC:oracle:")) {
            return ORACLE_DRIVER;
        } else if (rawUrl.startsWith("jdbc:alibaba:oracle:")) {
            return ALI_ORACLE_DRIVER;
        } else if (rawUrl.startsWith("jdbc:oceanbase:")) {
            return OCEANBASE_DRIVER;
        } else if (rawUrl.startsWith("jdbc:microsoft:")) {
            return "com.microsoft.jdbc.sqlserver.SQLServerDriver";
        } else if (rawUrl.startsWith("jdbc:sqlserver:")) {
            return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        } else if (rawUrl.startsWith("jdbc:sybase:Tds:")) {
            return "com.sybase.jdbc2.jdbc.SybDriver";
        } else if (rawUrl.startsWith("jdbc:jtds:")) {
            return "net.sourceforge.jtds.jdbc.Driver";
        } else if (rawUrl.startsWith("jdbc:fake:") || rawUrl.startsWith("jdbc:mock:")) {
            return "com.alibaba.druid.mock.MockDriver";
        } else if (rawUrl.startsWith("jdbc:postgresql:")) {
            return POSTGRESQL_DRIVER;
        } else if (rawUrl.startsWith("jdbc:edb:")) {
            return ENTERPRISEDB_DRIVER;
        } else if (rawUrl.startsWith("jdbc:odps:")) {
            return ODPS_DRIVER;
        } else if (rawUrl.startsWith("jdbc:hsqldb:")) {
            return "org.hsqldb.jdbcDriver";
        } else if (rawUrl.startsWith("jdbc:db2:")) {
            // Resolve the DB2 driver from JDBC URL
            // Type2 COM.ibm.db2.jdbc.app.DB2Driver, url = jdbc:db2:databasename
            // Type3 COM.ibm.db2.jdbc.net.DB2Driver, url = jdbc:db2:ServerIP:6789:databasename
            // Type4 8.1+ com.ibm.db2.jcc.DB2Driver, url = jdbc:db2://ServerIP:50000/databasename
            String prefix = "jdbc:db2:";
            if (rawUrl.startsWith(prefix + "//")) { // Type4
                return DB2_DRIVER; // "com.ibm.db2.jcc.DB2Driver";
            } else {
                String suffix = rawUrl.substring(prefix.length());
                if (suffix.indexOf(':') > 0) { // Type3
                    return DB2_DRIVER3; // COM.ibm.db2.jdbc.net.DB2Driver
                } else { // Type2
                    return DB2_DRIVER2; // COM.ibm.db2.jdbc.app.DB2Driver
                }
            }
        } else if (rawUrl.startsWith("jdbc:sqlite:")) {
            return SQLITE_DRIVER;
        } else if (rawUrl.startsWith("jdbc:ingres:")) {
            return "com.ingres.jdbc.IngresDriver";
        } else if (rawUrl.startsWith("jdbc:h2:")) {
            return H2_DRIVER;
        } else if (rawUrl.startsWith("jdbc:mckoi:")) {
            return "com.mckoi.JDBCDriver";
        } else if (rawUrl.startsWith("jdbc:cloudscape:")) {
            return "COM.cloudscape.core.JDBCDriver";
        } else if (rawUrl.startsWith("jdbc:informix-sqli:")) {
            return "com.informix.jdbc.IfxDriver";
        } else if (rawUrl.startsWith("jdbc:timesten:")) {
            return "com.timesten.jdbc.TimesTenDriver";
        } else if (rawUrl.startsWith("jdbc:as400:")) {
            return "com.ibm.as400.access.AS400JDBCDriver";
        } else if (rawUrl.startsWith("jdbc:sapdb:")) {
            return "com.sap.dbtech.jdbc.DriverSapDB";
        } else if (rawUrl.startsWith("jdbc:JSQLConnect:")) {
            return "com.jnetdirect.jsql.JSQLDriver";
        } else if (rawUrl.startsWith("jdbc:JTurbo:")) {
            return "com.newatlanta.jturbo.driver.Driver";
        } else if (rawUrl.startsWith("jdbc:firebirdsql:")) {
            return "org.firebirdsql.jdbc.FBDriver";
        } else if (rawUrl.startsWith("jdbc:interbase:")) {
            return "interbase.interclient.Driver";
        } else if (rawUrl.startsWith("jdbc:pointbase:")) {
            return "com.pointbase.jdbc.jdbcUniversalDriver";
        } else if (rawUrl.startsWith("jdbc:edbc:")) {
            return "ca.edbc.jdbc.EdbcDriver";
        } else if (rawUrl.startsWith("jdbc:mimer:multi1:")) {
            return "com.mimer.jdbc.Driver";
        } else if (rawUrl.startsWith("jdbc:dm:")) {
            return JdbcConstants.DM_DRIVER;
        } else if (rawUrl.startsWith("jdbc:kingbase:")) {
            return JdbcConstants.KINGBASE_DRIVER;
        } else if (rawUrl.startsWith("jdbc:kingbase8:")) {
            return JdbcConstants.KINGBASE8_DRIVER;
        } else if (rawUrl.startsWith("jdbc:gbase:")) {
            return JdbcConstants.GBASE_DRIVER;
        } else if (rawUrl.startsWith("jdbc:xugu:")) {
            return JdbcConstants.XUGU_DRIVER;
        } else if (rawUrl.startsWith("jdbc:hive:")) {
            return JdbcConstants.HIVE_DRIVER;
        } else if (rawUrl.startsWith("jdbc:hive2:")) {
            return JdbcConstants.HIVE_DRIVER;
        } else if (rawUrl.startsWith("jdbc:phoenix:thin:")) {
            return "org.apache.phoenix.queryserver.client.Driver";
        } else if (rawUrl.startsWith("jdbc:phoenix://")) {
            return JdbcConstants.PHOENIX_DRIVER;
        } else if (rawUrl.startsWith("jdbc:kylin:")) {
            return JdbcConstants.KYLIN_DRIVER;
        } else if (rawUrl.startsWith("jdbc:elastic:")) {
            return JdbcConstants.ELASTIC_SEARCH_DRIVER;
        } else if (rawUrl.startsWith("jdbc:clickhouse:")) {
            return JdbcConstants.CLICKHOUSE_DRIVER;
        } else if(rawUrl.startsWith("jdbc:presto:")) {
            return JdbcConstants.PRESTO_DRIVER;
        } else if(rawUrl.startsWith("jdbc:trino:")) {
            return JdbcConstants.TRINO_DRIVER;
        } else if (rawUrl.startsWith("jdbc:inspur:")) {
            return JdbcConstants.KDB_DRIVER;
        } else if (rawUrl.startsWith("jdbc:polardb")) {
            return JdbcConstants.POLARDB_DRIVER;
        } else if (rawUrl.startsWith("jdbc:highgo:")) {
            return "com.highgo.jdbc.Driver";
        } else {
            throw new SQLException("unknown jdbc driver : " + rawUrl);
        }
    }
```



GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)


druid数据库连接池initial-size参数详解

> initial-size参数是数据库连接池初始化的时候连接初始化的数量；其会在应用程序第一次进行CRUD的时候进行初始化，所以初始化数量并不是越多越好，数量越多第一次操作数据库就会越慢；

##### initial-size参数是在com.alibaba.druid.pool.DruidDataSource#init方法进行第一次初始化时生效

```java
   public void init() throws SQLException {
        if (inited) {
            return;
        }

						...

            if (createScheduler != null && asyncInit) {
              //根据initial-size初始化物理连接
                for (int i = 0; i < initialSize; ++i) {
                    submitCreateTask(true);
                }
            } else if (!asyncInit) {
                // init connections
              //根据initial-size和连接池中的数量初始化物理连接
                while (poolingCount < initialSize) {
                    try {
                        PhysicalConnectionInfo pyConnectInfo = createPhysicalConnection();
                        DruidConnectionHolder holder = new DruidConnectionHolder(this, pyConnectInfo);
                        connections[poolingCount++] = holder;
                    } catch (SQLException ex) {
                        LOG.error("init datasource error, url: " + this.getUrl(), ex);
                        if (initExceptionThrow) {
                            connectError = ex;
                            break;
                        } else {
                            Thread.sleep(3000);
                        }
                    }
                }

                if (poolingCount > 0) {
                    poolingPeak = poolingCount;
                    poolingPeakTime = System.currentTimeMillis();
                }
            }

           ...
    }
```



GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
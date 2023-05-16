#### Mysql驱动数据类型YEAR默认是Date数据类型

> 在mysql数据库驱动中数据库中year类型的数据默认是被转换为了Date类型，而Date类型是yyyy-MM-dd日期类型。

##### 一、Java驱动中是如何将year类型判定为Date类型的？

```java
com.mysql.cj.jdbc.result.ResultSetMetaData#getColumnType

    @Override
    public int getColumnType(int column) throws SQLException {
        Field f = getField(column);
        if (f.getMysqlType() == MysqlType.YEAR && !this.treatYearAsDate) {
            return Types.SMALLINT;
        }
        return f.getJavaType();
    }
```

> 上述代码是判定Year类型是Date或者转换为SMALLINT类型；核心是treatYearAsDate属性，其实是属性yearIsDateType来控制的，yearIsDateType属性默认值是true;

##### 二、Java中如何修改Year的默认数据类型为int

配置数据库连接时配置上yearIsDateType参数修改默认转换数据类型

```Java
jdbc:mysql://127.0.0.1:3306/ocean_sky?characterEncoding=utf-8&rewriteBatchedStatements=true&yearIsDateType=false
```

参考：

[https://stackoverflow.com/questions/53600413/why-does-mysql-connector-tread-year4-as-date](https://stackoverflow.com/questions/53600413/why-does-mysql-connector-tread-year4-as-date)

[https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-type-conversions.html](https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-type-conversions.html)

[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

[https://github.com/mingyang66/SkyDb](https://github.com/mingyang66/SkyDb)
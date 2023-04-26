#### Java查询Mysql数据库Decimal类型小数位自动截取

>
最近发现一个有趣的问题，在oracle数据库中sql的select查询语句中的计算结果可以保留8位小数位返回到java客户端，而mysql只能返回五位小数，而且进行了四舍五入计算，如果涉及到钱的问题时就会发生多一分钱的问题，这是很严重的问题，所以必须想办法解决。

Oracle数据库中查询示例：

```sql
select 3635.95/6277 from dual
```

返回结果是：0.5792496415485104349211406722956826509479

mysql数据库中查询示例：

```sql
select 3635.95/6277 from dual
```

返回结果是：0.579250

>
上面示例两个数据库，同样的sql，返回的结果是完全不同的；这是又是什么原因导致这样的结果呢？个人猜测是不同的数据库小数位精度不同导致的（没有查到直接的证据，有查到的老铁还请分享下）；那我们又如何解决这个精度问题呢？经过不停的debug源代码发现，无论oracle还是mysql返回的数据类型都是DECIMAL类型，对应java数据类型是BigDecimal；那我们可以反推上述sql计算后的数据类型都是Decimal，只是不同的数据库保留的精度不同，所以我们应该想办法将其转换为定长浮点数。

将计算后的结果转换为定长浮点数：

```sql
select convert(3635.95/6277,decimal(15,8));
```

这样就解决了再不同数据库中由于默认精度的问题导致数据计算产生误差；

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
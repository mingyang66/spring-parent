Mybatis批处理学习笔记一

**一、Mybatis内置的ExecutorType有三种执行方式，分别是SIMPLE,REUSE,BATCH**

1. SIMPLE：是默认模式，该模式下会为每个sql语句的执行创建一个新的预处理语句，单条提交sql；
2. BATCH：会重复使用已经预处理的语句，并且批量执行所有更新语句，显然batch的性能更高；但是Batch模式也有自己的缺点，比如:insert插入操作是，在事务还未提交之前，是没有办法获取到自增的id;
3. REUSE：会复用预处理SQL语句

**二、在看实验案例之前先看下批处理测试结果比较**

|       模式        |                     10000条批量插入耗时                     | 性能比对 |
| :---------------: | :---------------------------------------------------------: | :------: |
| SIMPLE(逐条插入)  | 16315/11749/11859/11746/11409/11663/11305/11652/11449/10631 |   最差   |
| SIMPLE（foreach） |           425/390/430/364/409/381/354/339/350/361           |   良好   |
|       BATCH       |           201/298/237189/212/232/213/308/202/167            |   最佳   |

#### 三、实验案例（基于mysql数据库测试）

mybatis数据库xml配置

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.emily.infrastructure.test.mapper.ItemMapper">

	<insert id="inertByBatch" parameterType="java.util.List">
		insert into item values
		<foreach collection="list" item="item" index="" separator=",">
			(#{item.scheName},#{item.lockName})
		</foreach>
	</insert>
	<insert id="insertItem" parameterType="java.lang.String">
		insert into item values(#{scheName},#{lockName})
	</insert>
</mapper>
```

批量处理控制器及方法（此处写法不规范，只为了测试）

```java
    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    /**
     * foreach 模式批量插入数据库
     * @param num
     * @return
     */
    @GetMapping("batchSimple/{num}")
    @TargetDataSource("mysql")
    public long batchSimple(@PathVariable Integer num) {
        List<Item> list = Lists.newArrayList();
        for (int i = 0; i < num; i++) {
            Item item = new Item();
            item.setLockName("a" + i);
            item.setScheName("B" + i);
            list.add(item);
        }
        long start = System.currentTimeMillis();
        itemMapper.inertByBatch(list);
        return System.currentTimeMillis() - start;
    }

    /**
     * batch模式批量插入数据库
     * @param num
     * @return
     */
    @GetMapping("batch/{num}")
    @TargetDataSource("mysql")
    public long getBatch(@PathVariable Integer num) {
        long start = System.currentTimeMillis();
        SqlSessionFactory sqlSessionFactory = sqlSessionTemplate.getSqlSessionFactory();
        SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false);
        try {
            ItemMapper mysqlMapper = sqlSession.getMapper(ItemMapper.class);
            for (int i = 0; i < num; i++) {
                Item item = new Item();
                item.setLockName("a" + i);
                item.setScheName("B" + i);
                mysqlMapper.insertItem(item.getScheName(), item.getLockName());
                if(i%1000==0){
                    // 手动提交，提交后无法回滚
                    sqlSession.commit();
                }
            }
            // 手动提交，提交后无法回滚
            sqlSession.commit();
            // 清理缓存，防止溢出
            sqlSession.clearCache();
        } catch (Exception exception) {
            // 没有提交的数据可以回滚
            sqlSession.rollback();
        } finally {
            sqlSession.close();
        }
        return System.currentTimeMillis() - start;
    }

    /**
     * 逐条插入数据库
     * @param num
     * @return
     */
    @GetMapping("insertItem/{num}")
    @TargetDataSource("mysql")
    public long insertItem(@PathVariable Integer num) {
        long start = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            Item item = new Item();
            item.setLockName("a" + i);
            item.setScheName("B" + i);
            itemMapper.insertItem(item.getScheName(), item.getLockName());
        }
        return System.currentTimeMillis() - start;
    }
```

使用mysql做批处理验证时刚开始效率比foreach模式慢了很多，感觉batch批处理的性能不应该这么lj，经过一段时间摸索发现需要在url后面加上rewriteBatchedStatements=true参数后速度瞬间提升；

```
jdbc:mysql://127.0.0.1:3306/sgrain?characterEncoding=utf-8&rewriteBatchedStatements=true
```



> MYSQL的JDBC连接的url中需要加上rewriteBatchedStatements=true参数，mysql驱动默认情况下会把批量提交的sql拆分成一条一条的sql执行，导致批量插入变成了单条插入，造成了性能低下，只有把rewriteBatchedStatements=true参数加上，驱动才会帮我们批量执行sql，另外BATCH模式只对INSERT/UPDATE/DELETE有效；

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
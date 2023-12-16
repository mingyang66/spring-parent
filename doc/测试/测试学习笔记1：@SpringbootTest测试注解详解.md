#### 测试学习笔记1：@SpringbootTest测试注解详解

> @SpringBootTest是一个用于在springboot应用程序中启动完整应用的测试环境注解。它主要用于集成测试，可以启动一个嵌入式的数据库，加载完整的spring上下文，并自动装配测试类的ApplicationContext。

以下是@SpringBootTest的关键特性：

- 自动配置上下文环境：@SpringBootTest能够根据测试类所在的包扫描应用程序组件，并自动配置一个完整的Spring上下文；
- 自动配置Mock服务：@SpringBootTest自动为应用程序中的服务提供mock实现，这使得能够不依赖真实服务的情况下进行测试；
- 自动配置嵌入式数据库：@SpringBootTest自动配置一个嵌入式数据库，如：H2、HSQL或Derby，者能够使我们在不依赖外部数据库的情况下进行测试；
- 自动配置切面或拦截器：@SpringBootTest自动应用一系列的切面或拦截器，以便在在测试期间记录或验证应用程序的行为。

下面的简单案例展示如何通过@SpringBootTest注解完成简单的单元测试：

定义一个service类：

```java
@Service
public class MysqlServiceImpl implements MysqlService {
    @Autowired
    private MysqlMapper mysqlMapper;

    @Override
    public List<World> getMysql() {
        return mysqlMapper.getMysql("田晓霞", "123456");
    }
}
```

定义一个单元测试类：

```java
@SpringBootTest
public class MysqlServiceTest {
    @Autowired
    private MysqlServiceImpl mysqlService;
    @MockBean
    private MysqlMapper mysqlMapper;

    @Test
    public void getMysql() {
        World world = new World();
        world.password = "123";
        when(mysqlMapper.getMysql("田晓霞", "123456")).thenReturn(Lists.newArrayList(world));
        List<World> list = mysqlService.getMysql();
        Assertions.assertEquals(list.get(0).password, "123");
    }
}
```

> 在此案例中，spring boot会启动一个完整的spring应用上下文，并自动装配MysqlServiceTest类中所有的@Autowired类，通过@MockBean注解模拟真实需要的MysqlMapper实例对象，通过when、thenReturn模拟返回数据，避免调用真实的数据库实现。

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
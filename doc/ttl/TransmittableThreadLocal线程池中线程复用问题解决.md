#### TransmittableThreadLocal线程池中线程复用问题解决

> 对于TL、ITL、TTL网上有很多的介绍，对于源码的分析都很全，大家可以参考[小伙伴同学们写的 TTL实际业务使用场景 与 设计实现解析的文章](https://github.com/alibaba/transmittable-thread-local/issues/123)；

TTL对线程或者线程池的核心通过装饰器模式做了处理，核心如下：

- capture：获取父线程中的值，包括引用对象或普通对象
- replay：回放：备份、将父线程的值设置到子线程
- restore：将子线程执行之前backup的值设置回子线程的ThreadLocal

##### 一、TTL在线程池场景

- 线程池复用线程，如果子线程执行完未移除上下文，则会导致后续线程可以取到之前线程设置的属性

```java
public class TtlErrorTest {
    private static final TransmittableThreadLocal<User> context = new TransmittableThreadLocal<>();
    
    private static final ExecutorService service = Executors.newSingleThreadExecutor();
  //TTL修饰过的线程池（1）
//  private static final Executor service = TtlExecutors.getTtlExecutor(Executors.newSingleThreadExecutor());

    public static void main(String[] args) {
      	//主线程设置上下文变量（3）
        //User user1 = new User();
        //user1.setUsername("孙少平");
        //user1.setPassword("123456");
        //context.set(user1);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                User user = new User();
                user.setUsername("田晓霞");
                context.set(user);
                System.out.println("1-" + Thread.currentThread().getName() + ":" + context.get());
                //移除上下文变量（2）
                //context.remove();
            }
        };
        Runnable runnable1 = new Runnable() {
            @Override
            public void run() {
                System.out.println("2-" + Thread.currentThread().getName() + ":" + context.get());
            }
        };
        service.execute(runnable);
        service.execute(runnable1);
       //移除上下文(4)
        //ntext.remove();
    }
}
```

结果一：

```java
1-pool-1-thread-1:田晓霞
2-pool-1-thread-1:田晓霞
```

结果二（将代码中的（1）（3）打开运行）：

```java
1-pool-1-thread-1:田晓霞
2-pool-1-thread-1:孙少平
```



> 分析：上述案例定义了只有一个工作线程执行队列中线程的线程池，这样做是为了立马复现线程复用的效果；第一个线程中第一了上下文值引用对象，第二个线程之中没有定义，效果是第二个线程获取到了第一个线程之中设置的上下文变量；如果将第一个线程中（2）注释打开，则第二个线程获取不到第一个线程中设置的上下文变量；
>
> 如果将（1）打开会是什么效果，效果是第二个线程拿不到第一个线程中设置的上下文值，这又是为什么呢？在第一个线程中又没有主动remove掉为何使用了TTL修饰的线程池就拿不到符合预期了呢？
>
> 如果仔细分析源码会发现TTL修饰的线程使用装饰器模式，第一步capture捕获主线程上下文，第二部replay回放、backup备份子线程上下文、将主线程上下文值设置到子线程，第三部restore恢复阶段、将backup备份恢复到子线程上下文，因为子线程上下文执行没有任何值，restore恢复后上下文是干净的，上下文没有任何值；那在第二个线程复用线程的时候是取不到指的，符合预期；
>
> 如果将（1）（3）打开运行结果也是符合预期的，这里面有个问题，无论线程一、线程二执行完成后线程池中复用的线程都会持有一个User对象的引用，这样GC的时候就无法回收资源，如果线程池持有的引用对象足够多，引用对象占用的内存足够大的时候就有可能引发OOM异常；
>
> 如果将代码（4）打开则会移除主线程的上下文，但是子线程从父线程继承的上下文属性是无法移除的；





##### 二、TTL如何保证线程池中复用的线程不持有其它线程的属性值

针对上述线程池复用可能导致内存OOM的问题提供了两种解决方案，参考[https://github.com/alibaba/transmittable-thread-local/issues/521](https://github.com/alibaba/transmittable-thread-local/issues/521)

- 方案一：通过TtlExecutors.getDefaultDisableInheritableThreadFactory()在线程池中创建禁止继承父线程上下文的线程

```java
public class TtlFactoryTest {
    private static final TransmittableThreadLocal<User> context = new TransmittableThreadLocal<>();

    static final ExecutorService service = Executors.newFixedThreadPool(2, TtlExecutors.getDefaultDisableInheritableThreadFactory());

    public static void main(String[] args) {
        User user = new User();
        user.setUsername("田晓霞");
        context.set(user);
        System.out.println("1-" + Thread.currentThread().getName() + "-" + context.get());
        service.submit(new Runnable() {
            @Override
            public void run() {
                User user1 = new User();
                user1.setUsername("田二");
                context.set(user1);
                System.out.println("2-" + Thread.currentThread().getName() + "-" + context.get());
            }
        });
        System.out.println("3-" + Thread.currentThread().getName() + "-" + context.get());
    }
}
```



- 方案二：去除掉父子线程的继承关系，相当于TTL由InheritableThreadLocal回退到了ThreadLocal

```java
TransmittableThreadLocal<String> t1 = new TransmittableThreadLocal<String>() {
    protected String childValue(String parentValue) {
        return initialValue();
    }
}
```

> 此方法是去除掉父子线程之间的继承关系，会将TTL回退到ThreadLocal，对于非线程池的父子线程继承关系是不适用的；



GitHub源码：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
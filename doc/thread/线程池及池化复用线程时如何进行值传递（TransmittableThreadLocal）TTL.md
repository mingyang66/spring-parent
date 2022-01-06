#### 线程池及池化复用线程时如何进行值传递（TransmittableThreadLocal）TTL

##### 一、如何解决线程池中的值传递功能？

- 多个线程之间竞争同一个变量，为了线程安全进行值隔离，可以使用ThreadLocal
- 父子线程之间的值传递，可以使用InheritableThreadLocal类来实现。
- 在遇到线程池等会池化复用线程的执行组件情况下，上述两种方案都会失灵，就需要通过TransmittableThreadLocal类来实现。

##### 二、使用TtlRunnable修饰传入线程池的Runnable

```java
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        TransmittableThreadLocal<String> context = new TransmittableThreadLocal<>();
        context.set("set parent value");
        Runnable ttlRunnable = TtlRunnable.get(new Runnable() {
            @Override
            public void run() {
                //读取父线程中的值，其值为：set parent value
                System.out.println(context.get());
            }
        });
        executorService.submit(ttlRunnable);
        //读取当前线程中的值，其值为：set parent value
        System.out.println(context.get());
```

##### 三、使用TtlCallable来修饰传入线程池的Callable

```java
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        TransmittableThreadLocal<String> context = new TransmittableThreadLocal<>();
        context.set("set parent value");
        Callable<String> ttlCallable = TtlCallable.get(new Callable<String>() {
            @Override
            public String call() throws Exception {
                //读取父线程中的值，其值为：set parent value
                System.out.println(context.get());
                return "emily";
            }
        });
        Future<String> future = executorService.submit(ttlCallable);
        //读取当前线程中的值，其值为：set parent value
        System.out.println(context.get());
        System.out.println(future.get());
```

##### 四、可以直接使用TtlExecutors修饰线程池

```java
ExecutorService executorService = TtlExecutors.getTtlExecutorService(Executors.newFixedThreadPool(1));
```

##### 五、CompletableFuture通过TtlWrappers.wrap支持线程池的值传递

```java
        System.out.println("-----上下文ID:" + TraceContextHolder.get().getTraceId());
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(TtlWrappers.wrap(() -> {
            System.out.println("子线程1：" + Thread.currentThread().getName() + ":" + TraceContextHolder.get().getTraceId());
            return TraceContextHolder.get().getTraceId();
        }));
        String future = future1.get();
```

> 其TtlWrappers.wrap方法在2.12.4版本中已经标记为过期，可以通过TtlWrappers.wrapSupplier方法代替

##### 六、ThreadPoolTaskExecutor支持线程池的值传递

```java
 ThreadPoolTaskExecutor poolExecutor = ThreadPoolHelper.threadPoolTaskExecutor();
        poolExecutor.submit(TtlRunnable.get(new Runnable() {
            @Override
            public void run() {

            }
        }));
```

##### 七、ForkJoinPool支持线程池值传递

```java
      ForkJoinPool pool = ForkJoinPool.commonPool();
        pool.execute(TtlRunnable.get(new Runnable() {
            @Override
            public void run() {

            }
        }));
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)


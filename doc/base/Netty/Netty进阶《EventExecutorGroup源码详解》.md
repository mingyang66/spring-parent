#### Netty进阶《EventExecutorGroup源码详解》

EventExecutorGroup继承了JDK的ScheduledExecutroService，那么它就拥有了执行定时任务，执行提交的普通任务；

EventExecutorGroup还继承了JDK的Iterable接口，表示EventExecutorGroup是可遍历的，它的遍历对象是EventExecutor;

EventExecutorGroup继承关系图：

![在这里插入图片描述](https://img-blog.csdnimg.cn/481914a80087493b9d6d9e373e7ddc77.png)

##### 一、EventExecutorGroup中有两个Iterable相关的方法：

```java
    //返回一个被当前EventExecutorGroup管理的EventExecutor对象
		EventExecutor next();
    //返回一个EventExecutor类型的迭代器
    @Override
    Iterator<EventExecutor> iterator();
```

##### 二、接口Executor提供了一个提交执行任务的方法execute

```java
public interface Executor {
   //将需要执行的任务command提交到线程池、或者一个新的线程、或者直接执行
    void execute(Runnable command);
}
```

##### 三、Executor的子接口ExecutorService

```java
public interface ExecutorService extends Executor {

    /**
     * 启动有序关闭，在关闭过程中会继续执行以前提交的任务，但是不接受新的任务
     */
    void shutdown();

    /**
     * 尝试停止所有正在执行的任务，停止处理等待的任务，并返回一个等待执行的任务列表
     */
    List<Runnable> shutdownNow();

    /**
     * 如果此执行程序已关闭，则返回true
     */
    boolean isShutdown();

    /**
     * 如果关闭后所有任务都已完成，则返回true
     * 只有首先调用了shutdown()、shutdownNow()方法才有可能返回true，否则一定为false
     */
    boolean isTerminated();

    /**
     * 阻塞，shutdown后所有任务执行完成、超时、当前线程被终端，那个先发生那个优先；
     */
    boolean awaitTermination(long timeout, TimeUnit unit)
        throws InterruptedException;

    /**
     * 提交一个带返回值的执行任务，并返回一个Future代表将要返回的任务结果
     */
    <T> Future<T> submit(Callable<T> task);

    /**
     * 提交一个可运行任务以供执行，并返回一个表示该任务的Future。Future的get方法将在成功完成后返回给定的结果。
     */
    <T> Future<T> submit(Runnable task, T result);

    /**
     * 提交一个可运行任务以供执行，并返回一个表示该任务的Future。Future的get方法在成功完成后将返回null。
     */
    Future<?> submit(Runnable task);

    /**
     * 执行给定的多个任务，返回一个持有执行状态和结果的Future列表。
     */
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
        throws InterruptedException;

    /**
     * 执行给定的多个任务，返回一个持有执行状态和结果的Future列表。
     * 当所有的任务完成或超时过期Future#isDone都将返回ture
     */
    <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                  long timeout, TimeUnit unit)
        throws InterruptedException;

    /**
     * 执行给定的多个任务，返回其中一个执行成功的结果
     */
    <T> T invokeAny(Collection<? extends Callable<T>> tasks)
        throws InterruptedException, ExecutionException;

    /**
     * 执行给定的多个任务，返回其中一个执行成功的结果
     */
    <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                    long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}

```

下面是一个网络服务案例，其中线程池中的线程为传入请求提供服务，它使用预先配置的Executors.newFixedThreadPool工厂方法：

```java
class NetworkService implements Runnable {
    private final ServerSocket serverSocket;
    private final ExecutorService pool;

    public NetworkService(int port, int poolSize) throws IOException {
        serverSocket = new ServerSocket(port);
        pool = Executors.newFixedThreadPool(poolSize);
    }

    public void run() { // run the service      
        try {
            for (; ; ) {
                pool.execute(new Handler(serverSocket.accept()));
            }
        } catch (IOException ex) {
            pool.shutdown();
        }
    }
}

class Handler implements Runnable {
    private final Socket socket;

    Handler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        // read and service request on socket   
    }
}
```

下面的方法关闭ExecutorService分两个阶段，首先通过调用shutdown方法拒绝新任务进来，然后调用shutdownNow，如果有必要取消任何延迟任务：

```java
    void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted    
        try {
            // Wait a while for existing tasks to terminate      
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks        
                // Wait a while for tasks to respond to being cancelled      
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted      
            pool.shutdownNow();      // Preserve interrupt status     
            Thread.currentThread().interrupt();
        }
    }
```

##### 四、ExecutorService的子接口ScheduledExecutorService

ScheduledExecutorService接口提供了四个新的方法schedule延迟指定的时间执行任务，scheduleAtFixedRate方法延迟指定时间、按照固定的时间频率执行任务。

```java
public interface ScheduledExecutorService extends ExecutorService {

    /**
     * 提交一个一次性任务，该任务在给定延迟后变为启用状态。
     *
     * @param command 要执行的任务
     * @param delay 从现在开始延迟执行的时间
     * @param unit delay参数的时间单位
     * @return 一个ScheduledFuture，标识任务的挂起完成，其get方法将在完成时返回null
     */
    public ScheduledFuture<?> schedule(Runnable command,
                                       long delay, TimeUnit unit);

    /**
     * 提交一个带返回值的一次性任务，该任务在给定延迟后变为启用状态
     *
     * @param callable 要执行的函数任务
     * @param delay 从现在开始延迟执行的时间
     * @param unit delay参数的时间单位
     * @param <V> the type of the callable's result
     * @return 可用于提取结果或取消的ScheduledFuture
     */
    public <V> ScheduledFuture<V> schedule(Callable<V> callable,
                                           long delay, TimeUnit unit);

    /**
     * 提交一个可执行任务，延迟指定时间，然后按照period时间间隔执行任务
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command,
                                                  long initialDelay,
                                                  long period,
                                                  TimeUnit unit);

    /**
     * 提交一个可执行任务，延迟指定时间，然后按照period时间间隔执行任务
     */
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command,
                                                     long initialDelay,
                                                     long delay,
                                                     TimeUnit unit);

}
```



##### 五、ScheduledExecutorService的子接口EventExecutorGroup

EventExecutorGroup新增了三个方法：

- isShuttingDown：当且仅当此EventExecutorGroup管理的所有EventExecutor正在正常关闭或已关闭时，返回true
- shutdownGracefully：指定了超时时间的优雅关闭方法；



源码参考：[https://github.com/mingyang66/SkyDb](https://github.com/mingyang66/SkyDb)

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
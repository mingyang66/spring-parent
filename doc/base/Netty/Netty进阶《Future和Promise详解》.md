#### Netty进阶《Future和Promise详解》

##### 一、java.util.concurrent.Future源码解析

> java.util.concurrent.Future代表异步计算的结果，是JDK自带接口；提供了检查计算是否完成、等待计算完成以及检索计算结果的方法，只有当计算完成时，才能使用get方法获取结果，必要时进行阻塞，直到它准备好为止。通过cancel方法执行取消，额外提供了其它方法来确定任务时正常完成还是被取消，一旦计算完成，就不能取消计算。如果为了可取消性而想使用Future,但不提供可用的结果，则可以声明Future<?>形式的类型并且作为基础任务的结果返回null。

示例用法（源码提供）：

```java
interface ArchiveSearcher {
    String search(String target);
}

class App {
    ExecutorService executor = ...
    ArchiveSearcher searcher = ...

    void showSearch(String target) throws InterruptedException {
        Callable<String> task = () -> searcher.search(target);
        Future<String> future = executor.submit(task);
        displayOtherThings(); // do other things while searching      
        try {
            displayText(future.get()); // use future      
        } catch (ExecutionException ex) {
            cleanup();
            return;
        }
    }
}
```

FutureTask类是Future、Runnable接口的一种实现，因此可以被Executor执行，例如：上面submit提交方法可以用下面的代码替换：

```java
 FutureTask<String> future = new FutureTask<>(task);  
 executor.execute(future);
```



```java
public interface Future<V> {

    /**
     * 尝试关闭执行中的任务，如果任务已经执行完成，则尝试将会失败，
     * @param mayInterruptIfRunning {@code true} 如果任务正在执行，是否应该中断任务
     */
    boolean cancel(boolean mayInterruptIfRunning);

    /**
     * 如果此任务在正常完成之前被取消，则返回true
     */
    boolean isCancelled();

    /**
     * 如果当前任务完成，返回true
     */
    boolean isDone();

    /**
     * 如果需要，等待计算完成，然后获取其结果
     * @return 计算结果
     * @throws CancellationException 如果计算被取消
     * @throws ExecutionException 如果计算抛出异常
     * @throws InterruptedException 如果等待时当前线程被打断
     */
    V get() throws InterruptedException, ExecutionException;

    /**
     * 如果需要，最多等待给定的时间以完成计算，然后获取其结果。
     * @param timeout 最大等待时间
     * @param unit 时间单位
     * @return 计算结果
     * @throws CancellationException 如果计算被取消
     * @throws ExecutionException 如果计算抛出异常
     * @throws InterruptedException 如果在等待时当前线程被打断
     * @throws TimeoutException 如果等待时间超时
     */
    V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException;
}
```

##### 二、io.netty.util.concurrent.Future源码解析

> 异步操作结果

```java
public interface Future<V> extends java.util.concurrent.Future<V> {

    /**
     * 当且仅当I/O操作完成时，返回true
     */
    boolean isSuccess();

    /**
     * 当且仅当可以通过cancel方法取消操作时，返回true
     */
    boolean isCancellable();

    /**
     * 如果I/O操作失败，则返回I/O操作失败的原因。
     */
    Throwable cause();

    /**
     * 添加指定的监听器到Future,当future异步计算完成会通知指定的监听器。
     */
    Future<V> addListener(GenericFutureListener<? extends Future<? super V>> listener);

    /**
     * 添加指定的多个监听器到Future,当future异步计算完成会通知指定的监听器。
     */
    Future<V> addListeners(GenericFutureListener<? extends Future<? super V>>... listeners);

    /**
     * 删除future异步计算中第一次出现的监听器，被删除的监听器在future异步计算完成后将不会被通知
     */
    Future<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener);

    /**
     * 删除future异步计算中前面出现的多个监听器，被删除的监听器在future异步计算完成后将不会被通知
     */
    Future<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... listeners);

    /**
     * 等待future异步计算完成，如果future异步计算失败，则抛出失败原因
     */
    Future<V> sync() throws InterruptedException;

    /**
     * 等待future异步计算完成，如果future异步计算失败，则抛出失败原因
     */
    Future<V> syncUninterruptibly();

    /**
     * 等待future异步计算完成
     */
    Future<V> await() throws InterruptedException;

    /**
     * 等待future异步计算顺利完成，此方法如果捕获InterruptedException异常将会默认丢弃
     */
    Future<V> awaitUninterruptibly();

    /**
     * 在指定的时间内等待future异步计算完成
     */
    boolean await(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * 在指定的时间内等待future异步计算完成
     */
    boolean await(long timeoutMillis) throws InterruptedException;

    /**
     * 在指定的时间内等待future异步计算完成，如果发生InterruptedException异常将会默默丢弃掉
     */
    boolean awaitUninterruptibly(long timeout, TimeUnit unit);

    /**
     * 在指定的时间内等待future异步计算完成，如果发生InterruptedException异常将会默默丢弃掉
     */
    boolean awaitUninterruptibly(long timeoutMillis);

    /**
     * 无阻塞返回结果，如果future异步计算还未完成，则返回null
     */
    V getNow();

    /**
     * {@inheritDoc}
     *
     * 如果取消任务成功，future异步计算将会抛出CancellationException异常
     */
    @Override
    boolean cancel(boolean mayInterruptIfRunning);
}

```

##### 三、io.netty.channel.ChannelFuture源码解析

ChannelFuture是异步Channel I/O操作的结果。

Netty中的所有I/O操作都是异步的，这就意味着任何I/O操作都将立即返回，并且不能保证I/O操作在调用结束时已经完成，将会返回一个代表I/O操作结果或状态信息的ChannelFuture实例。

ChannelFuture代表完成或未完成的异步计算，当一个I/O操作开始时，将会创建一个future实例对象。这个新的future对象是未完成初始化的，它是处于即未完成、失败，也没有被关闭的状态，因为I/O操作还未完成。如果I/O操作完成，并且成功、或者失败、或者被关闭任务，future异步计算将会被更具体的信息标记，例如故障的原因。请注意，即使失败和取消也属于已完成状态。



```java
                          +---------------------------+
                                       | Completed successfully    |
                                       +---------------------------+
                                  +---->      isDone() = true      |
  +--------------------------+    |    |   isSuccess() = true      |
  |        Uncompleted       |    |    +===========================+
  +--------------------------+    |    | Completed with failure    |
  |      isDone() = false    |    |    +---------------------------+
  |   isSuccess() = false    |----+---->      isDone() = true      |
  | isCancelled() = false    |    |    |       cause() = non-null  |
  |       cause() = null     |    |    +===========================+
  +--------------------------+    |    | Completed by cancellation |
                                  |    +---------------------------+
                                  +---->      isDone() = true      |
                                       | isCancelled() = true      |
                                       +---------------------------+
```

ChannelFuture提供了各种方法，可以检查I/O操作是否已完成，等待完成，并检索I/O操作的结果。它还允许您添加ChannelFutureListener监听器，以便在I/O操作完成是受到通知。首选是addListener(GenericFutureListener) 而不是await()方法。

建议尽可能选择addListener（GenericFutureListener）而不是await()，以便在I/O操作完成时得到通知并执行任何后续任务。

addListener(GenericFutureListener) 是非阻塞的。它只需要将指定的ChannelFutureListener添加到ChannelFuture中，当I/O操作关联的future异步计算完成时将会通知监听器。ChannelFutureListener产生了最佳的性能和资源利用率，因为它根本不阻塞。但是如果您不习惯事件驱动的编程，那么实现顺序逻辑可能会很棘手。

相比之下，await()是一个阻塞操作。一旦被调用，调用方线程就会阻塞，直到操作完成。使用await()更容易实现顺序逻辑，但是调用方线程在I/O操作完成之前会产生线程不必要的阻塞，并且线程间通知的成本相对较高。此外在特定情况下可能会出现死锁，如下所述。不要在ChannelHandler内部调用await()。

ChannelHandler中的事件处理程序方法通常由I/O线程调用。如果await()被事件处理程序调用，也就是被I/O操作调用的事件处理程序，I/O操作可能永远不会完成，因为await()可以阻塞它所调用的时间处理程序，这是一个死锁。

```java
  // BAD - NEVER DO THIS
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
      ChannelFuture future = ctx.channel().close();
      future.awaitUninterruptibly();
      // Perform post-closure operation
      // ...
  }
 
  // GOOD
  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) {
      ChannelFuture future = ctx.channel().close();
      future.addListener(new ChannelFutureListener() {
          public void operationComplete(ChannelFuture future) {
              // Perform post-closure operation
              // ...
          }
      });
  }
```

尽管存在上述缺点，但是在某些情况下调用await()更方便。在这种情况下，请确保不要在I/O线程中调用await()。否则，将引发BlockingOperationException以防止死锁。

不要混淆I/O超时和await等待超时。

使用 await(long), await(long, TimeUnit), awaitUninterruptibly(long), 或 awaitUninterruptibly(long, TimeUnit) 指定的超时值与I/O超时完全无关。如果I/O操作超时，则未来将标记为“已完成但出现故障”，例如：应该通过特定于传输的选项配置连接超时：

```java
// BAD - NEVER DO THIS
  Bootstrap b = ...;
  ChannelFuture f = b.connect(...);
  f.awaitUninterruptibly(10, TimeUnit.SECONDS);
  if (f.isCancelled()) {
      // Connection attempt cancelled by user
  } else if (!f.isSuccess()) {
      // You might get a NullPointerException here because the future
      // might not be completed yet.
      f.cause().printStackTrace();
  } else {
      // Connection established successfully
  }
 
  // GOOD
  Bootstrap b = ...;
  // Configure the connect timeout option.
  b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
  ChannelFuture f = b.connect(...);
  f.awaitUninterruptibly();
 
  // Now we are sure the future is completed.
  assert f.isDone();
 
  if (f.isCancelled()) {
      // Connection attempt cancelled by user
  } else if (!f.isSuccess()) {
      f.cause().printStackTrace();
  } else {
      // Connection established successfully
  }
```



```java
public interface ChannelFuture extends Future<Void> {

    /**
     * 返回一个Channel信道，在该通道中执行与此future异步计算关联的I/O操作
     */
    Channel channel();

    @Override
    ChannelFuture addListener(GenericFutureListener<? extends Future<? super Void>> listener);

    @Override
    ChannelFuture addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners);

    @Override
    ChannelFuture removeListener(GenericFutureListener<? extends Future<? super Void>> listener);

    @Override
    ChannelFuture removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners);

    @Override
    ChannelFuture sync() throws InterruptedException;

    @Override
    ChannelFuture syncUninterruptibly();

    @Override
    ChannelFuture await() throws InterruptedException;

    @Override
    ChannelFuture awaitUninterruptibly();

    /**
     * Returns {@code true} if this {@link ChannelFuture} is a void future and so not allow to call any of the
     * following methods:
     * <ul>
     *     <li>{@link #addListener(GenericFutureListener)}</li>
     *     <li>{@link #addListeners(GenericFutureListener[])}</li>
     *     <li>{@link #await()}</li>
     *     <li>{@link #await(long, TimeUnit)} ()}</li>
     *     <li>{@link #await(long)} ()}</li>
     *     <li>{@link #awaitUninterruptibly()}</li>
     *     <li>{@link #sync()}</li>
     *     <li>{@link #syncUninterruptibly()}</li>
     * </ul>
     */
    boolean isVoid();
}

```

##### 四、io.netty.util.concurrent.Promise源码解析

> 可写的特殊Future异步计算

```java
public interface Promise<V> extends Future<V> {

    /**
     * 将此Future标记为成功，并通知所有的监听器
     *
     * 如果它已经成功或失败，它将抛出{@link IllegalStateException}.
     */
    Promise<V> setSuccess(V result);

    /**
     * 将此Future标记为成功，并通知所有的监听器
     *
     * @return {@code true} 当且仅当将当前future标记为了成功；
     *          {@code false} 因为当前future已经被标记为了成功或者失败；
     */
    boolean trySuccess(V result);

    /**
     * 标记此future为失败，并通知所有的监听器
     *
     * 如果它已经成功或者失败，它将抛出 {@link IllegalStateException}.
     */
    Promise<V> setFailure(Throwable cause);

    /**
     * 标记此future为失败，并通知所有的监听器
     *
     * @return {@code true} 当且仅当成功的将这个future标记为失败；
     *         {@code false} 因为这个future已经被标记为成功或者失败
     */
    boolean tryFailure(Throwable cause);

    /**
     * 标记future异步计算无法取消任务 
     *
     * @return {@code true} 当且仅当成功标记future异步计算无法取消任务或者已经被标记为无法取消任务；
     *         {@code false} 如果当前future异步计算任务已经被取消；
     */
    boolean setUncancellable();

    @Override
    Promise<V> addListener(GenericFutureListener<? extends Future<? super V>> listener);

    @Override
    Promise<V> addListeners(GenericFutureListener<? extends Future<? super V>>... listeners);

    @Override
    Promise<V> removeListener(GenericFutureListener<? extends Future<? super V>> listener);

    @Override
    Promise<V> removeListeners(GenericFutureListener<? extends Future<? super V>>... listeners);

    @Override
    Promise<V> await() throws InterruptedException;

    @Override
    Promise<V> awaitUninterruptibly();

    @Override
    Promise<V> sync() throws InterruptedException;

    @Override
    Promise<V> syncUninterruptibly();
}

```

##### 五、io.netty.channel.ChannelPromise源码解析

> ChannelPromise接口扩展了Promise和ChannelFuture，绑定了Channel，可以进行异步I/O操作，也可以监听Channel的I/O操作。

```java
public interface ChannelPromise extends ChannelFuture, Promise<Void> {

    @Override
    Channel channel();

    @Override
    ChannelPromise setSuccess(Void result);

    ChannelPromise setSuccess();

    boolean trySuccess();

    @Override
    ChannelPromise setFailure(Throwable cause);

    @Override
    ChannelPromise addListener(GenericFutureListener<? extends Future<? super Void>> listener);

    @Override
    ChannelPromise addListeners(GenericFutureListener<? extends Future<? super Void>>... listeners);

    @Override
    ChannelPromise removeListener(GenericFutureListener<? extends Future<? super Void>> listener);

    @Override
    ChannelPromise removeListeners(GenericFutureListener<? extends Future<? super Void>>... listeners);

    @Override
    ChannelPromise sync() throws InterruptedException;

    @Override
    ChannelPromise syncUninterruptibly();

    @Override
    ChannelPromise await() throws InterruptedException;

    @Override
    ChannelPromise awaitUninterruptibly();

    /**
     * Returns a new {@link ChannelPromise} if {@link #isVoid()} returns {@code true} otherwise itself.
     */
    ChannelPromise unvoid();
}

```

##### 六、io.netty.util.concurrent.AbstractFuture源码解析

```java
public abstract class AbstractFuture<V> implements Future<V> {

    @Override
    public V get() throws InterruptedException, ExecutionException {
        //等待future异步计算完成
        await();
				//如果I/O操作已经失败，则返回I/O操作失败的原因
        Throwable cause = cause();
        if (cause == null) {
            //无阻塞返回执行结果，如果future还未执行完，则返回null
            return getNow();
        }
        if (cause instanceof CancellationException) {
            throw (CancellationException) cause;
        }
        throw new ExecutionException(cause);
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      //等待future异步计算指定的时间计算完成
        if (await(timeout, unit)) {
          //如果I/O操作已经失败，则返回I/O操作失败的原因
            Throwable cause = cause();
            if (cause == null) {
              //无阻塞返回执行结果，如果future还未执行完，则返回null
                return getNow();
            }
            if (cause instanceof CancellationException) {
                throw (CancellationException) cause;
            }
            throw new ExecutionException(cause);
        }
        throw new TimeoutException();
    }
}
```



GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

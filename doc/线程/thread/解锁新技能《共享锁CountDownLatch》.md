#### 解锁新技能《共享锁CountDownLatch》

>
CountDownLatch是一种同步辅助工具，允许一个或多个线程等待，直到在其它线程中执行的一组操作完成；CountDownLatch使用指定的计数初始化。wait方法会阻塞，直到当前计数由于countDown方法的调用而达到零，之后所有的等待线程都会被释放，任何后续的wait调用都会立即返回。这是一种一次性现象，计数无法重置。如果需要重置计数的版本，可以参考CyclicBarrier。

##### 一、CountDownLatch的使用方法

```java
public class Test {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("线程一执行完成");
                latch.countDown();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("线程二执行完成");
                latch.countDown();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程三执行完成");
                latch.countDown();
            }
        }).start();
        System.out.println("主线程等待");
        latch.await();
        System.out.println("主线程开始执行");
    }
}
```

执行结果如下：

```sh
线程三执行完成
主线程等待
线程二执行完成
线程一执行完成
主线程开始执行
```

> 上面的示例是一个主线程等待其它三个 线程执行，执行完成后调用countDown方法，计数减一，直到所有的线程执行完成，计数归0，然后await方法放回，主线程继续执行。

##### 二、CountDownLatch源码解析

```java
  public void countDown() {
        sync.releaseShared(1);
    }
```

> countDown方法会递减锁的计数器，如果计数为0，则释放所有等待的线程。如果当前计数大于0，则递减。如果新计数为0，则出于线程调度的目的重新启用所有等待线程。如果当前计数等于0，那么什么也不发生。

```java
 public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }
```

> await方法会使当前线程等待，直到锁的计数器为0，除非线程被中断。如果当前计数为0，则此方法立即返回。如果当前计数大于0，则出于线程调度目的，当前线程将被禁用，并处于休眠状态，直到发生一下两种情况之一：
>
> 1.由于调用了countDown方法，计数达到了0；
>
> 2.其它线程终端当前线程；



GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
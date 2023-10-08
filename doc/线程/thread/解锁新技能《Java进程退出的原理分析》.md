### 解锁新技能《Java进程退出的原理分析》

先了解下Java的Daemon线程，所谓的守护线程就是运行在程序后台的线程，通常守护线程是JVM创建的，用于辅助用户线程或者JVM工作，比较典型的如GC线程。用户创建的线程可以设置成
Daemon线程，程序的主线程（main线程）不是守护线程。Daemon线程在Java里面的定义是，如果虚拟机中只有Daemon线程运行，则虚拟机退出。

- 虚拟机中可能同时有多个线程运行，只有当所有的非守护线程（通常都是用户线程）都结束的时候，虚拟机的进程才会结束，不管当前运行的线程是不是主线程。
- main线程运行结束，如果此时运行的其它线程全部是Daemon线程，JVM会使这些线程停止，同时退出。但是如果此时正在运行的其它线程有非守护线程，那么必须等所有的非守护线程结束，JVM才会退出。

看下下面的守护线程的示例：

```java
public class TestDaemon {

    public static void main(String[] args) throws InterruptedException {

        long start = System.currentTimeMillis();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.DAYS.sleep(Long.MAX_VALUE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();
        TimeUnit.SECONDS.sleep(15);
        System.out.println("系统退出，执行时间：" + (System.currentTimeMillis() - start)+"毫秒");
    }
}
```

程序运行15S后进程正常退出，运行结果是：

```sh
/Library/Java/JavaVirtualMachines/jdk-11.0.10.jdk/Contents/Home/bin/...
系统退出，执行时间：15006毫秒

Process finished with exit code 0
```

启动一个非守护线程，即使main线程执行完成，进程也不会退出。

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)


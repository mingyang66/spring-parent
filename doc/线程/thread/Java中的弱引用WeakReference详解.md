Java中的弱引用WeakReference详解

>
最近再看ThreadLocal源码，其中内部类ThreadLocalMap中的Entry类继承了WeakReference，其表示key的ThreadLocal对象是通过弱应用指向，如果外部没有强引用对象指向ThreadLocal对象的时候就会被GC回收掉，不论当前的内存空间是否足够，这个对象都会被回收

##### 一、WeakReference源码，其中只有两个构造函数

```java
public class WeakReference<T> extends Reference<T> {
		//(1)
    public WeakReference(T referent) {
        super(referent);
    }
		//(2)
    public WeakReference(T referent, ReferenceQueue<? super T> q) {
        super(referent, q);
    }

}
```

- 构造函数（1）的参数referent就是弱引用指向的对象（弱引用和被弱引用指向的对象是两个不同的概念）
- 构造函数（2）的参数比（1）多了一个ReferenceQueue，在对象被回收后会把弱引用对象，也就是WeakReference对象或者其子类的对象放入队列ReferenceQueue中，

##### 二、验证弱引用对象被回收示例

定义PO类User:

```java
public class User {
    private String username = "liming";
    private String password;
    private Job job;

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("User:"+username+ " finalize");
    }

    @Override
    public String toString() {
        return this.getUsername();
    }
}
```

定义弱引用对象UserWeakReference：

```java
public class UserWeakReference extends WeakReference<User> {

    public UserWeakReference(User user) {
        super(user);
    }

    public UserWeakReference(User user, ReferenceQueue<? super User> q) {
        super(user, q);
    }
}
```

验证弱引用对象回收：

```java
public class WeakReferenceTest {
    public static void main(String[] args) throws InterruptedException {
        //定义弱引用对象
        UserWeakReference reference = new UserWeakReference(new User());
        //输出弱应用对象
        System.out.println("Username:" + reference.get());
        //GC
        System.gc();
        Thread.sleep(5000);
        //输出GC后弱引用对象输出
        System.out.println(reference.get());
        //判定是否弱应用对象已被回收
        if (reference.get() == null) {
            System.out.println("user is clear...");
        }
    }
}
```

输出结果是：

```java
Username:liming
User:liming finalize
null
user is clear...
```

##### 三、验证ReferenceQueue队列的使用（基础类定义同上）

```java
public class WeakReferenceQueueTest {
    public static void main(String[] args) throws InterruptedException {
        ReferenceQueue referenceQueue = new ReferenceQueue();
        UserWeakReference reference = new UserWeakReference(new User(), referenceQueue);
        //GC之前弱引用对象是否为空
        System.out.println("Username:" + reference.get());
        //GC回收之前ReferenceQueue是否为空
        System.out.println(referenceQueue.poll());
        System.gc();
        Thread.sleep(5000);
        //GC回收之后弱引用对象输出结果
        System.out.println(reference.get());
        //GC回收之后队列内容
        System.out.println(referenceQueue.poll());
        //GC之后判定ReferenceQueue是否为空
        if (referenceQueue.poll() != null) {
            System.out.println("referenceQueue is not null...");
        }
    }
}
```

输出结果：

```java
Username:liming
null
User:liming finalize
null
com.emily.infrastructure.test.mainTest.UserWeakReference@1d057a39
```

> 可以看到在ReferenceQueue队列中在GC之前是空的，在GC之后是有弱引用对象存入队列之中。

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
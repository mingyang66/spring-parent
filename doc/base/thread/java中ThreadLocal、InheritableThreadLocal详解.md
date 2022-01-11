##### java中ThreadLocal、InheritableThreadLocal详解

一、ThreadLocal介绍

> ​		在多线程环境下访问同一个线程的时候会出现并发问题，特别是多个线程同时对一个变量进行写入操作时，为了保证线程的安全，通常会进行加锁来保证线程的安全，但是加锁又会造成效率的降低；ThreadLocal是jdk提供的除了加锁之外保证线程安全的方法，其实现原理是在Thread类中定义了两个ThreadLocalMap类型变量threadLocals、inheritableThreadLocals用来存储当前操作的ThreadLocal的引用及变量对象，这样就可以把当前线程的变量和其他的线程的变量之间进行隔离，从而实现了线程的安全性。

##### 二、ThreadLocal的简单示例

> 首先定义一个ThreadLocal全局变量，在main方法中开启两个线程，在线程启用之前先设置变量然后再线程内部和外部获取变量，发现输出结果有什么不同吗？疑问我们后面解答。

```java
public class Test1 {
    public static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void main(String[] args) throws IllegalAccessException, NoSuchFieldException, InterruptedException {
        threadLocal.set("主线程1。。。");
        System.out.println("线程1：" + threadLocal.get());
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("子线程1：" + Thread.currentThread().getName() + ":" + threadLocal.get());
            }
        }).start();

        Thread.sleep(1000);
        threadLocal.set("主线程2。。。");
        System.out.println("线程2：" + threadLocal.get());
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("子线程2：" + Thread.currentThread().getName() + ":" + threadLocal.get());
            }
        }).start();
        //删除本地内存中的变量
        threadLocal.remove();
    }

}
```

输出结果如下：

```
线程1：主线程1。。。
子线程1：Thread-0:null
线程2：主线程2。。。
子线程2：Thread-1:null
```

##### 三、ThreadLocal原理及源码分析

Thread类有两个变量threadLocals和inheritableThreadLocals，这两个变量都是ThreadLocal类的内部类ThreadLocalMap类型变量，ThreadLocalMap是一个类似于HashMap类型的容器类，默认情况下两个变量都为null，只有当第一次调用ThreadLocal的get或set方法的时候才会创建它们。

```java
    /* ThreadLocal values pertaining to this thread. This map is maintained
     * by the ThreadLocal class. */
    ThreadLocal.ThreadLocalMap threadLocals = null;

    /*
     * InheritableThreadLocal values pertaining to this thread. This map is
     * maintained by the InheritableThreadLocal class.
     */
    ThreadLocal.ThreadLocalMap inheritableThreadLocals = null;
```

> 每个线程的变量不是存放在ThreadLocal类中，都是存放在当前线程的变量之中，也就是说存放在当前线程的上下文空间中，其线程本身相当于变量的一个承载工具，通过set方法将变量添加到线程的threadLocals变量中，通过get方法能够从它的threadLocals变量中获取变量。如果线程一直不终止，那么这个本地变量将会一直存放在它的threadLocals变量中，所以可以通过remove方法删除本地变量。

- set方法

  ```java
      public void set(T value) {
          //获取当前线程
          Thread t = Thread.currentThread();
        	//以当前线程为参数，查找线程的变量threadLocals(1)
          ThreadLocalMap map = getMap(t);
          //如果线程变量map不为null,直接添加本地变量，
        	//key为当前定义的ThreadLocal变量的this引用，值为添加到本地的变量值
          if (map != null) {
              map.set(this, value);
          } else {
             //如果为null,这创建一个ThreadLocalMap对象赋值给当前线程t(2)
              createMap(t, value);
          }
      }
  ```

  上述代码（1）处获取当前线程的threadLocals变量，方法如下：

  ```java
  //获取线程的threadLocals变量，并将本地变量和ThreadLocal对象引用绑定到变量上    
  ThreadLocalMap getMap(Thread t) {
          return t.threadLocals;
      }
  ```

  上述代码（2）处创建ThreadLocalMap对象并初始化当前线程的threadLocals变量，方法如下：

  ```java
      void createMap(Thread t, T firstValue) {
         //this是当前ThreadLocal对象的引用，firstValue是变量值
          t.threadLocals = new ThreadLocalMap(this, firstValue);
      }
  ```

  

- get方法

```java
    public T get() {
       //获取当前线程|调用者的线程（1）
        Thread t = Thread.currentThread();
       //获取当前线程的threadLocals变量（2）
        ThreadLocalMap map = getMap(t);
       //如果ThreadLocalMap变量不为null，就可以在map中查找到本地变量的值（3）
        if (map != null) {
            //通过当前ThreadLocal对象的this引用获取变量中
            ThreadLocalMap.Entry e = map.getEntry(this);
          // 如果变量值不为空，则转换为指定的类型返回
            if (e != null) {
                @SuppressWarnings("unchecked")
                T result = (T)e.value;
                return result;
            }
        }
      //threadLocals变量为null,则对当前线程的threadLocals的变量进行初始化
        return setInitialValue();
    }

    private T setInitialValue() {
        //调用抽象初始化方法，默认：null
        T value = initialValue();
      //获取当前线程
        Thread t = Thread.currentThread();
       //查找当前线程的threadLocals变量
        ThreadLocalMap map = getMap(t);
        //如果map不为null,则直接添加本地变量，key为当前ThreadLocal的this引用，value为本地变量
        if (map != null) {
            map.set(this, value);
        } else {
            //首次添加，创建对应的threadLocals变量
            createMap(t, value);
        }
        if (this instanceof TerminatingThreadLocal) {
            TerminatingThreadLocal.register((TerminatingThreadLocal<?>) this);
        }
        return value;
    }
```

- remove方法的实现

```java
     public void remove() {
        //获取当前线程的threadLocals变量，如果变量非null，则删除当前ThreadLocal引用对应的变量值
         ThreadLocalMap m = getMap(Thread.currentThread());
         if (m != null) {
             //删除ThreadLocalMap中存储的数据（1）
             m.remove(this);
         }
     }
```

上述（1）中的remove方法是ThreadLocal.ThreadLocalMap类的方法：

```java
        private void remove(ThreadLocal<?> key) {
            //当前ThreadLocal变量所在的table数组位置
            Entry[] tab = table;
            int len = tab.length;
            int i = key.threadLocalHashCode & (len-1);
            for (Entry e = tab[i];
                 e != null;
                 e = tab[i = nextIndex(i, len)]) {
                if (e.get() == key) {
                  //调用WeakReference的clear方法清除对ThreadLocal的弱引用
                    e.clear();
                  //清理key为null的元素
                    expungeStaleEntry(i);
                    return;
                }
            }
        }
```



##### 四、ThreadLocal不支持继承性

​		在同一个ThreadLocal变量在父线程中被设置值之后，在子线程中是获取不到的（由二中的实例输出可以看出），threadLocals中为当前调用线程的本地变量，所以子线程是无法获取父线程的变量的；一开始我们介绍的时候说Thread类中还有一个inheritableThreadLocals变量，其值是存储的子线程的变量，所以可以通过InheritableThreadLocal类获取父线程的变量；

##### 五、InheritableThreadLocal类源码简介

InheritableThreadLocal类是ThreadLocal类的子类，重写了chidValue、getMap、createMap三个方法，其中createMap方法在被调用的时候创建的是inheritableThreadLocals变量值（ThreadLocal类中创建的是threadLocals变量的值），getMap方法在被get或set方法调用的时候返回的也是线程的inheritableThreadLocals变量。

```java
public class InheritableThreadLocal<T> extends ThreadLocal<T> {

    protected T childValue(T parentValue) {
        return parentValue;
    }

    ThreadLocalMap getMap(Thread t) {
       return t.inheritableThreadLocals;
    }
    void createMap(Thread t, T firstValue) {
        t.inheritableThreadLocals = new ThreadLocalMap(this, firstValue);
    }
}
```

那chidValue方法又是在哪里被调用呢？它会在ThreadLocalMap的中被调用：

```java
        private ThreadLocalMap(ThreadLocalMap parentMap) {
            Entry[] parentTable = parentMap.table;
            int len = parentTable.length;
            setThreshold(len);
            table = new Entry[len];

            for (Entry e : parentTable) {
                if (e != null) {
                    @SuppressWarnings("unchecked")
                    ThreadLocal<Object> key = (ThreadLocal<Object>) e.get();
                    if (key != null) {
                       //调用重写的childValue方法
                        Object value = key.childValue(e.value);
                        Entry c = new Entry(key, value);
                        int h = key.threadLocalHashCode & (len - 1);
                        while (table[h] != null)
                            h = nextIndex(h, len);
                        table[h] = c;
                        size++;
                    }
                }
            }
        }
```

子线程是如何继承父线程的变量的，看Thread类的如下代码：

```java
    private Thread(ThreadGroup g, Runnable target, String name,
                   long stackSize, AccessControlContext acc,
                   boolean inheritThreadLocals) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }

        this.name = name;
				//获取当前线程（父线程）
        Thread parent = currentThread();
        //安全校验
        SecurityManager security = System.getSecurityManager();
        if (g == null) {
            /* Determine if it's an applet or not */

            /* If there is a security manager, ask the security manager
               what to do. */
            if (security != null) {
                g = security.getThreadGroup();
            }

            /* If the security manager doesn't have a strong opinion
               on the matter, use the parent thread group. */
            if (g == null) {
                g = parent.getThreadGroup();
            }
        }

        /* checkAccess regardless of whether or not threadgroup is
           explicitly passed in. */
        g.checkAccess();

        /*
         * Do we have the required permissions?
         */
        if (security != null) {
            if (isCCLOverridden(getClass())) {
                security.checkPermission(
                        SecurityConstants.SUBCLASS_IMPLEMENTATION_PERMISSION);
            }
        }

        g.addUnstarted();

        this.group = g;//设置为当前线程组
        this.daemon = parent.isDaemon();//判定是否是守护线程同父线程
        this.priority = parent.getPriority();//优先级同父线程
        if (security == null || isCCLOverridden(parent.getClass()))
            this.contextClassLoader = parent.getContextClassLoader();
        else
            this.contextClassLoader = parent.contextClassLoader;
        this.inheritedAccessControlContext =
                acc != null ? acc : AccessController.getContext();
        this.target = target;
        setPriority(priority);
        //如果是否初始化线程的inheritThreadLocals变量为true并且父线程的inheritThreadLocals变量不为null
        if (inheritThreadLocals && parent.inheritableThreadLocals != null)
           //设置子线程的inheritThreadLocals变量为父线程的inheritThreadLocals变量
            this.inheritableThreadLocals =
                ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);
        /* Stash the specified stack size in case the VM cares */
        this.stackSize = stackSize;

        /* Set thread ID */
        this.tid = nextThreadID();
    }
```

> InheritableThreadLocal类通过重写方法在调用get、set方法的时候会调用重写的方法，调用方法时会对线程的inheritableThreadLocals变量进行初始化，在对子线程进行初始化的时候会将子线程的inheritableThreadLocals变量赋值为父线程的inheritableThreadLocals变量值，这样就实现了子线程继承父线程问题。

##### 六、拓展ThreadLocal使用不当引起的内存泄漏问题

首先了解下什么是弱引用，弱引用也就是GC会主动的帮你把内存回收掉，但是当弱引用指定的对象有强引用指向时GC则不会回收对象。

ThreadLocalMap的内部实现实际上是一个Entry集合，Entry类继承了WeakReference类，表示其是一个弱引用对象，其构造函数super调用了父类的构造函数，传递了k变量，即ThreadLocal对象的引用，也就是说Map集合中存储的是ThreadLocal的弱引用。

```java
    static class ThreadLocalMap {

        static class Entry extends WeakReference<ThreadLocal<?>> {
            /** The value associated with this ThreadLocal. */
            Object value;

            Entry(ThreadLocal<?> k, Object v) {
                super(k);
                value = v;
            }
        }
        }
```

如果当前线程一直存在且没有调用ThreadLocal的remove方法，那么ThreadLocal会在下次GC的时候将弱引用对象回收，这样就会造成ThreadLocalMap对象中的Entry对象的key为null，但是value值还是存在强引用关系，这样就会造成内存泄漏问题；引用链关系如下：

```
Thread->ThreadLocalMap->Entry->value
```

> 弱引用会在没有强引用的情况下在下次GC的时候自动的回收掉，每次使用完后要记得主动的调用remove方法，而且ThreadLocal每次调用get、set、remove的时候都会直接或者间接的调用expungeStaleEntry方法清除掉key为null的Entry，从而避免了内存泄漏。

七、拓展InheritableThreadLocal类子线程使用线程池更改存储变量值不变问题

```java
public class Test {
    public static final ThreadLocal<String> threadLocal = new InheritableThreadLocal<>();
    private static ExecutorService executorService = Executors.newFixedThreadPool(1);

    public static void main(String[] args) throws IllegalAccessException, NoSuchFieldException, InterruptedException {
        threadLocal.set("主线程1。。。");
        System.out.println("线程1：" + threadLocal.get());
        executorService.submit(TtlRunnable.get(() -> System.out.println("子线程：" + Thread.currentThread().getName() + ":" + threadLocal.get())));
        Thread.sleep(1000);
        threadLocal.set("主线程2。。。");
        System.out.println("线程2：" + threadLocal.get());
        executorService.submit(TtlRunnable.get(() -> System.out.println("子线程：" + Thread.currentThread().getName() + ":" + threadLocal.get())));
    }

}
```

输出结果：

```
线程1：主线程1。。。
子线程：pool-1-thread-1:主线程1。。。
线程2：主线程2。。。
子线程：pool-1-thread-1:主线程1。。。
```

> 可以看到在运行线程2之前会修改InheritableThreadLocal内部存的值，但是在线程池内部获取值还是原来的，这其实又涉及到了线程池等会池化复用线程情况下，提供ThreadLocal值传递问题，推荐使用阿里开源的TTLhttps://github.com/alibaba/transmittable-thread-local



GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)


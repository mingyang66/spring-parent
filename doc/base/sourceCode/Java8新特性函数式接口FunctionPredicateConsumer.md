### Java8新特性函数式接口Function|Predicate|Consumer

| 名称      | 类型           | 描述                   |
| --------- | -------------- | ---------------------- |
| Function  | Function<T, R> | 接收T对象，返回R对象   |
| Predicate | Predicate<T>   | 接收T对象，返回boolean |
| Consumer  | Consumer<T>    | 接收T对象，无返回值    |
|           |                |                        |

## java.util.function.Function接口

> 该函数的主要作用是根据输入的参数对象做一些额外的判定、计算处理，返回另外一个指定的对象R

##### 1.源码解析

```
package java.util.function;

import java.util.Objects;

/**
 * 代表一个函数接收参数T，返回结果R
 */
@FunctionalInterface
public interface Function<T, R> {
    /**
     * 应用输入的参数T，并返回结果
     */
    R apply(T t);
    /**
     * 返回一个先执行before行数对象的apply方法，再执行当前函数的apply方法的函数对象
     */
    default <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }
    /**
     * 返回一个先执行当前函数对象的apply方法，再执行after函数对象的apply方法的函数对象。
     */
    default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(apply(t));
    }
    /**
     * 返回一个输入参数和返回值相同的函数对象
     */
    static <T> Function<T, T> identity() {
        return t -> t;
    }
}

```

##### 2.测试示例

```java
				Function<Integer, Integer> function = p-> 2*p;
        Function<Integer, Integer> function1 = p-> p*p;
        int s = function.andThen(function1).apply(3);
        int s1 = function.compose(function1).apply(3);
        System.out.println(s);
        System.out.println(s1);
```

输出结果：

```
36
18
```

## java.util.function.Predicate接口

> 该函数的作用其实就是判定输入的对象是否符合某个条件,然后返回一个布尔值;

##### 1.源码解析

```
/*
 * 接收一个对象,并返回一个布尔值
 */
package java.util.function;
import java.util.Objects;
/**
 * 
 */
@FunctionalInterface
public interface Predicate<T> {
    /**
     * 接收一个对象,并判定对象是否符合某些条件返回一个布尔值
     */
    boolean test(T t);
    /**
     * 且操作,即当前函数满足条件,other函数也满足条件
     */
    default Predicate<T> and(Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) && other.test(t);
    }
    /**
     * 取反操作，即为true返回false
     */
    default Predicate<T> negate() {
        return (t) -> !test(t);
    }
    /**
     * 或操作，满足A条件或者满足B条件
     */
    default Predicate<T> or(Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) || other.test(t);
    }
    /**
     * 静态方法，判定是否相等
     */
    static <T> Predicate<T> isEqual(Object targetRef) {
        return (null == targetRef)
                ? Objects::isNull
                : object -> targetRef.equals(object);
    }
}

```

##### 2.示例

```java
        Predicate<Integer> predicate = x -> x > 0;
        Predicate<Integer> predicate1 = x -> x+1 > 0;
        System.out.println("test判定比较结果="+predicate.test(1));
        System.out.println("test判定比较结果="+predicate.test(-1));
        System.out.println("and比较="+predicate.and(predicate1).test(1));
        System.out.println("negate比较="+predicate.negate().test(-1));
        System.out.println("or比较="+predicate.or(predicate1).test(1));
        System.out.println("isEqual比较="+Predicate.isEqual("12").test("12"));
        System.out.println("isEqual比较="+Predicate.isEqual(1).test(12));
```

输出结果：

```java
test判定比较结果=true
test判定比较结果=false
and比较=true
negate比较=true
or比较=true
isEqual比较=true
isEqual比较=false
```

## java.util.function.Consumer

> 该函数的作用是接收一个对象，对接收到的对象进行处理，并无返回结果；

##### 1.源码解析

```java
/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package java.util.function;

import java.util.Objects;

/**
 * 接收一个对象，并无返回结果
 */
@FunctionalInterface
public interface Consumer<T> {

    /**
     * 抽象方法，接收一个对象T，无返回结果
     */
    void accept(T t);

    /**
     * 且操作，返回一个先执行当前函数的accept方法，再执行after函数的accept方法的函数对象
     */
    default Consumer<T> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> { accept(t); after.accept(t); };
    }
}

```

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/doc/base](https://github.com/mingyang66/spring-parent/tree/master/doc/base)
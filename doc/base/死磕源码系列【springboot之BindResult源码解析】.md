### 死磕源码系列【springboot之BindResult源码解析】

> BindResult是Binder类返回绑定操作结果的容器对象，可能包含成功绑定的对象或空结果。

##### BindResult属性源码

```java
	private static final BindResult<?> UNBOUND = new BindResult<>(null);
	//绑定结果值
	private final T value;
	//创建有一个BindResult实例对象
	private BindResult(T value) {
		this.value = value;
	}
```

##### 获取绑定的对象

```java
	/**
	 * 返回绑定的对象，如果绑定对象为null，则抛出NoSuchElementException异常
	 */
	public T get() throws NoSuchElementException {
		if (this.value == null) {
			throw new NoSuchElementException("No value bound");
		}
		return this.value;
	}
```

##### 判定对象是否已经绑定了值

```java
	public boolean isBound() {
		return (this.value != null);
	}
```

##### 使用绑定的值调用指定的消费者，如果值未绑定则什么都不做

```java
	public void ifBound(Consumer<? super T> consumer) {
		Assert.notNull(consumer, "Consumer must not be null");
		if (this.value != null) {
			consumer.accept(this.value);
		}
	}
```

##### 返回指定绑定结果的BindResult对象

```java
	static <T> BindResult<T> of(T value) {
		if (value == null) {
			return (BindResult<T>) UNBOUND;
		}
		return new BindResult<>(value);
	}
```

##### 使用绑定值作为或者值未绑定返回一个更新后的值构建BindResult对象

```java
	public <U> BindResult<U> map(Function<? super T, ? extends U> mapper) {
		Assert.notNull(mapper, "Mapper must not be null");
		return of((this.value != null) ? mapper.apply(this.value) : null);
	}
```

##### 返回绑定的值，如果未绑定返回其它值

```java
public T orElse(T other) {
		return (this.value != null) ? this.value : other;
	}
```

##### 返回绑定的值，如果未绑定返回其它值

```java
public T orElseGet(Supplier<? extends T> other) {
		return (this.value != null) ? this.value : other.get();
	}
```

##### 返回绑定的值，如果未绑定抛出一个由Supplier创建的异常

```java
	public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
		if (this.value == null) {
			throw exceptionSupplier.get();
		}
		return this.value;
	}
```

------

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
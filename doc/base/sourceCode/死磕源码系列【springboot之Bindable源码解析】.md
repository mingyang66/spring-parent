### 死磕源码系列【springboot之Bindable源码解析】

> Bindable是指可由Binder绑定的源（如：基本数据类型，java对象、List、数组等等），也可以理解为可以绑定到指定的属性配置的值。

##### Bindable属性源码

```java
public final class Bindable<T> {
	//默认注解空数组
	private static final Annotation[] NO_ANNOTATIONS = {};
	//要绑定项的类型
	private final ResolvableType type;
	//要绑定项的包装类型
	private final ResolvableType boxedType;
	//要绑定的数据值的提供者，是一个函数式接口
	private final Supplier<T> value;
	//可能影响绑定的任何关联注解
	private final Annotation[] annotations;
	//私有的构造函数，创建一个Bindable实例
	private Bindable(ResolvableType type, ResolvableType boxedType, Supplier<T> value, Annotation[] annotations) {
		this.type = type;
		this.boxedType = boxedType;
		this.value = value;
		this.annotations = annotations;
	}
	}
```

##### 获取不同数据类型的Bindable实例对象：

```java
	/**
	 * 创建一个指定值及数据类型和指定值数据类型相同的Bindable实例对象
	 */
	@SuppressWarnings("unchecked")
	public static <T> Bindable<T> ofInstance(T instance) {
		Assert.notNull(instance, "Instance must not be null");
		Class<T> type = (Class<T>) instance.getClass();
		return of(type).withExistingValue(instance);
	}

	/**
	 * 创建一个指定数据类型的Bindable实例对象
	 */
	public static <T> Bindable<T> of(Class<T> type) {
		Assert.notNull(type, "Type must not be null");
		return of(ResolvableType.forClass(type));
	}

	/**
	 * 创建一个指定数据类型List的Bindable实例对象
	 */
	public static <E> Bindable<List<E>> listOf(Class<E> elementType) {
		return of(ResolvableType.forClassWithGenerics(List.class, elementType));
	}

	/**
	 * 创建一个指定数据类型Set的Bindalbe实例对象
	 */
	public static <E> Bindable<Set<E>> setOf(Class<E> elementType) {
		return of(ResolvableType.forClassWithGenerics(Set.class, elementType));
	}

	/**
	 * 创建一个指定类型为Map及指定key-value数据类型的Bindable实例对象
	 */
	public static <K, V> Bindable<Map<K, V>> mapOf(Class<K> keyType, Class<V> valueType) {
		return of(ResolvableType.forClassWithGenerics(Map.class, keyType, valueType));
	}

	/**
	 * 创建一个指定类型的Bindable实例对象
	 */
	public static <T> Bindable<T> of(ResolvableType type) {
		Assert.notNull(type, "Type must not be null");
		ResolvableType boxedType = box(type);
		return new Bindable<>(type, boxedType, null, NO_ANNOTATIONS);
	}

```

##### 将指定的数据类型转换为包装类型：

```java
	private static ResolvableType box(ResolvableType type) {
		Class<?> resolved = type.resolve();
		if (resolved != null && resolved.isPrimitive()) {
			Object array = Array.newInstance(resolved, 1);
			Class<?> wrapperType = Array.get(array, 0).getClass();
			return ResolvableType.forClass(wrapperType);
		}
		if (resolved != null && resolved.isArray()) {
			return ResolvableType.forArrayComponent(box(type.getComponentType()));
		}
		return type;
	}
```

##### 更新Bindable实例对象：

```java
	/**
	 * 创建一个指定注解的更新Bindable实例对象
	 */
	public Bindable<T> withAnnotations(Annotation... annotations) {
		return new Bindable<>(this.type, this.boxedType, this.value,
				(annotations != null) ? annotations : NO_ANNOTATIONS);
	}

	/**
	 * 创建一个存在指定值的更新Bindable实例对象
	 */
	public Bindable<T> withExistingValue(T existingValue) {
		Assert.isTrue(
				existingValue == null || this.type.isArray() || this.boxedType.resolve().isInstance(existingValue),
				() -> "ExistingValue must be an instance of " + this.type);
		Supplier<T> value = (existingValue != null) ? () -> existingValue : null;
		return new Bindable<>(this.type, this.boxedType, value, this.annotations);
	}

	/**
	 * 创建一个suppliedValue值提供者更新的Bindable实例对象
	 */
	public Bindable<T> withSuppliedValue(Supplier<T> suppliedValue) {
		return new Bindable<>(this.type, this.boxedType, suppliedValue, this.annotations);
	}
```

------

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)


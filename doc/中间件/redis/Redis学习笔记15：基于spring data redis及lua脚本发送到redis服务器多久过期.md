#### Redis学习笔记15：基于spring data redis及lua脚本发送到redis服务器多久过期

> 在Redis服务器中，通过SCRIPT LOAD命令加载的脚本会被缓存，并且会一直保存在缓存中，直到服务器重启或者使用SCRIPT FLUSH名利手动清空缓存。

一个对springboot redis框架进行重写，支持lettuce、jedis、连接池、同时连接多个集群、多个redis数据库、开发自定义属性配置的开源SDK

```xml
<dependency>
    <groupId>io.github.mingyang66</groupId>
    <artifactId>emily-spring-boot-redis</artifactId>
    <version>4.4.0</version>
</dependency>
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

##### 一、缓存的好处

缓存的好处是，当一个脚本被多次执行时，可以避免每次都重新解析和编译脚本，提高执行效率。缓存的脚本会通过SHA1散列值作为键，保存在Redis服务器的脚本缓存中。

需要注意的是，当Redis服务器重启或者使用SCRIPT FLUSH命令手动清空缓存时，所有缓存的脚本都会被清除，需要重新加载。

在脚本缓存中的脚本并不会自动过期或者自动清除，除非手动进行重启或者执行清空操作。

##### 二、Redis脚本命令

- 执行lua脚本

```sh
EVAL script numkeys key [key ...] arg [arg ...]
```

- 执行lua脚本

```sh
	EVALSHA sha1 numkeys key [key ...] arg [arg ...]
```

- 查询指定的脚本是否已经被保存在缓存当中

```sh
SCRIPT EXISTS script [script ...]
```

案例如下：

```sh
xx.xx.xx.xx:0>SCRIPT EXISTS "e877b0c16c7f1c6d1b4e0c7be5b13c02b98d58d4"
 1)  "0"
```



- 从脚本缓存中移除所有脚本

```sh
SCRIPT FLUSH
```

案例如下：

```sh
xx.xx.xx.xx:0>script flush
"OK"
```



- 杀死当前正在运行的lua脚本

```sh
	SCRIPT KILL
```

案例如下：

```sh
xx.xx.xx.xx:0>SCRIPT KILL
"NOTBUSY No scripts in execution right now."
```



- 将脚本script添加到脚本缓存中，但并不立即执行这个脚本

```sh
SCRIPT LOAD script
```

案例如下：

```sh
xx.xx.xx.xx:0>SCRIPT LOAD "return redis.call('GET', 'mykey')"
"87f5a5b4a00f1811c3352ea840cb30dea17ac071"
```


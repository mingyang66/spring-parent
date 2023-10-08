### RabbitMQ学习笔记：centos7中操作rabbitmqctl指令the VM is running with native name encoding of latin1 which may cause Elixir to malfunction as it expects utf8

##### CentOS7中安装了rabbitmq，操作CLI工具时报如下错误：

```
warning: the VM is running with native name encoding of latin1 which may cause Elixir to malfunction as it expects utf8. Please ensure your locale is set to UTF-8 (which can be verified by running "locale" in your shell)
```

##### 解决方法是在/etc/profile环境变量配置文件末尾加上如下配置：

```
export LC_ALL=en_US.UTF-8
```

##### 然后执行：

```java
source /etc/profile
```

解决方案参考：[https://stackoverflow.com/questions/32407164/the-vm-is-running-with-native-name-encoding-of-latin1-which-may-cause-elixir-to](https://stackoverflow.com/questions/32407164/the-vm-is-running-with-native-name-encoding-of-latin1-which-may-cause-elixir-to)

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rabbitmq-service)
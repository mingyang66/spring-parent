### mac中podman容器新建RabbitMQ集群

##### 一、在/etc/hosts文件中添加如下配置：

```sh
10.88.0.10 rabbit1 
10.88.0.11 rabbit2
10.88.0.12 rabbit3
```

##### 二、执行如下podman指令

```sh
podman run -d --hostname rabbit1 --name myrabbit1 --ip 10.88.0.10 -p 15672:15672 -p 5672:5672 -p 4372:4369 -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:3.11-management
    
podman run -d --hostname rabbit2 --name myrabbit2 --ip 10.88.0.11 -p 15673:15672 -p 5673:5672  -p 4373:4369 --add-host myrabbit1:10.88.0.10 -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:3.11-management
    
podman run -d --hostname rabbit3 --name myrabbit3 --ip 10.88.0.12 -p 15674:15672 -p 5674:5672  -p 4374:4369 --add-host myrabbit1:10.88.0.10 --add-host myrabbit2:10.88.0.11 -e RABBITMQ_ERLANG_COOKIE='rabbitcookie' rabbitmq:3.11-management
```

- -d：后台进行运行；
- --hostname：RabbitMQ主机名称；
- --name：容器名称；
- -p 15672:15672 访问HTTP API客户端，容器对外的接口和内部接口映射；
- -p 5672:5672 由不带TLS和带TLS的AMQP 0-9-1和1.0客户端使用；
- --ip 10.88.0.10 为容器指定一个静态IPv4定制；
- --add-host：指定一个自定义主机和IP的映射关系（host:ip）类似docker中的--link

##### 三、分别在myrabbit2、myrabbit3容器中执行如下指令加入容器

```sh
docker exec -it myrabbit2 bash
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl join_cluster --ram rabbit@rabbit1
rabbitmqctl start_app
exit
```

```sh
docker exec -it myrabbit3 bash
rabbitmqctl stop_app
rabbitmqctl reset
rabbitmqctl join_cluster --ram rabbit@rabbit1
rabbitmqctl start_app
exit

```


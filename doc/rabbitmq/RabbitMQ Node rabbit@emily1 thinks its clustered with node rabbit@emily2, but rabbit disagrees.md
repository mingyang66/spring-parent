#### RabbitMQ Node rabbit@emily1 thinks its clustered with node rabbit@emily2, but rabbit disagrees

```sh
warning: the VM is running with native name encoding of latin1 which may cause Elixir to malfunction as it expects utf8. Please ensure your locale is set to UTF-8 (which can be verified by running "locale" in your shell)
Error: unable to perform an operation on node 'rabbit@emily2'. Please see diagnostics information and suggestions below.

Most common reasons for this are:

 * Target node is unreachable (e.g. due to hostname resolution, TCP connection or firewall issues)
 * CLI tool fails to authenticate with the server (e.g. due to CLI tool's Erlang cookie not matching that of the server)
 * Target node is not running

In addition to the diagnostics info below:

 * See the CLI, clustering and networking guides on https://rabbitmq.com/documentation.html to learn more
 * Consult server logs on node rabbit@emis2
 * If target node is configured to use long node names, don't forget to use --longnames with CLI tools

DIAGNOSTICS
===========

attempted to contact: [rabbit@emis2]

rabbit@emis2:
  * connected to epmd (port 4369) on emis2
  * epmd reports: node 'rabbit' not running at all
                  no other nodes on emis2
  * suggestion: start the node

Current node details:
 * node name: 'rabbitmqcli-2879-rabbit@emily2'
 * effective user's home directory: /var/lib/rabbitmq
 * Erlang cookie hash: MRFkIwPZlMRWZvXka/6Rsg==
```

RabbitMQ报以上错误时是由于当前节点mnesia数据库中记录还在集群之中，但是集群确认为其已经不咋集群中了，解决方案是：

1. 删除当前节点/var/lib/rabbit/mnesia数据库记录
2. 确保/var/lib/rabbit/.erlang.cookie中的key和集群中的保持一致
3. rabbitmq-server start -detached启动erlang和rabbitmq服务器
4. rabbitmqctl stop_app停止rabbitmq服务器
5. rabbitmqctl join_cluster rabbit@emily2 加入集群
6. rabbitmqctl start_app启动当前节点rabbitmq

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)


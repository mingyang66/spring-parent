### 解锁新技能《Redis ACL SETUSER命令》

> ACL SETUSER指令根据指定的规则创建用户或修改一个已经存在用户的访问规则，可以指定用户访问key的权限、访问命令行指令的权限、访问所有通道的权限；

语法格式如下：

```sh
ACL SETUSER username [rule [rule ...]]
```

##### Redis ACL规则被分为两种类型：

- 定义commond权限的命令行规则（Command rules）；
- 定义用户状态的规则（User management rules）；

##### Command rules（命令行规则）

- ~<pattern>: 添加指定key的匹配模式（正则表达式），指定key模式匹配的列表的可读可写权限，也可以给用户指定多个key的匹配模式；示例：~objects:*
- %R~<pattern>:（7.0以后版本支持）指定可读的key的匹配模式；
- %W~<pattern>:（7.0以后版本支持）指定可写的key的匹配模式；
- %RW~<pattern>: （7.0以后版本支持）别名是~<pattern>；
- allkeys：别名是~*，允许用户访问所有的key；
- resetkeys：删除用户可以访问的所有key的权限；
- &<pattern>：（6.2版本以后支持）指定用户可以访问的Pub/Sub通道的匹配模式，可以给用户指定多个通道模式；示例：&chatroom:*
- allchannels：别名&*，允许用户访问所有的Pub/Sub通道；
- resetchannels：删除用户可以访问的所有Pub/Sub通道的访问权限；
- +<command>: 添加用户可以调用的命令列表，可以使用|分割（e.g "+config|get"）;
- +@<category>: 添加用户可以调用的类别日志列表（e.g +@string）;
- +allcommands: 别名+@all. 授权所有的指定用户都可以调用；
- -<command>: 删除用户可以执行的指令，多个指令可以用|分割（7.0以后版本支持）e.g "-confg|set"
- -@<category>: 类似+@<category> ，删除用户可执行权限的类别列表；
- nocommands: 别名是-@all. 删除用户可执行的所有命令权限；

##### User management rules（用户管理规则）

- on: 激活用户，将会用AUTH <username> <password> 命令激活用户；
- off: 将用户设置为非激活状态，如果用户已经和redis服务器建立连接，则可以正常的操作（除非删除用户或者关闭连接）
- nopass: 设置用户无密码认证；
- />password: 给用户添加指定的密码（eg: 》>password）；
- #<hashedpassword>: 添加指定的哈希编码过的用户密码（eg: c3ab8ff13720e8ad9047dd39466b3c8974e592c2fa383d4a3960714caef0c4f2）；
- <password: 类似>password, 删除指定的密码；
- !<hashedpassword>: 类似#<hashedpassword>, 删除指定的哈希密码；
- reset: 删除用户的任何功能；

##### 示例如下：

```sh
127.0.0.1:6379> ACL USERS
1) "default"
127.0.0.1:6379> ACL SETUSER emily on ~* &* +@all >emily123
OK
127.0.0.1:6379> ACL USERS 
1) "default"
2) "emily"
127.0.0.1:6379> ACL LIST
1) "user default on #5eda3cda6825004208c8d5fe430304e7c7127058922d9f2d1671389e71fd9222 ~* &* +@all"
2) "user emily on #5eda3cda6825004208c8d5fe430304e7c7127058922d9f2d1671389e71fd9222 ~* &* +@all"
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
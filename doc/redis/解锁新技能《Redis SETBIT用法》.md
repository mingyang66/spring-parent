### 解锁新技能《Redis SETBIT用法》

##### SETBIT语法：

```sh
SETBIT key offset value
```

- key：位图的键
- offset：偏移量（0~2^32-1，位图最大512MB），最大偏移量值是4294967295
- value：值0或1

我们先回顾下常识：

```sh
1GB=1024MB
1MB=1024KB
1KB=1024B
1B=8bits
```

在Redis中，存储的字符串都是以二进制的方式存储的；字符'a'的ASCII码时97，转为二进制是01100001，字符'b'
的ASCII码时98，转为二进制是01100010；二进制中的每一位都是offset偏移量的值，比如：字符'a'
的偏移量offset为0时值为0，offset为1时值为1；offset偏移量是从左往右计算的，也就是从高位往低位（偏移量从0开始计算）

我们可以通过SETBIT命令将emily中的字符'a'变为字符'b'，即将01100001变为01100010；

首先往redis库中插入字符'a'：

```sh
127.0.0.1:6379[1]> set emily 'a'
OK
127.0.0.1:6379[1]> get emily
"a"
```

然后将字符'a'（01100001）的第7位变成1，第8位变成0（offset分别是6和7）：

```sh
127.0.0.1:6379[1]> SETBIT emily 6 1
(integer) 0
127.0.0.1:6379[1]> SETBIT emily 7 0
(integer) 1
127.0.0.1:6379[1]> get emily
"b"
```

> 上述SETBIT命令操作完成后都会返回一个整数0或1，这其实是你操作之前offset位置上的比特值；

##### GETBIT用法

```sh
GETBIT key offset
```

> 获取指定键值的偏移量上的比特值；

示例：获取字符'b'（01100010）offset为2、6、7的比特值：

```sh
127.0.0.1:6379[1]> GETBIT emily 2
(integer) 1
127.0.0.1:6379[1]> GETBIT emily 6
(integer) 1
127.0.0.1:6379[1]> GETBIT emily 7
(integer) 0
```

##### BITCOUNT用法

```sh
BITCOUNT key [start end [BYTE | BIT]]
```

> 统计指定键和开始结束偏移量值为1的数量，offset偏移量单位默认是BYTE

示例：获取字符'b'（01100010）指定偏移量之间值为1的数量：

```sh
127.0.0.1:6379[1]> BITCOUNT emily 0 7
(integer) 3
127.0.0.1:6379[1]> BITCOUNT emily 0 1
(integer) 3
127.0.0.1:6379[1]> BITCOUNT emily 0 1 bit
(integer) 1
127.0.0.1:6379[1]> BITCOUNT emily 0 2 bit
(integer) 2
127.0.0.1:6379[1]> BITCOUNT emily 0 3 bit
(integer) 2
127.0.0.1:6379[1]> BITCOUNT emily 0 5 bit
(integer) 2
127.0.0.1:6379[1]> BITCOUNT emily 0 6 bit
(integer) 3
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)

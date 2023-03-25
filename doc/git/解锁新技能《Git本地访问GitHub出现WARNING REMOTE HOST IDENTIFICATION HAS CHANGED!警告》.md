#### 解锁新技能《Git本地访问GitHub出现WARNING: REMOTE HOST IDENTIFICATION HAS CHANGED!警告》

今天本地git访问github仓库的时候出现如下异常：

```sh
xx:spring-xx xx$ git push
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
@    WARNING: REMOTE HOST IDENTIFICATION HAS CHANGED!     @
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
IT IS POSSIBLE THAT SOMEONE IS DOING SOMETHING NASTY!
Someone could be eavesdropping on you right now (man-in-the-middle attack)!
It is also possible that a host key has just been changed.
The fingerprint for the RSA key sent by the remote host is
SHA256:uNiVztksCsDhcc0u9e8BujQXxxUpKZIDTMczCvj3tD2s.
Please contact your system administrator.
Add correct host key in /Users/xx/.ssh/known_hosts to get rid of this message.
Offending RSA key in /Users/xx/.ssh/known_hosts:2
RSA host key for github.com has changed and you have requested strict checking.
Host key verification failed.
fatal: Could not read from remote repository.
```

问题原因是SSH会把每个曾经访问过的Git服务器的公钥记录在/Users/xx/.ssh/known_hosts文件中，当下次访问时会核对公钥，如果和上次的记录不同，SSH就会发出警告。

解决方法：直接删除/Users/xx/.ssh/known_hosts文件。

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
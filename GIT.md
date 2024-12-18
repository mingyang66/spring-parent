### 打tag标签

##### 1.添加tag

```
git tag -a version1.0 -m 'first version'
```

##### 2.提交tag

```
git push origin --tags
```

##### 3.查看tag

- 列出所有的tag（按照字符排序，和创建时间没有关系）

```sh
git tag
```

- 列出符合规则的tag（只会列出指定版本的tag，如1.x）

```sh
git tag -l version.*
```

##### 4.删除tag

- 删除本地tag

```sh
git tag -d version
```

- 删除远程分支tag

```sh
git push origin --delete version
```

其它tag操作参考：[操作指南](https://blog.csdn.net/yaomingyang/article/details/78839295)

##### 5.如果文件已被Git跟踪（已经在版本控制中），则需要使用如下命令，然后在.gitignore文件中添加

```sh
git rm --cached 文件路径
```
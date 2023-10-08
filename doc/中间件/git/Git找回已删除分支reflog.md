#### Git找回已删除分支reflog

##### 一、查找被删除分支最后一次commit的id:

```
git reflog
```

##### 二、通过找到的commitId切换一个新的分支，即当时删除的分支

```
git checkout -b 分支名 commitId
```

GitHub地址：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
### Git误删分支找回

##### 1.首先查看提交的记录

```
git log -g
```

##### 2.查找被误删分支上最后提交的commit ID,跟回commit ID新建分支并将代码复制到新分支上

```
git branch new_branch commit-id
```

```
通过上面简单的两步就可以找回被误删除的分支
```

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/doc/git](https://github.com/mingyang66/spring-parent/tree/master/doc/git)
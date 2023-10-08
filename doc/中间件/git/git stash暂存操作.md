### git stash暂存操作

> 开发过程中经常会遇到正在做当前任务，突然又要切换到其它分支调试代码，又不想将当前代码提交，这时就可以使用暂存操作，等其它事情完成后可以回过头来恢复暂存的任务继续开发；

##### 1.暂存操作

```
#查看当前状态
git status
#如果有修改或新增代码可以使用暂存操作
git stash save '暂存标识'
```

##### 2.查看当前暂存操作记录

```
git stash list
```

##### 3.恢复暂存的工作

- pop命令恢复，恢复后暂存区域会删除当前的记录

```
#恢复指定的暂存工作, 暂存记录保存在list内,需要通过list索引index取出恢复
git stash pop stash@{index}
```

- apply命令恢复，恢复后，暂存区域会保留当前的记录

```
#恢复指定的暂存工作, 暂存记录保存在list内,需要通过list索引index取出恢复
git stash apply stash@{index}
```

##### 4.删除暂存记录

```
#删除某个暂存, 暂存记录保存在list内,需要通过list索引index取出恢复
git stash drop stash@{index}
#删除全部暂存
git stash clear
```

GitHub地址：[https://github.com/mingyang66/spring-parent/tree/master/doc/git](https://github.com/mingyang66/spring-parent/tree/master/doc/git)
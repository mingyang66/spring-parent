#### Mysql for update详解

##### 一、共享锁和排它锁

InnoDB实现了两种标准的行级锁，共享锁和排它锁。

- 共享锁：允许持有锁的事务读取一行数据；
- 排它锁：允许持有锁的事务更新或删除一行数据；

如果事务T1持有r行上的共享s锁，则来自不同事务T2对r行上锁的请求如下处理：

- T2对s锁的请求会被立马授予，结果T1和T2都在行r上保持s锁；
- T2对x锁的请求不会立即授予；

如果事务T1持有r行上的排它锁x，则来自不同事务T2对r行上锁的请求不会立马授予。相反，事务T2必须等待事务T1释放持有的r行上的锁。

##### 二、行锁与表锁

InnoDB引擎对for update语句默认是行级别的锁，当有明确指定的主键或索引的时候是行级锁，否则是表级锁，并且必须开启事务才有效；

1. 只根据主键进行查询，并且查询到数据，主键字段产生行锁

   ```sql
   SELECT * FROM sailboat s WHERE s.id=3 for UPDATE;
   ```

2. 只根据主键进行查询，未查询到数据，不产生锁

   ```sql
   SELECT * FROM sailboat s WHERE s.id=2 for UPDATE;
   ```

3. 根据主键、非主键含索引（name）进行查询，并且查询到数据，主键字段产生行锁，name字段产生行锁。

   ```sql
   SELECT * FROM sailboat s WHERE s.id=4 and name='小白兔' for UPDATE;
   ```

4. 根据主键、非主键含索引（name）进行查询，未查到数据，不产生索引

   ```sql
   SELECT * FROM sailboat s WHERE s.id=5 and name='小白兔' for UPDATE;
   ```

5. 根据主键、非主键不含索引（name）进行查询，并且查询到数据，主键字段产生行锁，name字段产生行锁

   ```sql
   SELECT * FROM sailboat s WHERE s.id=5 and name='小白兔' for UPDATE;
   ```

   

6. sdf

7. sdf

```sql
SELECT * FROM sailboat s WHERE s.id=3 for UPDATE;
```

##### 三、查询数据库锁（包括行锁和表锁）

```sql
mysql> show status like '%lock%';
+------------------------------------------+-------+
| Variable_name                            | Value |
+------------------------------------------+-------+
| Com_lock_instance                        | 0     |
| Com_lock_tables                          | 0     |
| Com_unlock_instance                      | 0     |
| Com_unlock_tables                        | 0     |
| Handler_external_lock                    | 34    |
| Innodb_row_lock_current_waits            | 0     |
| Innodb_row_lock_time                     | 95720 |
| Innodb_row_lock_time_avg                 | 19144 |
| Innodb_row_lock_time_max                 | 38754 |
| Innodb_row_lock_waits                    | 5     |
| Key_blocks_not_flushed                   | 0     |
| Key_blocks_unused                        | 6698  |
| Key_blocks_used                          | 0     |
| Locked_connects                          | 0     |
| Performance_schema_locker_lost           | 0     |
| Performance_schema_metadata_lock_lost    | 0     |
| Performance_schema_rwlock_classes_lost   | 0     |
| Performance_schema_rwlock_instances_lost | 0     |
| Performance_schema_table_lock_stat_lost  | 0     |
| Table_locks_immediate                    | 4     |
| Table_locks_waited                       | 0     |
+------------------------------------------+-------+
21 rows in set (0.00 sec)

```

##### 四、查看表锁的状态信息

```sql
mysql> show status like 'table%';
+----------------------------+-------+
| Variable_name              | Value |
+----------------------------+-------+
| Table_locks_immediate      | 11    |
| Table_locks_waited         | 0     |
| Table_open_cache_hits      | 4     |
| Table_open_cache_misses    | 9     |
| Table_open_cache_overflows | 0     |
+----------------------------+-------+
5 rows in set (0.00 sec)
```

##### 五、查看行锁状态信息

```sql
mysql> show status like 'InnoDB_row_lock%';
+-------------------------------+--------+
| Variable_name                 | Value  |
+-------------------------------+--------+
| Innodb_row_lock_current_waits | 0      |
| Innodb_row_lock_time          | 186709 |
| Innodb_row_lock_time_avg      | 23338  |
| Innodb_row_lock_time_max      | 51167  |
| Innodb_row_lock_waits         | 8      |
+-------------------------------+--------+
5 rows in set (0.00 sec)
```


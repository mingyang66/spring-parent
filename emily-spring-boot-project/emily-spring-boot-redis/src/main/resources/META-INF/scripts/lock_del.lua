-- 键值
local key = KEYS[1]
-- 锁标识
local value = ARGV[1]
-- 获取锁标识
local marker = redis.call('GET', key)
-- 如果标识一致，则释放锁；否则标注对应的锁已经释放
if marker == value then
    -- 删除已存在的键,不存在的 key 会被忽略
    return redis.call('DEL', key)
else
    return true
end
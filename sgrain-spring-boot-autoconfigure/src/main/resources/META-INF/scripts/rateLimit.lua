--限流大小
local limit = tonumber(ARGV[1])
--当前库中的键值
local current = tonumber(redis.call('get', KEYS[1]) or 0)
--如果超出限流大小
if current + 1 > limit then
  return 0
else
  --incrby命令将key中存储的数字加上指定的增量值，如果key不存在，那么key的值会先被初始化为0，然后再incr操作
  redis.call('incrby', KEYS[1], 1)
  --expire命令用于设置key的过期时间，key过期后将不再可用，单位：秒
  redis.call('expire', KEYS[1], ARGV[2])
  return current + 1
end
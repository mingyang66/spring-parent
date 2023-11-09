-- 列表键名
local key = KEYS[1]
-- 列表值
local value = ARGV[1]
-- 列表限制长度阀值
local threshold = tonumber(ARGV[2])
-- 超时时间，单位：秒
local expire = tonumber(ARGV[3] or '0')
-- 获取列表长度
local len = tonumber(redis.call('LLEN', key))
-- 根据列表长度与阀值比较
if len >= threshold then
    -- 移出并获取列表的第一个元素
    redis.call('LPOP', key)
end
-- 在列表中添加一个或多个值到列表尾部
redis.call('RPUSH', key, value)
-- 超时时间必须大于0，否则永久有效
if expire > 0 then
    -- 设置超时时间
    redis.call('EXPIRE', key, expire)
end
-- 返回列表长度
return redis.call('LLEN', key)
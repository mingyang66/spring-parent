-- 要限流的键名
local key = KEYS[1]
-- 限流阀值
local threshold = tonumber(ARGV[1])
-- 限流的时间窗口，单位：秒
local expire = tonumber(ARGV[2])
-- 当前访问数量
local current = tonumber(redis.call('GET', key) or "0")
-- 0：超过阀值 1：访问有效
if current + 1 > threshold then
    return 0
elseif current == 0 then
    redis.call('SET', key, 1)
    redis.call('EXPIRE', key, expire)
    return 1;
else
    redis.call('INCR', key)
    return 1
end

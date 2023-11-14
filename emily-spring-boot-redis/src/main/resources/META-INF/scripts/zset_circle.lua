-- 键名
local key = KEYS[1]
-- 分数
local score = tonumber(ARGV[1])
-- 值
local value = ARGV[2]
-- 阀值
local threshold = tonumber(ARGV[3])
-- 超时时间
local expire = tonumber(ARGV[4])

local success, result = pcall(function(key, score, value, threshold, expire)
    -- 返回有序成员的分数
    local exists = redis.call('ZSCORE', key, value)
    if not exists then
        -- 获取有序集合的成员数
        local len = tonumber(redis.call('ZCARD', key))
        if (len >= threshold) then
            -- 移除有序集合中给定的排名区间的所有成员
            redis.call('ZREMRANGEBYRANK', key, 0, 0)
        end
        -- 向有序集合添加一个或多个成员，或者更新已存在成员的分数
        redis.call('ZADD', key, score, value)
    end
    -- 设置过期时间
    redis.call('EXPIRE', key, expire)
end, key, score, value, threshold, expire)

-- 判定脚本是否执行成功
if success then
    return true
else
    return false
end
-- 游标位置
local cursor = tonumber(ARGV[1])
-- 一次查询出的数量
local count = tonumber(ARGV[2])
-- 匹配模式
local pattern = '*'
-- SCAN cursor [MATCH pattern] [COUNT count] 迭代数据库中的数据库键
local value = redis.call('SCAN', cursor, 'MATCH', pattern, 'COUNT', count)
-- 下次循环的游标
local nextCursor = value[1]
-- 当前批次的数据
local data = value[2]
-- 符合条件的数据集合
local result = {}
for i, key in ipairs(data) do
    -- 查询键对应过期时间
    local ttl = redis.call('TTL', key)
    if ttl == -1 then
        table.insert(result, key)
    end
end
return { nextCursor, result }

-- 判定列表中是否包含指定的value
local function contains_value(key, value)
    -- 获取列表指定范围内的所有元素
    local elements = redis.call('LRANGE', key, 0, -1)
    -- 泛型for迭代器
    for k, v in pairs(elements) do
        if  v == value then
            return true
        end
    end
    return false
end

-- pcall函数捕获多条指令执行时的异常
local success, result = pcall(function()
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

    -- 判定列表中是否包含value
    if not contains_value(key, value) then
        -- 根据列表长度与阀值比较
        if len >= threshold then
            -- 移出并获取列表的第一个元素
            redis.call('LPOP', key)
        end
        -- 在列表中添加一个或多个值到列表尾部
        redis.call('RPUSH', key, value)
    end
    -- 超时时间必须大于0，否则永久有效
    if expire > 0 then
        -- 设置超时时间
        redis.call('EXPIRE', key, expire)
    end
    -- 返回列表长度
    return redis.call('LLEN', key)
end)

if success then
    return result
else
    return -1
end
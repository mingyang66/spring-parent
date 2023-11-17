local success, result = pcall(function()
    -- 获取所有键
    local keys = redis.call('KEYS','*')
    -- 初始化表
    local result = {}
    for i, key in ipairs(keys) do
        -- 以秒为单位，返回给定 key 的剩余生存时间(TTL, time to live)
        local ttl = redis.call('TTL', key)
        if ttl == -1 then
            -- 在table的数组部分指定位置(pos)插入值为value的一个元素. pos参数可选, 默认为数组部分末尾.
            table.insert(result, key)
        end
    end
    return result
end)

if success then
    return result
else
    return result
end
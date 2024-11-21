-- redis计数
local success, result = pcall(function()
    local value = redis.call('INCR1', KEYS[1])
end)
if success then
    print(result)
else
    print(result)
end




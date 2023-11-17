-- 键
local key = KEYS[1]
-- 删除已存在的键,不存在的 key 会被忽略
local success = redis.call('DEL', key)
if success then
    return true
else
    return false
end
-- 键值
local key = KEYS[1]
-- 值
local value = ARGV[1]
-- 过期时间
local expire = ARGV[2]
-- SET key value [NX | XX] [GET] [EX seconds | PX milliseconds | EXAT unix-time-seconds | PXAT unix-time-milliseconds | KEEPTTL]
-- NX 仅当key不存在时才设置key
-- XX 仅当key已经存在时才设置key
local success = redis.call('SET', key, value, 'NX', 'EX', expire)
if success then
    return true
else
    return false
end
-- checkandset.lua
local current = redis.call('GET', KEYS[1])
redis.log(redis.LOG_WARNING, '当前的键值是')
if current ~= ARGV[1]
then
    redis.call('SET', KEYS[1], ARGV[2])
    return true
end
return false

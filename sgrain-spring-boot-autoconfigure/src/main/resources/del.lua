local key=KEYS[1]
if redis.call('del', key)==1 then
    return 1
else
    return 0
end
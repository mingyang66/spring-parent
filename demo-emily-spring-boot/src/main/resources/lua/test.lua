if redis.call('GET', KEYS[1]) == '1第一' then
    redis.call('SET', KEYS[1], ARGV[1] .. '第一');
else
    redis.call('SET', KEYS[1], ARGV[1] .. '第二');
end
return true;
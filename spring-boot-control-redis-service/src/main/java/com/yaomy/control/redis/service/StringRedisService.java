package com.yaomy.control.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description: Redis 字符串操作服务类
 * @ProjectName: EM.FrontEnd.PrivateEquity.electronic-contract
 * @Package: com.uufund.ecapi.config.redis.RedisBean
 * @Date: 2019/5/15 15:53
 * @Version: 1.0
 */
@Service
public class StringRedisService {
    /**
     * 操作字符串
     */
    @Autowired
    private StringRedisTemplate template;
    /**
     * @Description 设置键值对
     * @Date 2019/7/16 14:44
     * @Version  1.0
     */
    public Boolean set(String key, String val){
        try{
            template.opsForValue().set(key, val);
            return Boolean.TRUE;
        } catch (Exception e){
            return Boolean.FALSE;
        }
    }
    /**
     * @Description 设置键值对对应过期时间
     * @Date 2019/7/16 14:49
     * @Version  1.0
     */
    public Boolean set(String key, String val, Long timeOut, TimeUnit timeUnit){
        try{
            template.opsForValue().set(key, val, timeOut, timeUnit);
            return Boolean.TRUE;
        } catch (Exception e){
            return Boolean.FALSE;
        }
    }
    public String get(String key){
        return template.opsForValue().get(key);
    }

    public Boolean del(String key){
        try{
            return template.delete(key);
        } catch (Exception e){
            return Boolean.FALSE;
        }
    }
    public Long del(Collection<String> keys){
        try{
            return template.delete(keys);
        } catch (Exception e){
            return 0L;
        }
    }

    public Boolean exist(String key){
        try{
            return template.hasKey(key);
        } catch (Exception e){
            return Boolean.FALSE;
        }
    }
    public Boolean incrLong(String key){
        return incrLong(key, 1L, 0L, TimeUnit.SECONDS);
    }
    public Boolean incrLong(String key, Long val){
        return incrLong(key, val, 0L, TimeUnit.SECONDS);
    }
    public Boolean incrLong(String key, long val, long timeout, TimeUnit unit){
        try {
            template.opsForValue().increment(key, val);
            if(timeout > 0){
                template.expire(key, timeout, unit);
            }
            return Boolean.TRUE;
        } catch (Exception e){
            return  Boolean.FALSE;
        }
    }

    public Boolean incrDouble(String key){
        return incrDouble(key, 1D, 0L, TimeUnit.SECONDS);
    }
    public Boolean incrDouble(String key, Double val){
        return incrDouble(key, val, 0L, TimeUnit.SECONDS);
    }
    public Boolean incrDouble(String key, double val, long timeout, TimeUnit unit){
        try {
            template.opsForValue().increment(key, val);
            if(timeout > 0L){
                template.expire(key, timeout, unit);
            }
            return Boolean.TRUE;
        } catch (Exception e){
            return  Boolean.FALSE;
        }
    }
    /**
     * @Description 获取主键对应的有效时长, -1一直有效，-2键不存在
     * @param unit 单位
     * @Date 2019/6/6 13:05
     * @Version  1.0
     */
    public Long getExpire(String key, TimeUnit unit){
        return template.getExpire(key, unit);
    }
    /**
     * @Description 获取主键对应的有效时长,单位毫秒, -1一直有效，-2键不存在
     * @param key 主键
     * @Date 2019/6/6 13:06
     * @Version  1.0
     */
    public Long getExpire(String key){
        return template.getExpire(key, TimeUnit.MILLISECONDS);
    }
    /**
     * @Description 获取数据库key对应的键值
     * @Date 2019/7/5 10:43
     * @Version  1.0
     */
    public Set<String> getKeys(String pattern){
        return template.keys(pattern);
    }
}

package com.yaomy.security.oauth2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.service.RedisTokenRepositoryImpl
 * @Date: 2019/7/31 13:57
 * @Version: 1.0
 */
public class RedisTokenRepository implements PersistentTokenRepository {
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public void createNewToken(PersistentRememberMeToken token) {
        String key = generateKey(token.getSeries());
        HashMap<String, String> map = new HashMap();
        map.put("username", token.getUsername());
        map.put("tokenValue", token.getTokenValue());
        map.put("date", String.valueOf(token.getDate().getTime()));
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
    }

    @Override
    public void updateToken(String series, String tokenValue, Date date) {
        String key = generateKey(series);
        HashMap<String, String> map = new HashMap();
        map.put("tokenValue", tokenValue);
        map.put("date", String.valueOf(date.getTime()));
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, 1, TimeUnit.HOURS);
    }

    @Override
    public PersistentRememberMeToken getTokenForSeries(String seriesId) {
        String key = generateKey(seriesId);
        List<String> hashKeys = new ArrayList<>();
        hashKeys.add("username");
        hashKeys.add("tokenValue");
        hashKeys.add("date");
        List<String> hashValues = redisTemplate.opsForHash().multiGet(key, hashKeys);
        String username = hashValues.get(0);
        String tokenValue = hashValues.get(1);
        String date = hashValues.get(2);
        if (null == username || null == tokenValue || null == date) {
            return null;
        }
        Long timestamp = Long.valueOf(date);
        Date time = new Date(timestamp);
        PersistentRememberMeToken token = new PersistentRememberMeToken(username, seriesId, tokenValue, time);
        return token;
    }

    @Override
    public void removeUserTokens(String s) {

    }
    private String generateKey(String series) {
        return "spring:security:rememberMe:token:" + series;
    }
}

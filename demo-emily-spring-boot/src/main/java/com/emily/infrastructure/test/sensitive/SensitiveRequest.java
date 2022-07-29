package com.emily.infrastructure.test.sensitive;

import com.emily.infrastructure.common.sensitive.annotation.Sensitive;
import com.emily.infrastructure.common.sensitive.enumeration.Logic;
import com.emily.infrastructure.common.sensitive.enumeration.Strategy;

import java.util.List;
import java.util.Map;

/**
 * @Description :  脱敏字段测试
 * @Author :  Emily
 * @CreateDate :  Created in 2022/7/20 2:43 下午
 */
public class SensitiveRequest {
    @Sensitive(strategy = Strategy.ENTITY, logic = Logic.ID_CARD)
    private String username;
    @Sensitive
    private String password;
    @Sensitive
    private String idCard;
    @Sensitive
    private List<Map<String, Object>> list;
    @Sensitive
    private Map<String, Object> data;
    private StrategyPo strategy;

    public StrategyPo getStrategy() {
        return strategy;
    }

    public void setStrategy(StrategyPo strategy) {
        this.strategy = strategy;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
}

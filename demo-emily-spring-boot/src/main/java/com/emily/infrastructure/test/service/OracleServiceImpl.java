package com.emily.infrastructure.test.service;

import com.emily.infrastructure.test.mapper.OracleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2022/01/17
 */

@Service
public class OracleServiceImpl implements OracleService {
    @Autowired
    private OracleMapper oracleMapper;

    @Override
    public String getOracle() {
        return oracleMapper.getOracle();
    }
}

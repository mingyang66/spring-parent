package com.emily.infrastructure.test.service.impl;

import com.emily.infrastructure.test.mapper.MyOracleMapper;
import com.emily.infrastructure.test.mapper.OracleMapper;
import com.emily.infrastructure.test.service.context.MapperContext;
import com.emily.infrastructure.test.service.OracleService;
import org.springframework.stereotype.Service;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2022/01/17
 */
@Service
public class OracleServiceImpl implements OracleService {

    @Override
    public String getOracle() {
        return MapperContext.getBean(OracleMapper.class).getOracle();
    }

    @Override
    public String getTarget(String param) {
        return MapperContext.getBean(param, OracleMapper.class, MyOracleMapper.class).getOracle();
    }
}

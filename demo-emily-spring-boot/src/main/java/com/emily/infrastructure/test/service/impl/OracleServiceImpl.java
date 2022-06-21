package com.emily.infrastructure.test.service.impl;

import com.emily.infrastructure.test.mapper.mysql.MyOracleMapper;
import com.emily.infrastructure.test.mapper.oracle.OracleMapper;
import com.emily.infrastructure.test.service.OracleService;
import com.emily.infrastructure.test.service.factory.MapperFactory;
import org.springframework.stereotype.Service;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2022/01/17
 */
@Service
public class OracleServiceImpl implements OracleService {

    private MapperFactory<OracleMapper> factory = MapperFactory.newInstance(OracleMapper.class, MyOracleMapper.class);

    @Override
    public String getOracle() {
        return factory.getMapper().getOracle();
    }

    @Override
    public String getTarget(String param) {
        OracleMapper oracleMapper =factory.getMapper(param);
        return oracleMapper.getOracle();
    }
}

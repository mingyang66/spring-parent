package com.emily.infrastructure.test.service.pc;

import org.apache.ibatis.annotations.Mapper;

public interface Student {
    @Mapper
    String getName();
    String getPassword();
}

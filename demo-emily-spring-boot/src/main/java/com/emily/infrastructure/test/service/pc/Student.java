package com.emily.infrastructure.test.service.pc;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface Student {

    String getName();

    String getPassword();
}

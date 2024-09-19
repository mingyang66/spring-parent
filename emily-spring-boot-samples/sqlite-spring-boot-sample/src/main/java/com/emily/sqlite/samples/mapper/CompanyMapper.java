package com.emily.sqlite.samples.mapper;

import org.apache.ibatis.annotations.Mapper;

/**
 * @author :  姚明洋
 * @since :  2024/9/18 下午7:02
 */
@Mapper
public interface CompanyMapper {
    void insertCompany(long id);
    long getCompanyId();
}

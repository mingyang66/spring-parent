package com.emily.infrastructure.datasource.mapper;


import com.emily.infrastructure.datasource.annotation.TargetDataSource;
import com.emily.infrastructure.datasource.po.Node;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Emily
 * @Description: Description
 * @Version: 1.0
 */
@Mapper
public interface NodeMapper {
    /**
     * 查询接口
     */
    @TargetDataSource("default")
    Node findNode();
}

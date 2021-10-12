package com.emily.infrastructure.test.mapper;


import com.emily.infrastructure.datasource.annotation.TargetDataSource;
import com.emily.infrastructure.test.po.Node;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Emily
 * @Description: Description
 * @Version: 1.0
 */
@Mapper
public interface Node1Mapper {
    /**
     * 查询接口
     */
    @TargetDataSource("slave")
    Node findNode();
}

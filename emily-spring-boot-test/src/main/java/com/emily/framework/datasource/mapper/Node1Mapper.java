package com.emily.framework.datasource.mapper;


import com.emily.framework.datasource.annotation.TargetDataSource;
import com.emily.framework.datasource.po.Node;
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

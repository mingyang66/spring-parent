package com.emily.infrastructure.test.mapper;

import com.emily.infrastructure.test.po.Item;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/09/08
 */
@Mapper
public interface ItemMapper {
    int inertByBatch(@Param("list") List<Item> list);

    int insertItem(@Param("scheName") String scheName, @Param("lockName") String lockName);
}

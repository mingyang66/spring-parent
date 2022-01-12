package com.emily.infrastructure.test.mapper;

import com.emily.infrastructure.test.po.Job;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChildMapper extends ParentMapper{

    Job getJob();
}

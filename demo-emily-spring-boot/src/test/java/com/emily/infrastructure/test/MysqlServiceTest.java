package com.emily.infrastructure.test;

import com.emily.infrastructure.test.mapper.mysql.MysqlMapper;
import com.emily.infrastructure.test.po.World;
import com.emily.infrastructure.test.service.impl.MysqlServiceImpl;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.mockito.Mockito.when;


/**
 * @author :  Emily
 * @since :  2023/11/29 10:27 PM
 */
@SpringBootTest
public class MysqlServiceTest {
    @Autowired
    private MysqlServiceImpl mysqlService;
    @MockBean
    private MysqlMapper mysqlMapper;

    @Test
    public void getMysql() {
        World world = new World();
        world.password = "123";
        when(mysqlMapper.getMysql("田晓霞", "123456")).thenReturn(Lists.newArrayList(world));
        List<World> list = mysqlService.getMysql();
        Assertions.assertEquals(list.get(0).password, "123");
    }
}

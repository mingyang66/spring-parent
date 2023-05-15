package com.emily.infrastructure.common.test;

import com.emily.infrastructure.common.date.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @Description :  日期单元测试
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/14 3:27 PM
 */
public class DateUtilsTest {

    @Test
    public void asDate() {
        Assert.assertNotNull(DateUtils.asDate(LocalDateTime.now()));
        Assert.assertNotNull(DateUtils.asDate(LocalDate.now()));
    }

    @Test
    public void asLocalDateXX() {
        Assert.assertNotNull(DateUtils.asLocalDateTime(new Date()));
        Assert.assertNotNull(DateUtils.asLocalDate(new Date()));
        Assert.assertNotNull(DateUtils.asLocalTime(new Date()));
    }
}

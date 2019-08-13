package com.yaomy.common.inject;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.common.inject.TeacherServiceImpl
 * @Author: 姚明洋
 * @Date: 2019/8/12 14:08
 * @Version: 1.0
 */
@Service
@Order(2)
public class TeacherServiceImpl implements Persion {
    @Override
    public String getName() {
        return "Teacher";
    }
}

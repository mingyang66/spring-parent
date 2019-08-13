package com.yaomy.common.inject;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.common.inject.StudentServiceImpl
 * @Author: 姚明洋
 * @Date: 2019/8/12 14:07
 * @Version: 1.0
 */
@Service
@Order(1)
public class StudentServiceImpl implements Persion {
    @Override
    public String getName() {
        return "Student";
    }
}

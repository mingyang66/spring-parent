package com.yaomy.control.common.inject;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @Description: Description
 * @ProjectName: spring-parent
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

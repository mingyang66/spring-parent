package com.yaomy.sgrain.common.inject;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

/**
 * @Description: Description
 * @ProjectName: spring-parent
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

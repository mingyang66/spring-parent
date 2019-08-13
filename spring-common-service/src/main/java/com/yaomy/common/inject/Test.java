package com.yaomy.common.inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.common.inject.Test
 * @Date: 2019/8/12 14:09
 * @Version: 1.0
 */
@Service
public class Test {
    @Autowired
    @Qualifier("studentServiceImpl")
    private Persion persion;
}

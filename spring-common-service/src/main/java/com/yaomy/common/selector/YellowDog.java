package com.yaomy.common.selector;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.common.selector.YellowDog
 * @Date: 2019/8/6 10:54
 * @Version: 1.0
 */
public class YellowDog implements Dog {
    @Override
    public String dogName() {
        return "黄色Dog";
    }
}

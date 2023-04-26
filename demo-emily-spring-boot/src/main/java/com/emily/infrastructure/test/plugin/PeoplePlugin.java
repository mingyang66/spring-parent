package com.emily.infrastructure.test.plugin;

import org.springframework.plugin.core.Plugin;

/**
 * @Description :  人类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/4/26 1:48 PM
 */
public interface PeoplePlugin extends Plugin<PeoplePluginType> {
    String eat();
}

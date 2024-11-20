package com.emily.infrastructure.sample.web.config.plugin;

import org.springframework.plugin.core.Plugin;

/**
 * 人类
 *
 * @author Emily
 * @since Created in 2023/4/26 1:48 PM
 */
public interface PeoplePlugin extends Plugin<PeoplePluginType> {
    String eat();
}

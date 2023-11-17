package com.emily.infrastructure.test.plugin.controller;

import com.emily.infrastructure.test.plugin.PeoplePlugin;
import com.emily.infrastructure.test.plugin.PeoplePluginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.plugin.core.config.EnablePluginRegistries;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 控制器插件
 *
 * @author Emily
 * @since Created in 2023/4/26 1:54 PM
 */
@EnablePluginRegistries(value = PeoplePlugin.class)
@RestController
@RequestMapping("api/plugin")
public class PluginController {
    @Autowired
    private PluginRegistry<PeoplePlugin, PeoplePluginType> pluginRegistry;

    @GetMapping("eat")
    public void eat() {
        List<PeoplePlugin> list = pluginRegistry.getPlugins();
        for (PeoplePlugin people : list) {
            String s = people.eat();
            System.out.println(s);
        }
    }
}

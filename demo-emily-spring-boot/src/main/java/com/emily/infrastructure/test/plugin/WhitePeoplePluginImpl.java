package com.emily.infrastructure.test.plugin;

import org.springframework.stereotype.Service;

/**
 * @Description :  白种人
 * @Author :  Emily
 * @CreateDate :  Created in 2023/4/26 1:52 PM
 */
@Service
public class WhitePeoplePluginImpl implements PeoplePlugin {
    @Override
    public String eat() {
        return "面包";
    }

    @Override
    public boolean supports(PeoplePluginType peopleType) {
        return PeoplePluginType.WHITE.equals(peopleType);
    }
}

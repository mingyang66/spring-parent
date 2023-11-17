package com.emily.infrastructure.test.plugin;

import org.springframework.stereotype.Service;

/**
 * 黄种人
 *
 * @author Emily
 * @since Created in 2023/4/26 1:50 PM
 */
@Service
public class YellowPeoplePluginImpl implements PeoplePlugin {
    @Override
    public String eat() {
        return "馒头、大米";
    }

    @Override
    public boolean supports(PeoplePluginType peopleType) {
        return PeoplePluginType.YELLOW.equals(peopleType);
    }
}

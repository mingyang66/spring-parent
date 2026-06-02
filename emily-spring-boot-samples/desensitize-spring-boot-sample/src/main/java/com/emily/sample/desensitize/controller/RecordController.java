package com.emily.sample.desensitize.controller;

import com.emily.sample.desensitize.entity.RecordPeople;
import com.emily.sample.desensitize.entity.RecordPeopleBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * record controller
 * @author Emily
 */
@RestController
public class RecordController {
    @PostMapping("api/desensitize/record/index")
    public RecordPeople index() {
        return RecordPeopleBuilder.builder()
                .username("username")
                .password("password")
                .email("email")
                .phone("phone")
                .idCard("idCard")
                .address("address")
                .build();
    }
}

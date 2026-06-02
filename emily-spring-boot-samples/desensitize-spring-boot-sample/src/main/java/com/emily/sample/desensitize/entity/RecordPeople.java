package com.emily.sample.desensitize.entity;

import io.soabase.recordbuilder.core.RecordBuilder;

@RecordBuilder
public record RecordPeople(String username, String password, String email, String phone, String idCard,
                           String address) {
}

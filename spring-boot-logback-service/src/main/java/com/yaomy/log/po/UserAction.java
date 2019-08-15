package com.yaomy.log.po;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE)
public class UserAction implements Serializable {
    //重命名
    @JsonProperty("NUMBER")
    private String number;
    @JsonProperty("USERNAME")
    private String username;
}

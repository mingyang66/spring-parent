package com.emily.sample.i18n.entity;

import com.emily.infrastructure.language.annotation.I18nModel;
import com.emily.infrastructure.language.annotation.I18nProperty;

/**
 * @author :  Emily
 * @since :  2024/10/31 下午1:53
 */
@I18nModel
public class Bank {
    @I18nProperty
    private String name;
    @I18nProperty
    private String code;

    private SubBank subBank;

    public SubBank getSubBank() {
        return subBank;
    }

    public void setSubBank(SubBank subBank) {
        this.subBank = subBank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @I18nModel
    public static class SubBank {
        @I18nProperty
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

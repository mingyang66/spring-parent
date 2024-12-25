package com.emily.sample.i18n.controller;

import com.emily.infrastructure.i18n.annotation.I18nOperation;
import com.emily.sample.i18n.entity.PluginField;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author :  Emily
 * @since :  2024/12/25 下午10:58
 */
@RestController
public class I18nPluginController {
    @I18nOperation
    @GetMapping("api/i18n/getField")
    public PluginField getField() {
        return new PluginField();
    }

    @I18nOperation
    @GetMapping("api/i18n/getFieldStr")
    public PluginField getFieldStr() {
        PluginField field = new PluginField();
        field.setFieldName("田晓霞");
        return field;
    }

    @I18nOperation
    @GetMapping("api/i18n/getFieldList")
    public PluginField getFieldList() {
        PluginField field = new PluginField();
        field.setStringList(List.of("孙少平"));
        return field;
    }
}

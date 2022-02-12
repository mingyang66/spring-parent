package com.emily.boot.test.api.i18n;

import com.emily.infrastructure.autoconfigure.response.annotation.ApiWrapperIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/12/16
 */
@RestController
public class I18nController {

    @Autowired
    private MessageSource messageSource;

    @ApiWrapperIgnore
    @GetMapping("/i18n/test")
    public String test2() {
        return "success-" + messageSource.getMessage("spring.emily.username", new String[]{"1", "2"}, LocaleContextHolder.getLocale());
    }
}

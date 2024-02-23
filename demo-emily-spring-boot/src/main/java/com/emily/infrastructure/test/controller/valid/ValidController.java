package com.emily.infrastructure.test.controller.valid;

import com.emily.infrastructure.autoconfigure.valid.annotation.IsAccountCode;
import com.emily.infrastructure.core.helper.RequestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author :  Emily
 * @since :  2023/12/24 1:40 PM
 */
@RestController
@RequestMapping("api/valid")
public class ValidController {
    @PostMapping("test")
    public void testValid(@Validated @RequestBody ValidReq strAn) {
        System.out.println("---" + RequestUtils.getClientIp());
        System.out.println("---" + RequestUtils.getRealClientIp());
    }

    @PostMapping("test2")
    public void testValid2(@Validated @RequestBody BeforeDate strAn) {
        System.out.println("---" + RequestUtils.getClientIp());
    }

    @PostMapping("length")
    public void testLength(@Validated @RequestBody LengthReq strAn) {
        System.out.println("---" + RequestUtils.getClientIp());
    }

    @PostMapping("accountCode")
    public AccountCodeEntity testAccountCode(@Validated @RequestBody AccountCodeEntity entity) {
        return entity;
    }

    @PostMapping("accountCodeGet")
    public String testAccountCodeGet(@Validated @IsAccountCode(minLength = 8, type = Long.class) String accountCode) {
        return accountCode;
    }
}

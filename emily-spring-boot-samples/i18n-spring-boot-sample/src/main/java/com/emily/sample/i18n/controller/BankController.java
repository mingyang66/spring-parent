package com.emily.sample.i18n.controller;

import com.emily.infrastructure.i18n.annotation.I18nOperation;
import com.emily.sample.i18n.entity.Bank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author :  姚明洋
 * @since :  2024/10/31 下午1:52
 */
@RestController
public class BankController {
    @I18nOperation
    @GetMapping("api/i18n/getBank")
    public Bank getBank() {
        Bank bank = new Bank();
        bank.setCode("古北");
        bank.setName("渣渣银行");
        return bank;
    }

    @I18nOperation
    @GetMapping("api/i18n/getStr")
    public String getStr() {
        return "古北";
    }
}

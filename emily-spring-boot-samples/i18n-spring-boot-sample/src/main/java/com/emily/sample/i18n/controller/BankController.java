package com.emily.sample.i18n.controller;

import com.emily.infrastructure.i18n.annotation.I18nOperation;
import com.emily.sample.i18n.entity.Bank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author :  Emily
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
        Bank.SubBank subBank = new Bank.SubBank();
        subBank.setName("渣渣银行");
        bank.setSubBank(subBank);
        return bank;
    }

    @I18nOperation
    @GetMapping("api/i18n/getStr")
    public String getStr() {
        return "古北";
    }

    @I18nOperation
    @GetMapping("api/i18n/getBankList")
    public List<Bank> getBankList() {
        Bank bank = new Bank();
        bank.setCode("古北");
        bank.setName("渣渣银行");
        Bank.SubBank subBank = new Bank.SubBank();
        subBank.setName("渣渣银行");
        bank.setSubBank(subBank);
        return List.of(bank);
    }

    /**
     * 不支持
     */
    @I18nOperation
    @GetMapping("api/i18n/getBankListStr")
    public List<String> getBankListStr() {
        return List.of("古北", "渣渣银行");
    }

    @I18nOperation
    @GetMapping("api/i18n/getBankMap")
    public Map<String, Bank> getBankMap() {
        Bank bank = new Bank();
        bank.setCode("古北");
        bank.setName("渣渣银行");
        Bank.SubBank subBank = new Bank.SubBank();
        subBank.setName("渣渣银行");
        bank.setSubBank(subBank);
        return Map.of("test1", bank);
    }

    /**
     * 不支持
     */
    @I18nOperation
    @GetMapping("api/i18n/getBankMapStr")
    public Map<String, String> getBankMapStr() {
        return Map.of("test1", "古北", "test2", "渣渣银行");
    }

    @I18nOperation
    @GetMapping("api/i18n/getBankArray")
    public Bank[] getBankArray() {
        Bank bank = new Bank();
        bank.setCode("古北");
        bank.setName("渣渣银行");
        Bank.SubBank subBank = new Bank.SubBank();
        subBank.setName("渣渣银行");
        bank.setSubBank(subBank);
        return new Bank[]{bank};
    }

    /**
     * 不支持
     */
    @I18nOperation
    @GetMapping("api/i18n/getBankArrayStr")
    public String[] getBankArrayStr() {
        return new String[]{"古北", "渣渣银行"};
    }
}

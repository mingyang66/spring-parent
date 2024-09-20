package com.emily.sqlite.samples.controller;

import com.emily.sqlite.samples.mapper.CompanyMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author :  Emily
 * @since :  2024/9/18 下午7:57
 */
@RestController
public class TestController {
    private final CompanyMapper companyMapper;

    public TestController(CompanyMapper companyMapper) {
        this.companyMapper = companyMapper;
    }

    @PostMapping("api/sqlite/insertCompany")
    public void insertCompany() {
        long id = companyMapper.getCompanyId();
        companyMapper.insertCompany(id + 1);
    }
}

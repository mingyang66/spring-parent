package com.emily.sample.desensitize.controller;

import com.emily.infrastructure.desensitize.annotation.DesensitizeOperation;
import com.emily.sample.desensitize.entity.Company;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author :  Emily
 * @since :  2024/12/7 下午4:13
 */
@RestController
@RequestMapping("api/desensitize")
public class DesensitizeController {
    @DesensitizeOperation
    @GetMapping("getCompany")
    public Company getCompany() {
        Company company = new Company();
        company.setCompanyName("魔方科技");
        company.setAddress("古北市南京路1688号50号楼106");
        company.setPhone("18888888888");
        company.setEmail("18888888888@qq.com");
        return company;
    }

    @DesensitizeOperation(removePackClass = ResponseEntity.class)
    @GetMapping("getCompany1")
    public ResponseEntity<Company> getCompany1() {
        Company company = new Company();
        company.setCompanyName("魔方科技");
        company.setAddress("古北市南京路1688号50号楼106");
        company.setPhone("18888888888");
        company.setEmail("18888888888@qq.com");
        return ResponseEntity.ok(company);
    }
}

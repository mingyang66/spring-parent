package com.emily.sample.desensitize.controller;

import com.emily.infrastructure.desensitize.annotation.DesensitizeOperation;
import com.emily.infrastructure.web.response.entity.BaseResponse;
import com.emily.sample.desensitize.entity.Company;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author :  Emily
 * @since :  2024/12/7 下午4:13
 */
@DesensitizeOperation(removePackClass = ResponseEntity.class)
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
        company.getDataMap().put("password", "123456");
        company.getDataMap().put("username", "兰兰");
        company.setTestNull(100D);
        company.setFieldKey("test");
        company.setFieldValue("188888888888@qq.com");
        company.setList(List.of("123", "456", "789"));
        company.setArrays(new String[]{"123", "456", "789"});
        return company;
    }

    @GetMapping("getCompany1")
    public ResponseEntity<Company> getCompany1() {
        Company company = new Company();
        company.setCompanyName("魔方科技");
        company.setAddress("古北市南京路1688号50号楼106");
        company.setPhone("18888888888");
        company.setEmail("18888888888@qq.com");
        company.getDataMap().put("password", "123456");
        company.getDataMap().put("username", "兰兰");
        company.setTestNull(100D);
        company.setFieldKey("email");
        company.setFieldValue("188888888888@qq.com");
        company.setList(List.of("123", "456", "789"));
        company.setArrays(new String[]{"123", "456", "789"});
        return ResponseEntity.ok(company);
    }

    @DesensitizeOperation(removePackClass = BaseResponse.class)
    @GetMapping("getCompany2")
    public BaseResponse<Company> getCompany2() {
        Company company = new Company();
        company.setCompanyName("魔方科技");
        company.setAddress("古北市南京路1688号50号楼106");
        company.setPhone("18888888888");
        company.setEmail("18888888888@qq.com");
        company.getDataMap().put("password", "123456");
        company.getDataMap().put("username", "兰兰");
        BaseResponse<Company> baseResponse = new BaseResponse<>();
        baseResponse.setData(company);
        company.setTestNull(100D);
        company.setFieldKey("phone");
        company.setFieldValue("188888888888");
        company.setList(List.of("123", "456", "789"));
        company.setArrays(new String[]{"123", "456", "789"});
        return baseResponse;
    }
}

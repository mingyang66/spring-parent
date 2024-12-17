package com.emily.sample.desensitize.controller;

import com.emily.infrastructure.desensitize.annotation.DesensitizeOperation;
import com.emily.infrastructure.web.response.entity.BaseResponse;
import com.emily.sample.desensitize.entity.Company;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author :  Emily
 * @since :  2024/12/7 下午4:13
 */
@RestController
public class DesensitizeController {
    @DesensitizeOperation
    @GetMapping("api/desensitize/getCompany")
    public Company getCompany() {
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
        return company;
    }

    @DesensitizeOperation
    @GetMapping("api/desensitize/getCompanyStr")
    public String getCompanyStr() {
        return "魔方科技";
    }

    @DesensitizeOperation(removePackClass = ResponseEntity.class)
    @GetMapping("api/desensitize/getCompanyList")
    public ResponseEntity<List<Company>> getCompanyList() {
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
        return ResponseEntity.ok(List.of(company));
    }

    @DesensitizeOperation(removePackClass = ResponseEntity.class)
    @GetMapping("api/desensitize/getCompanyListStr")
    public ResponseEntity<List<String>> getCompanyListStr() {
        return ResponseEntity.ok(List.of("古北市南京路1688号50号楼106"));
    }

    @DesensitizeOperation(removePackClass = ResponseEntity.class)
    @GetMapping("api/desensitize/getCompanyMap")
    public ResponseEntity<Map<String, Company>> getCompanyMap() {
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
        return ResponseEntity.ok(Map.of("test", company));
    }

    @DesensitizeOperation(removePackClass = ResponseEntity.class)
    @GetMapping("api/desensitize/getCompanyMapStr")
    public ResponseEntity<Map<String, String>> getCompanyMapStr() {
        return ResponseEntity.ok(Map.of("test", "魔方科技"));
    }

    @DesensitizeOperation(removePackClass = ResponseEntity.class)
    @GetMapping("api/desensitize/getCompanyArray")
    public ResponseEntity<Company[]> getCompanyArray() {
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
        return ResponseEntity.ok(new Company[]{company});
    }

    @DesensitizeOperation(removePackClass = ResponseEntity.class)
    @GetMapping("api/desensitize/getCompanyArrayStr")
    public ResponseEntity<String[]> getCompanyArrayStr() {
        return ResponseEntity.ok(new String[]{"魔方科技"});
    }

    @DesensitizeOperation(removePackClass = {BaseResponse.class, ResponseEntity.class, ResponseEntity.class})
    @GetMapping("api/desensitize/getCompanyPack")
    public BaseResponse<ResponseEntity<ResponseEntity<Company>>> getCompanyPack() {
        Company company = new Company();
        company.setCompanyName("魔方科技");
        company.setAddress("古北市南京路1688号50号楼106");
        company.setPhone("18888888888");
        company.setEmail("18888888888@qq.com");
        company.getDataMap().put("password", "123456");
        company.getDataMap().put("username", "兰兰");
        BaseResponse<ResponseEntity<ResponseEntity<Company>>> baseResponse = new BaseResponse<>();
        baseResponse.setData(ResponseEntity.ok(ResponseEntity.ok(company)));
        company.setTestNull(100D);
        company.setFieldKey("phone");
        company.setFieldValue("188888888888");
        company.setList(List.of("123", "456", "789"));
        company.setArrays(new String[]{"123", "456", "789"});
        return baseResponse;
    }

}

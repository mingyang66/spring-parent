package com.yaomy.control.exception.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Version: 1.0
 */
@RestController
public class TestController {
    @RequestMapping(value = "/test/get_name", method = RequestMethod.POST)
    public ResponseEntity<String> getName(){
        String s = null;
       // s.length();
       // throw new Exception();
        return ResponseEntity.ok("SUCCESS");
    }
}

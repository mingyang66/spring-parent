package com.yaomy.control.test.api;

import com.yaomy.control.common.control.conf.PropertyService;
import com.yaomy.control.common.control.po.BaseResponse;
import com.yaomy.control.logback.po.UserAction;
import com.yaomy.control.logback.utils.LoggerUtil;
import com.yaomy.control.rest.client.HttpClientService;
import com.yaomy.control.test.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @Description 测试类
 * @Date 2019/8/19 11:32
 * @Version  1.0
 */
@RestController
public class HandlerController {
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private HttpClientService httpClientService;

    @RequestMapping(value = "/handler/test")
    public HttpHeaders getName(@RequestBody @Valid User user, HttpServletResponse response) throws IOException {
        response.setStatus(201);
        response.setContentType("application/json");
        LoggerUtil.info(HandlerController.class, "测试。。。");
        //return ResponseEntity.ok(BaseResponse.createResponse(10006, "自定义测试", user));
        response.getOutputStream().print("this is body");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("name", "12");
        httpHeaders.add("age", "12");
        return httpHeaders;
    }
    @RequestMapping(value = "/handler/test1")
    public BaseResponse testNull(@RequestBody @Valid User user, Map<String, Object> map){
        LoggerUtil.info(HandlerController.class, "测试。。。");
        System.out.println(user);
        System.out.println(map);
        System.out.println("-------------------"+user.getWeight()[1]);
        return BaseResponse.createResponse(1004, "sfsdf");
    }
    @RequestMapping(value = "/handler/test2")
    public ResponseEntity<List> testNull1(@RequestBody @Valid User user){
        System.out.println("----------------deee");
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("name", "666");
        map.put("age", 88);
        list.add(map);
        return ResponseEntity.ok(list);
    }
    @RequestMapping(value = "/handler/upload")
    public String upload(String fileName, MultipartFile jarFile) {
        System.out.println(fileName+"==============");
        System.out.println(jarFile.getOriginalFilename());
        // 下面是测试代码
        String originalFilename = jarFile.getOriginalFilename();
        //System.out.println(originalFilename);
        try {
            String string = new String(jarFile.getBytes(), "UTF-8");
            System.out.println(string+"---------------");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // TODO 处理文件内容...
        return "OK";
    }
    @RequestMapping(value = "/handler/test3")
    public void testNull13(@RequestBody @Valid User user){
        LoggerUtil.info(HandlerController.class, propertyService.getProperty("sms.code")+"---"+propertyService.getProperty("sms.message"));
        UserAction userAction = new UserAction();
        userAction.setNumber("12222");
        userAction.setUsername("hhhhhhh");
        LoggerUtil.user(userAction);
        System.out.println("----------------deee");
    }
    @RequestMapping(value = "/handler/url")
    public void testUrl(@RequestBody @Valid User user){
        String url = "https://peecapi.uufund.com/seal_label/list";
        Map<String, Object> params = new HashMap<>();
        params.put("token", "8b6deca08f174355a0da393fbf182a44");
      /*  params.put("age", 12);
        params.put("weight", Arrays.asList(12,34,66));*/
        Map<String, Object> result = httpClientService.postMulti(url, params, Map.class);
        System.out.println(result);
    }
    @RequestMapping(value = "/handler/test4")
    public void testUrl1(@RequestBody @Valid User user) throws IOException{
        String url = "http://172.30.67.122:9000/handler/upload";
        FileSystemResource resource = new FileSystemResource(new File("D:\\work\\ssr\\pac.txt"));
        FileSystemResource resource1 = new FileSystemResource(new File("D:\\work\\ssr\\gui-config.json"));
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.put("jarFile", Arrays.asList(resource, resource1));
        params.put("fileName", Arrays.asList("liming", "hello"));
        String result = httpClientService.postMulti(url, params, String.class);
        System.out.println(result);
    }
}

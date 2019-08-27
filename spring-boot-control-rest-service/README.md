### Spring boot之 RestTemplate组件
>RestTemplate是Spring用于同步client端的核心类，简化了与http服务的通信，并满足RestFul原则，程序代码可以给它提供URL，
并提取结果。默认情况下，RestTemplate默认依赖jdk的HTTP连接工具。当然你也可以 通过setRequestFactory属性切换到不同的HTTP源，
比如Apache HttpComponents、Netty和OkHttp
#### 1.简介
RestTemplate默认使用SimpleClientHttpRequestFactory和DefaultResponseErrorHandler来分别处理HTTP的创建和错误，但也可以通过setRequestFactory和setErrorHandler来覆盖;
默认提供了多种的请求方式，本示例只提供通用的方式postForEntity、getForEntity
#### 2.组装代码：
>支持POST、GET请求，支持文件上传、支持数组参数传递、支持HTTP、HTTPS
```
package com.yaomy.control.rest.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @Description: 发送Http请求Client服务类
 * @Version: 1.0
 */
@Component
public class HttpClientService {

    @Autowired
    private RestTemplate restTemplate;
    /**
     * @Description 支持参数为非数组模式POST请求
     * @Version  1.0
     */
    public <T> T  post(String url, Map<String, Object> params, Class<T> responseType){
        ResponseEntity<T> entity = restTemplate.postForEntity(url, new HttpEntity(params), responseType);
        return entity.getBody();
    }
    /**
     * @Description 支持参数为非数组模式POST请求
     * @Version  1.0
     */
    public <T> T  post(String url, Map<String, Object> params, Class<T> responseType, Object... uriVariables){
        ResponseEntity<T> entity = restTemplate.postForEntity(url, new HttpEntity(params), responseType, uriVariables);
        return entity.getBody();
    }
    /**
     * @Description 支持参数为非数组模式POST请求
     * @Version  1.0
     */
    public<T> T  post(String url, Map<String, Object> params, Class<T> responseType, Map<String, ?> uriVariables){
        ResponseEntity<T> entity = restTemplate.postForEntity(url, new HttpEntity(params), responseType, uriVariables);
        return entity.getBody();
    }
    /**
     * @Description 支持文件上传、参数为数组模式、参数为非数组模式
     * @Version  1.0
     */
    public <T> T postMulti(String url, MultiValueMap<String, Object> params, Class<T> responseType){
        ResponseEntity<T> entity = restTemplate.postForEntity(url, new HttpEntity(params), responseType);
        return entity.getBody();
    }
    /**
     * @Description 支持文件上传、参数为数组模式、参数为非数组模式
     * @Version  1.0
     */
    public <T> T  postMulti(String url, Map<String, Object> params, Class<T> responseType, Object... uriVariables){
        ResponseEntity<T> entity = restTemplate.postForEntity(url, new HttpEntity(params), responseType, uriVariables);
        return entity.getBody();
    }
    /**
     * @Description 支持文件上传、参数为数组模式、参数为非数组模式
     * @Version  1.0
     */
    public<T> T  postMulti(String url, Map<String, Object> params, Class<T> responseType, Map<String, ?> uriVariables){
        ResponseEntity<T> entity = restTemplate.postForEntity(url, new HttpEntity(params), responseType, uriVariables);
        return entity.getBody();
    }
    /**
     * -----------------------------GET----------------------------------------------
     */
    /**
     * @Description 支持参数为非数组模式POST请求
     * @Version  1.0
     */
    public <T> T  get(String url, Class<T> responseType, Object... uriVariables){
        ResponseEntity<T> entity = restTemplate.getForEntity(url, responseType, uriVariables);
        return entity.getBody();
    }
    /**
     * @Description 支持参数为非数组模式POST请求
     * @Version  1.0
     */
    public<T> T  get(String url, Class<T> responseType, Map<String, ?> uriVariables){
        ResponseEntity<T> entity = restTemplate.getForEntity(url, responseType, uriVariables);
        return entity.getBody();
    }
}
```

#### 3.Rest HTTP Client请求组件【spring-boot-control-rest-service】

* 使用方式,示例如下
```
    @Autowired
    private HttpClientService httpClientService;
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
```
>支持文件上传

#### 4.文件上传接收文件接口
```
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
        return "OK";
    }
```

GitHub源码：[https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rest-service](https://github.com/mingyang66/spring-parent/tree/master/spring-boot-control-rest-service)


package com.yaomy.control.test.api.sgrain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sgrain.boot.autoconfigure.web.annotation.ApiPrefix;
import com.sgrain.boot.common.po.ResponseData;
import com.sgrain.boot.common.utils.json.JSONUtils;
import com.sgrain.boot.context.httpclient.HttpClientService;
import com.yaomy.control.test.po.GmFundPageInfoResData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: spring-parent
 * @description: 网络请求控制器
 * @author:
 * @create: 2020/04/23
 */
@ApiPrefix(ignore = true)
@RestController
@RequestMapping("network")
public class HttpClientController {

    @Autowired
    @Lazy
    private HttpClientService httpClientService;
    @GetMapping("responseData")
    public String testResponseData(){
        String url = "http://127.0.0.1:8108/questionnaire/get";
        Map<String, Object> paramMap = Maps.newHashMap();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        ResponseData responseData = httpClientService.post(url, paramMap, null, ResponseData.class);
        return null;
    }
    @GetMapping("responseData1")
    public ResponseEntity<List<Map<Long, Map<Integer, Integer>>>> getResult(){
        List<Map<Long, Map<Integer, Integer>>> result = Lists.newArrayList();
        Map<Long, Map<Integer, Integer>> map = Maps.newHashMap();
        Map<Integer, Integer> subMap = Maps.newHashMap();
        subMap.put(1000, 12);
        map.put(10086l, subMap);
        result.add(map);
        return ResponseEntity.ok(result);
    }
    @GetMapping("responseData2")
    public ResponseEntity<List<Map<Integer, Integer>>> getResult2(){
        List<Map<Integer, Integer>> result = Lists.newArrayList();
        Map<Integer, Integer> subMap = Maps.newHashMap();
        subMap.put(1000, 12);
        result.add(subMap);
        return ResponseEntity.ok(result);
    }

    @GetMapping("responseData3")
    public ResponseData<List<Map<Long, Map<Integer, Integer>>>> getResult3(){
        String url = "http://0.0.0.0:9000/network/responseData1";
        //String url = "http://0.0.0.0:8003/type/test";
        ResponseData<List<Map<Long, Map<Integer, Integer>>>> responseData = httpClientService.get(url, ResponseData.class);
        List<Map<Long, Map<Integer, Integer>>> data = JSONUtils.toJavaBean(JSONUtils.toJSONString(responseData.getData()), List.class, Map.class);
        JavaType javaType = JSONUtils.javaType(Map.class, Long.class, Map.class);
        JavaType javaType1 = JSONUtils.javaType(Map.class, Integer.class, Integer.class);
        List<Map<Long, Map<Integer, Integer>>> data1 = JSONUtils.toJavaBean(JSONUtils.toJSONString(responseData.getData()), javaType);
        List<Map<Long, Map<Integer, Integer>>> data2 = JSONUtils.toJavaBean(JSONUtils.toJSONString(responseData.getData()), new TypeReference<List<Map<Long, Map<Integer, Integer>>>>() {});
        return responseData;
    }
    @GetMapping("responseData4")
    public Map<String, List<Integer>> getResult4() throws Exception{
        Map<String, List<Integer>> map = Maps.newHashMap();
        List<Integer> list = Lists.newArrayList(12, 23);
        map.put("12", list);
        ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = JSONUtils.javaType(List.class, Integer.class);
        JavaType javaType1 = JSONUtils.javaType(HashMap.class, String.class, javaType.getRawClass());
        Map<String, List<Integer>> result = JSONUtils.toJavaBean(JSONUtils.toJSONString(map), javaType1);
        return result;
    }
    @GetMapping("getFundList")
    public ResponseData<List<GmFundPageInfoResData.Bk>> test6(){
        String url = "http://192.168.253.3:8108/api/fund/home/getFundCardList";
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("pageCode", "fundWealthIndex");
        ResponseData<List<GmFundPageInfoResData.Bk>> list = httpClientService.post(url, paramMap, null, new ParameterizedTypeReference<ResponseData<List<GmFundPageInfoResData.Bk>>>() {});
        System.out.println(JSONUtils.toJSONString(list));

        return list;
    }

}

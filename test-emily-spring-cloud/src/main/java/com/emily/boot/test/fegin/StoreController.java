package com.emily.boot.test.fegin;

import com.emily.infrastructure.common.base.BaseResponse;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @program: spring-parent
 * @description:
 * @create: 2021/03/24
 */
@RestController
public class StoreController {
    @Autowired
    private StoreClient storeClient;

    @GetMapping("stores")
    BaseResponse<Object> stores(){
        return storeClient.stores("10005002", "1234");
    }

    @PostMapping("insertStores")
    public Store insertStores(@Validated @RequestBody Store store){
        return storeClient.insertStores(store);
    }

    @GetMapping("getStores")
    public Store getStores(Store store){
        return storeClient.getStores(store);
    }

    @GetMapping("getStores1")
    @ResponseBody
    public Store getStoresTest(Store store){
        Map<String, Object> params = Maps.newHashMap();
        params.put("name", "adfs");
        params.put("weight", "12");
        return storeClient.getStoresTest1(params);
    }
}

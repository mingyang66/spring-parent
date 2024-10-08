package com.emily.cloud.test.fegin;

import com.emily.infrastructure.web.response.entity.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.Map;

/**
 * @author Emily
 * @program: spring-parent
 * fegin
 * @since 2021/03/24
 */
//@FeignClient(value = "stores", url = "http://172.30.67.122:9111")
@FeignClient(name = "consul-demo", contextId = "user", path = "user", primary = true)
public interface UserClient {

    @RequestMapping(method = RequestMethod.GET, value = "stores")
    List<Store> stores();

    @RequestMapping(method = RequestMethod.POST, value = "insertStores")
    BaseResponse<Store> insertStores(Store store);

    @RequestMapping(method = RequestMethod.GET, value = "getStores")
    BaseResponse<Store> getStores(@SpringQueryMap Store store);

    @RequestMapping(method = RequestMethod.GET, value = "getStores")
    Store getStoresTest1(@SpringQueryMap Map<String, Object> params);

    @RequestMapping(method = RequestMethod.POST, value = "stores/{storeId}", consumes = "application/json")
    Store update(@PathVariable("storeId") Long storeId, Store store);
}

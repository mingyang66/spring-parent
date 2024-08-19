package com.emily.cloud.test.fegin;

import com.emily.infrastructure.web.response.entity.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * @author Emily
 * @program: spring-parent
 * fegin
 * @since 2021/03/24
 */
@FeignClient(value = "stores", url = "http://10.10.181.116:8150/")
//@FeignClient(name = "consul-demo", contextId = "store", primary = true)
public interface StoreClient {

    @GetMapping(value = "/innerApi/accountNumber/getMainFundAccountBy/{accountCode}")
    BaseResponse<Object> stores(@PathVariable("accountCode") String accountCode, String password);

    @RequestMapping(method = RequestMethod.POST, value = "/insertStores")
    Store insertStores(Store store);

    @RequestMapping(method = RequestMethod.GET, value = "/getStores")
    Store getStores(@SpringQueryMap Store store);

    @RequestMapping(method = RequestMethod.GET, value = "/getStores")
    Store getStoresTest1(@SpringQueryMap Map<String, Object> params);

    @RequestMapping(method = RequestMethod.POST, value = "/stores/{storeId}", consumes = "application/json")
    Store update(@PathVariable("storeId") Long storeId, Store store);
}

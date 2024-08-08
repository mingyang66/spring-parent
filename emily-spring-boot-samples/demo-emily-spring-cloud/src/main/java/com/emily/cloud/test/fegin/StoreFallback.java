package com.emily.cloud.test.fegin;/*
package com.emily.boot.test.fegin;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

*/
/**
 * @program: spring-parent
 * 异常熔断
 * @since 2021/03/24
 *//*

//@Component
public class StoreFallback implements StoreClient {

    @Override
    public List<Store> stores() {
        throw new BusinessException(AppHttpStatus.EXCEPTION);
    }

    @Override
    public Store getStores(Store store) {
        throw new BusinessException(AppHttpStatus.EXCEPTION);
    }

    @Override
    public Page<Store> getStores(Pageable pageable) {
        throw new BusinessException(AppHttpStatus.EXCEPTION);
    }

    @Override
    public Store update(Long storeId, Store store) {
        throw new BusinessException(AppHttpStatus.EXCEPTION);
    }
}
*/

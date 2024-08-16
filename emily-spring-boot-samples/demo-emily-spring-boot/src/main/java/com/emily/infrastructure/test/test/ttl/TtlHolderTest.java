package com.emily.infrastructure.test.test.ttl;

import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.tracing.holder.ContextHolder;

/**
 * @author :  Emily
 * @since :  2023/8/11 10:09 AM
 */
public class TtlHolderTest {
    public static void main(String[] args) {
        ContextHolder holder = ContextHolder.newBuilder().build();
        System.out.println(JsonUtils.toJSONPrettyString(holder));
    }
}

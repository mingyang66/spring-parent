package com.emily.infrastructure.test.test.ttl;

import com.emily.infrastructure.core.context.holder.ContextHolder;
import com.emily.infrastructure.core.context.holder.ContextHolderBuilder;
import com.emily.infrastructure.json.JsonUtils;

/**
 * @author :  Emily
 * @since :  2023/8/11 10:09 AM
 */
public class TtlHolderTest {
    public static void main(String[] args) {
        ContextHolder holder = new ContextHolderBuilder().build();
        System.out.println(JsonUtils.toJSONPrettyString(holder));
    }
}

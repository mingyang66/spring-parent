package com.emily.infrastructure.test.test.ttl;

import com.emily.infrastructure.core.context.holder.ContextWrapper;
import com.emily.infrastructure.core.context.holder.LocalContextHolder;

/**
 * @author :  Emily
 * @since :  2023/8/10 2:54 PM
 */
public class TtlRunnableTest {

    public static void main(String[] args) {
        ContextWrapper.run(() -> System.out.println(Thread.currentThread().getName() + "-" + LocalContextHolder.current().getTraceId()));
    }
}

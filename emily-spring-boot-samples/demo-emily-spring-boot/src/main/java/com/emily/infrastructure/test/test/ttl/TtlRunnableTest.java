package com.emily.infrastructure.test.test.ttl;


import com.emily.infrastructure.tracing.holder.ContextWrapper;
import com.emily.infrastructure.tracing.holder.LocalContextHolder;

/**
 * @author :  Emily
 * @since :  2023/8/10 2:54 PM
 */
public class TtlRunnableTest {

    public static void main(String[] args) {
        ContextWrapper.run(null, () -> System.out.println(Thread.currentThread().getName() + "-" + LocalContextHolder.current().getTraceId()));
    }
}

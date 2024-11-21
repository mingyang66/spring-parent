package com.emily.infrastructure.sample.web.test.ttl;


import com.emily.infrastructure.tracing.holder.LocalContextHolder;

/**
 * @author : Emily
 * @since :  2023/9/14 11:15 AM
 */
public class TtlThreadTest {
    public static void main(String[] args) {
        LocalContextHolder.current().getTraceId();
        //LocalContextHolder.unbind(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                LocalContextHolder.current().getTraceId();
            }
        }).start();
    }
}

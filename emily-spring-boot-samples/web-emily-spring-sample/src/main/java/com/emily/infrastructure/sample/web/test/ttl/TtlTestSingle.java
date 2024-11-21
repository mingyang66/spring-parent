package com.emily.infrastructure.sample.web.test.ttl;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * @author :  Emily
 * @since :  2023/8/9 10:25 AM
 */
public class TtlTestSingle {
    public static void main(String[] args) {
        TransmittableThreadLocal<String> holder = new TransmittableThreadLocal<>();
        holder.set("测试");
    }
}

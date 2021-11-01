package com.emily.infrastructure.rpc.client.loadbalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: spring-parent
 * @description: 轮询机制获取服务器IP地址
 * @author: Emily
 * @create: 2021/11/01
 */
public class RoundLoadBalance extends AbstractLoadBalance {
    /**
     * 原子类型请求数量统计
     */
    private AtomicInteger position = new AtomicInteger(0);

    @Override
    protected String doSelect(List<String> serviceAddress) {
        if (position.get() == 0) {
            return serviceAddress.get(position.getAndIncrement());
        }
        //数据增加到最大Integer.MAX_VALUE后绝对值开始减小
        int pos = Math.abs(position.getAndIncrement());
        return serviceAddress.get(pos % serviceAddress.size());
    }

}

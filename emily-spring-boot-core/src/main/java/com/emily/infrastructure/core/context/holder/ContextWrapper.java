package com.emily.infrastructure.core.context.holder;

import com.alibaba.ttl.TtlRunnable;
import com.emily.infrastructure.core.helper.ThreadPoolHelper;

/**
 * 新增对非servlet上下文请求入口
 *
 * @author :  Emily
 * @since :  2023/8/7 4:59 PM
 */
public class ContextWrapper {
    /**
     * 1.对线程前初始化上下文
     * 2.将线程通过TTL修饰后加入线程池执行；
     * 3.最后移除第一步初始化的上下文
     *
     * @param runnable 线程
     */
    public static void run(Runnable runnable) {
        try {
            //初始化上下文
            LocalContextHolder.current();
            //执行具体代码
            ThreadPoolHelper.defaultThreadPoolTaskExecutor().execute(TtlRunnable.get(runnable));
        } finally {
            //移除上下文值设置
            LocalContextHolder.unbind(true);
        }
    }
}

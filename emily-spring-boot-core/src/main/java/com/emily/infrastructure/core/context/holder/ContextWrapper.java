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
     * 通过Ttl修饰运行指定的线程
     * 使用案例如下：
     * <pre>{@code
     *     @Scheduled(fixedRate = 5000)
     *     public void doSchedule() {
     *         ContextWrapper.run(() -> {
     *             System.out.println("start--------上下文-1-" + LocalContextHolder.current().getTraceId());
     *             mysqlMapper.getMysql("田晓霞", "123456");
     *             System.out.println("--------上下文-2-" + LocalContextHolder.current().getTraceId());
     *             mysqlMapper.getMysql("孙少平", "123457");
     *             System.out.println("end--------上下文-3-" + LocalContextHolder.current().getTraceId());
     *         });
     *     }
     * }</pre>
     *
     * @param runnable 线程
     */
    public static void run(Runnable runnable) {
        run(runnable, true);
    }

    /**
     * 1.对线程前初始化上下文，并标记是否是逻辑上的servlet上下文
     * 2.将线程通过TTL修饰后加入线程池执行；
     * 3.最后移除第一步初始化的上下文
     *
     * @param runnable 线程
     * @param servlet  是否是servlet上下文
     */
    public static void run(Runnable runnable, boolean servlet) {
        try {
            //初始化上下文
            LocalContextHolder.current().setServlet(servlet);
            //执行具体代码
            ThreadPoolHelper.defaultThreadPoolTaskExecutor().execute(TtlRunnable.get(runnable));
        } finally {
            //移除上下文值设置
            LocalContextHolder.unbind(true);
        }
    }
}

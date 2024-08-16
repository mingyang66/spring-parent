package com.emily.infrastructure.tracing.holder;

/**
 * 对上下文数据进行捕获、备份、恢复操作
 *
 * @author :  Emily
 * @since :  2023/8/19 1:36 PM
 */
public class ContextTransmitter {
    /**
     * 获取原始数据备份，设置当前数据
     *
     * @param servletStage 当前阶段标识
     * @return 备份阶段标识
     */
    public static ServletStage replay(ServletStage servletStage) {
        // 获取原始阶段标识
        ServletStage backup = LocalContextHolder.current().getServletStage();
        // 设置当前阶段标识
        LocalContextHolder.current().setServletStage(servletStage);
        return backup;
    }

    /**
     * 恢复阶段标识
     *
     * @param servletStage 阶段标识
     */
    public static void restore(ServletStage servletStage) {
        LocalContextHolder.current().setServletStage(servletStage);
    }
}

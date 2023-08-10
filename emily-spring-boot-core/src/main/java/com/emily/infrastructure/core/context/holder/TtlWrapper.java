package com.emily.infrastructure.core.context.holder;

/**
 * @author :  Emily
 * @since :  2023/8/7 4:59 PM
 */
public class TtlWrapper implements Runnable {
    private Runnable runnable;

    public TtlWrapper(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        try {
            //初始化上下文
            LocalContextHolder.current();
            //执行具体代码
            runnable.run();
        } finally {
            //移除上下文值设置
            LocalContextHolder.unbind(true);
        }
    }

    public static TtlWrapper get(Runnable runnable) {
        if (runnable == null) {
            return null;
        }
        if (runnable instanceof TtlWrapper) {
            return (TtlWrapper) runnable;
        } else {
            return new TtlWrapper(runnable);
        }
    }

    public static void run(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (runnable instanceof TtlWrapper) {
            runnable.run();
        } else {
            new TtlWrapper(runnable).run();
        }
    }
}

/*
package com.emily.infrastructure.rabbitmq.listener;

import com.rabbitmq.client.impl.recovery.RetryContext;
import org.springframework.core.retry.RetryListener;

*/
/**
 * 生产端发布重试监听器
 *
 * @author :  Emily
 * @param context   the current {@link RetryContext}.
 * @param callback  the current {@link RetryCallback}.
 * @param throwable the last exception that was thrown by the callback.
 * @param <T>
 * @param <E>
 * <p>
 * 在重试发生错误时执行的逻辑
 * @param context   the current {@link RetryContext}.
 * @param callback  the current {@link RetryCallback}.
 * @param throwable the last exception that was thrown by the callback.
 * @param <T>
 * @param <E>
 * <p>
 * 在每次重试之前执行的逻辑
 * @param context  the current {@link RetryContext}.
 * @param callback the current {@link RetryCallback}.
 * @param <T>
 * @param <E>
 * @return 返回true表示继续执行重试操作，返回false表示终止重试
 * @since :  2023/9/20 21:10 PM
 * <p>
 * 在重试操作完成后执行的逻辑
 *//*

public class PublisherRetryListener implements RetryListener {
    */
/**
 * 在重试操作完成后执行的逻辑
 *
 * @param context   the current {@link RetryContext}.
 * @param callback  the current {@link RetryCallback}.
 * @param throwable the last exception that was thrown by the callback.
 * @param <T>
 * @param <E>
 *//*

    @Override
    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        if (context.getRetryCount() > 0) {
            System.out.println("重试成功");
            String[] attributes = context.attributeNames();
            for (int i = 0; i < attributes.length; i++) {
                Object value = context.getAttribute(attributes[i]);
                System.out.println(attributes[i] + "--" + value);
            }
        }
    }

    */
/**
 * 在重试发生错误时执行的逻辑
 *
 * @param context   the current {@link RetryContext}.
 * @param callback  the current {@link RetryCallback}.
 * @param throwable the last exception that was thrown by the callback.
 * @param <T>
 * @param <E>
 *//*

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        System.out.println("重试失败");
    }

    */
/**
 * 在每次重试之前执行的逻辑
 *
 * @param context  the current {@link RetryContext}.
 * @param callback the current {@link RetryCallback}.
 * @param <T>
 * @param <E>
 * @return 返回true表示继续执行重试操作，返回false表示终止重试
 *//*

    @Override
    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        System.out.println("重试开始");
        return true;
    }
}
*/

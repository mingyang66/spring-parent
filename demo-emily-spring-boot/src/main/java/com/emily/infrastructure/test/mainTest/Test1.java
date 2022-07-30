package com.emily.infrastructure.test.mainTest;

import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.core.helper.RequestHelper;
import com.emily.infrastructure.test.po.Job;
import com.emily.infrastructure.test.po.User;

import java.util.Map;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/05/17
 */
public class Test1 {
    public static final ThreadLocal<String> threadLocal = new InheritableThreadLocal<>();

    public static void main(String[] args) throws IllegalAccessException, NoSuchFieldException, InterruptedException {
/*        threadLocal.set("主线程1。。。");
        System.out.println("线程1：" + threadLocal.get());
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("子线程1：" + Thread.currentThread().getName() + ":" + threadLocal.get());
            }
        }).start();

        Thread.sleep(1000);
        threadLocal.set("主线程2。。。");
        System.out.println("线程2：" + threadLocal.get());
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("子线程2：" + Thread.currentThread().getName() + ":" + threadLocal.get());
            }
        }).start();
        //删除本地内存中的变量
        threadLocal.remove();*/
        User user = new User();
        user.setUsername("孙少平");
        user.setPassword("123456");
        Job job = new Job();
        job.setA("孙玉厚");
        job.setJobDesc("孙少安开了砖窑厂，做了窑主");
        job.setJobNumber(20L);
        job.setId(1234L);
        user.setJob(job);

        Map<String, Object> paramMap = RequestHelper.getObjectMap(user, "username");
        System.out.println(JSONUtils.toJSONPrettyString(paramMap));
    }

}

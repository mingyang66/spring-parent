package com.emily.boot.test.api.pool2;

import com.emily.boot.test.api.po.User;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @program: spring-parent
 * @description:
 * @author:
 * @create: 2021/03/19
 */
public class PoolMain {
    public static void main(String[] args) throws Exception {
        ObjectFactory orderFactory = new ObjectFactory();
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(5000);
        //设置获取连接超时时间
        config.setMaxWaitMillis(1000);
        GenericObjectPool<User> connectionPool = new GenericObjectPool<User>(orderFactory, config);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            User o = connectionPool.borrowObject();
            //User o = new User();
            //System.out.println("brrow a connection: " + o +" active connection:"+connectionPool.getNumActive());
            connectionPool.returnObject(o);
        }
        System.out.println(System.currentTimeMillis() - start);
    }
}

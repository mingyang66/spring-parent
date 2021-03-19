package com.emily.boot.test.api.pool2;

import com.emily.boot.test.api.po.User;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @program: spring-parent
 * @description:
 * @author: 姚明洋
 * @create: 2021/03/19
 */
public class ObjectFactory extends BasePooledObjectFactory<User> {
    @Override
    public User create() throws Exception {
        return new User();
    }

    @Override
    public PooledObject<User> wrap(User obj) {
        return new DefaultPooledObject<>(obj);
    }
}

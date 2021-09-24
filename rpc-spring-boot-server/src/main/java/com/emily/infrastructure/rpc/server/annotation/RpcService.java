package com.emily.infrastructure.rpc.server.annotation;

import java.lang.annotation.*;

/**
* @Description: 标注为Rpc服务的注解标识
* @Author: Emily
* @create: 2021/9/18
*/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RpcService {
}

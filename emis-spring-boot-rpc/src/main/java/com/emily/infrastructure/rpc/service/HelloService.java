package com.emily.infrastructure.rpc.service;

import com.emily.infrastructure.rpc.entity.Result;

public interface HelloService {
    Result hello(String s);
    String str();
}

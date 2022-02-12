package com.emily.infrastructure.rpc.server.example;

import com.emily.infrastructure.rpc.core.example.Student;

public interface HelloService {
    Result hello(String s);

    String str();

    int get(int x, long y, String s);

    double get(int x, Long y);

    String getStudent(Student student);
}

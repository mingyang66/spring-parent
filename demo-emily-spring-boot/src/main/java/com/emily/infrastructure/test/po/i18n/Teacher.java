package com.emily.infrastructure.test.po.i18n;

import com.emily.infrastructure.common.i18n.ApiI18n;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * @Description :  老师
 * @Author :  Emily
 * @CreateDate :  Created in 2023/4/17 3:46 PM
 */
@ApiI18n
public class Teacher {
    public String name;
    public List<Student> studentList = Lists.newArrayList();
    public Map<String, Student> studentMap = Maps.newHashMap();
    public Student[] students = new Student[2];
}

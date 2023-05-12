package com.emily.infrastructure.test.po.i18n;

import com.emily.infrastructure.language.convert.ApiI18n;
import com.emily.infrastructure.language.convert.ApiI18nProperty;
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
    @ApiI18nProperty
    public String name;
    public List<Student> studentList = Lists.newArrayList();
    public Map<String, Student> studentMap = Maps.newHashMap();
    public Student[] students = new Student[2];
}

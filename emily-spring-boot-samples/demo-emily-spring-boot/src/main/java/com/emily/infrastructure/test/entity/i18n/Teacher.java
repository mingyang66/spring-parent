package com.emily.infrastructure.test.entity.i18n;

import com.emily.infrastructure.language.annotation.I18nModel;
import com.emily.infrastructure.language.annotation.I18nProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 * 老师
 *
 * @author Emily
 * @since Created in 2023/4/17 3:46 PM
 */
@I18nModel
public class Teacher {
    @I18nProperty
    public String name;
    public List<Student> studentList = Lists.newArrayList();
    public Map<String, Student> studentMap = Maps.newHashMap();
    public Student[] students = new Student[2];
}

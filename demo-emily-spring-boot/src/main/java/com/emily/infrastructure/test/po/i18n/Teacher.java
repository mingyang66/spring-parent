package com.emily.infrastructure.test.po.i18n;

import com.emily.infrastructure.language.convert.JsonI18n;
import com.emily.infrastructure.language.convert.JsonI18nField;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

/**
 *  老师
 * @author  Emily
 * @since  Created in 2023/4/17 3:46 PM
 */
@JsonI18n
public class Teacher {
    @JsonI18nField
    public String name;
    public List<Student> studentList = Lists.newArrayList();
    public Map<String, Student> studentMap = Maps.newHashMap();
    public Student[] students = new Student[2];
}

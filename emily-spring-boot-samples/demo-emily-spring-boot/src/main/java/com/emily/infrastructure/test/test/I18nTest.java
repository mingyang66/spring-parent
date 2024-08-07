package com.emily.infrastructure.test.test;

import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.language.convert.I18nConvertHelper;
import com.emily.infrastructure.language.convert.LanguageMap;
import com.emily.infrastructure.language.convert.LanguageType;
import com.emily.infrastructure.test.entity.i18n.Student;
import com.emily.infrastructure.test.entity.i18n.Teacher;

/**
 * 多语言测试
 *
 * @author Emily
 * @since Created in 2023/4/17 3:36 PM
 */
public class I18nTest {
    public static void main(String[] args) throws IllegalAccessException {
        LanguageMap.bindEn("田晓霞", "tianxiaoxia");
        LanguageMap.bindEn("李老师", "li teacher");
        LanguageMap.bindEn("孙少平", "sun shao ping");
        LanguageMap.bindEn("孙少安", "sun shao an");
        Student student = new Student();
        student.setAge(18);
        student.setName("田晓霞");

        // System.out.println(JSONUtils.toJSONString(I18nUtils.acquire(student, LanguageType.EN)));
        Teacher teacher = new Teacher();
        teacher.name = "李老师";
        teacher.studentList.add(student);
        student.setName("孙少平");
        teacher.studentMap.put("testMap1", student);
        teacher.students[0] = student;
        student.setName("孙少安");
        teacher.studentMap.put("testMap2", student);
        teacher.students[1] = student;
        System.out.println(JsonUtils.toJSONString(teacher));
        System.out.println(JsonUtils.toJSONString(I18nConvertHelper.acquire(teacher, LanguageType.EN_US)));
        System.out.println(JsonUtils.toJSONString(teacher));
    }
}

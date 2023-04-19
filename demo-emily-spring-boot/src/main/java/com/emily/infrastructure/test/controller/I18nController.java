package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.common.i18n.LanguageMap;
import com.emily.infrastructure.test.po.i18n.Student;
import com.emily.infrastructure.test.po.i18n.Teacher;
import com.google.common.collect.Maps;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description :  多语言
 * @Author :  Emily
 * @CreateDate :  Created in 2023/4/18 9:58 AM
 */
@RestController
@RequestMapping("api/i18n")
public class I18nController {
    static {
        LanguageMap.bindEn("田晓霞","tianxiaoxia");
        LanguageMap.bindEn("李老师", "li teacher");
        LanguageMap.bindEn("孙少平", "sun shao ping");
        LanguageMap.bindEn("孙少安", "sun shao an");
    }
    @GetMapping("test1")
    public Student student() {
        Student student = new Student();
        student.setName("田晓霞");
        student.setAge(18);
        return student;
    }

    @GetMapping("test2")
    public Teacher teacher() {
        Student student = new Student();
        student.setName("孙少平");
        student.setAge(20);
        Teacher teacher = new Teacher();
        teacher.name = "李老师";
        teacher.studentList.add(student);

        teacher.studentMap.put("s", student);
        teacher.students[0] = student;
        return teacher;
    }

    @GetMapping("test3")
    public List<Student> student1() {
        Student student = new Student();
        student.setName("田晓霞");
        student.setAge(18);
        return Arrays.asList(student);
    }

    @GetMapping("test4")
    public Map<String, List<Teacher>> teacher4() {
        Student student = new Student();
        student.setName("孙少平");
        student.setAge(20);
        Teacher teacher = new Teacher();
        teacher.name = "李老师";
        teacher.studentList.add(student);

        teacher.studentMap.put("s", student);
        teacher.students[0] = student;

        Map<String, List<Teacher>> dataMap = Maps.newHashMap();
        dataMap.put("test", Arrays.asList(teacher));
        return dataMap;
    }
}

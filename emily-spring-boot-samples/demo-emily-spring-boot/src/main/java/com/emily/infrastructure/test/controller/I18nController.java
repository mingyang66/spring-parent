package com.emily.infrastructure.test.controller;

import com.emily.infrastructure.language.convert.LanguageMap;
import com.emily.infrastructure.test.entity.i18n.Student;
import com.emily.infrastructure.test.entity.i18n.Teacher;
import com.google.common.collect.Maps;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 多语言
 *
 * @author Emily
 * @since Created in 2023/4/18 9:58 AM
 */
@RestController
@RequestMapping("api/i18n")
public class I18nController {
    static {
        LanguageMap.bindEn("田晓霞", "tianxiaoxia");
        LanguageMap.bindEn("李老师", "li teacher");
        LanguageMap.bindEn("孙少平", "sun shao ping");
        LanguageMap.bindEn("孙少安", "sun shao an");
        LanguageMap.bindEn("红薯", "sweet potato");
        LanguageMap.bindEn("看书", "book");
        LanguageMap.bindEn("电影", "movie");
        LanguageMap.bindEn("刷抖音", "Tiktok");
    }

    @GetMapping("test1")
    public Student student() {
        Student student = new Student();
        student.setName("田晓霞");
        student.setAge(18);
        student.setFood("红薯");
        student.setLike(Arrays.asList("看书", "电影", "刷抖音"));
        student.getData().put("s", "看书");
        student.getData().put("t", "红薯");
        return student;
    }

    @GetMapping("test2")
    public Teacher teacher() {
        Student student = new Student();
        student.setFood("红薯");
        student.setLike(Arrays.asList("看书", "电影", "刷抖音"));
        student.getData().put("s", "看书");
        student.getData().put("t", "红薯");
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

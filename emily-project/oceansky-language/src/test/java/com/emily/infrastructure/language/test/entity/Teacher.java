package com.emily.infrastructure.language.test.entity;

import com.emily.infrastructure.language.annotation.I18nMapProperty;
import com.emily.infrastructure.language.annotation.I18nModel;
import com.emily.infrastructure.language.annotation.I18nProperty;

import java.util.List;
import java.util.Map;

/**
 * @author :  Emily
 * @since :  2024/12/23 下午10:37
 */
@I18nModel
public class Teacher {
    @I18nProperty
    private String name;
    /**
     * 无效
     */
    @I18nProperty
    private Integer age;
    @I18nProperty
    private List<String> stringList;
    @I18nProperty
    private String[] titles;
    @I18nProperty
    private Map<String, String> stringMap;
    @I18nProperty
    @I18nMapProperty(value = {"test1"})
    private Map<String, Object> mapObj;
    private String email;
    private List<Student> students;
    private Map<String, User> users;
    private Student student;
    private Course[] courses;
    private FlexibleField flexibleField;

    public FlexibleField getFlexibleField() {
        return flexibleField;
    }

    public void setFlexibleField(FlexibleField flexibleField) {
        this.flexibleField = flexibleField;
    }

    public Map<String, Object> getMapObj() {
        return mapObj;
    }

    public void setMapObj(Map<String, Object> mapObj) {
        this.mapObj = mapObj;
    }

    public Map<String, String> getStringMap() {
        return stringMap;
    }

    public void setStringMap(Map<String, String> stringMap) {
        this.stringMap = stringMap;
    }

    public List<String> getStringList() {
        return stringList;
    }

    public void setStringList(List<String> stringList) {
        this.stringList = stringList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public void setUsers(Map<String, User> users) {
        this.users = users;
    }

    public String[] getTitles() {
        return titles;
    }

    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course[] getCourses() {
        return courses;
    }

    public void setCourses(Course[] courses) {
        this.courses = courses;
    }
}

package com.emily.infrastructure.language.test;

import com.emily.infrastructure.language.convert.I18nCache;
import com.emily.infrastructure.language.convert.I18nUtils;
import com.emily.infrastructure.language.convert.LanguageType;
import com.emily.infrastructure.language.test.entity.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author :  Emily
 * @since :  2024/12/23 下午10:35
 */
public class I18nUtilsTest {
    @BeforeAll
    public static void setUp() {
        I18nCache.bindEn("田晓霞", "xiaoxia tian");
        I18nCache.bindEn("孙少安", "shaoan sun");
        I18nCache.bindEn("孙少平", "shaoping sun");
        I18nCache.bindEn("田润叶", "runye tian");
        I18nCache.bindEn("郝红梅", "hongmei hao");
    }

    @Test
    public void testI18nProperty() throws IllegalAccessException {
        Teacher teacher = new Teacher();
        teacher.setName("田晓霞");
        teacher.setAge(18);
        teacher.setStringList(List.of("孙少安", "孙少平", "田润叶", "郝红梅"));
        teacher.setTitles(new String[]{"孙少平"});
        //teacher.setStringMap(Map.of("name","田润叶"));
        teacher.setStringMap(new HashMap<>(Map.ofEntries(Map.entry("name", "田润叶"))));
        Teacher teacher1 = I18nUtils.translate(teacher, LanguageType.EN_US);
        Assertions.assertEquals(teacher1.getName(), "xiaoxia tian");
        Assertions.assertEquals(teacher1.getStringList().get(0), "shaoan sun");
        Assertions.assertEquals(teacher1.getTitles()[0], "shaoping sun");
        Assertions.assertEquals(teacher1.getStringMap().get("name"), "runye tian");
    }

    @Test
    public void testI18nPropertyNestArray() throws IllegalAccessException {
        Teacher teacher = new Teacher();
        Course course = new Course();
        course.setCode("123456789");
        course.setName("郝红梅");
        teacher.setCourses(new Course[]{course});
        Teacher teacher1 = I18nUtils.translate(teacher, LanguageType.EN_US);
        Assertions.assertEquals(teacher1.getCourses()[0].getName(), "hongmei hao");
    }

    @Test
    public void testI18nPropertyNestList() throws IllegalAccessException {
        Teacher teacher = new Teacher();
        Student student = new Student();
        student.setName("郝红梅");
        teacher.setStudents(List.of(student));
        Teacher teacher1 = I18nUtils.translate(teacher, LanguageType.EN_US);
        Assertions.assertEquals(teacher1.getStudents().get(0).getName(), "hongmei hao");
    }

    @Test
    public void testI18nPropertyNestMap() throws IllegalAccessException {
        Teacher teacher = new Teacher();
        User user = new User();
        user.setUsername("郝红梅");
        teacher.setUsers(new HashMap<>(Map.ofEntries(Map.entry("test", user))));
        Teacher teacher1 = I18nUtils.translate(teacher, LanguageType.EN_US);
        Assertions.assertEquals(teacher1.getUsers().get("test").getUsername(), "hongmei hao");
    }

    @Test
    public void testI18nMapProperty() throws IllegalAccessException {
        Teacher teacher = new Teacher();
        Map<String, Object> data = new HashMap<>();
        data.put("test1", "田润叶");
        data.put("test2", "田晓霞");
        data.put("test3", 12);
        teacher.setMapObj(data);
        Teacher teacher1 = I18nUtils.translate(teacher, LanguageType.EN_US);
        Assertions.assertEquals(teacher1.getMapObj().get("test1"), "runye tian");
        Assertions.assertEquals(teacher1.getMapObj().get("test2"), "田晓霞");
    }

    @Test
    public void testI18nFlexibleProperty() throws IllegalAccessException {
        Teacher teacher = new Teacher();
        FlexibleField flexibleField = new FlexibleField();
        flexibleField.setKey7("address");
        flexibleField.setValue7("田晓霞");
        flexibleField.setKey0("username");
        flexibleField.setKey1("username");
        flexibleField.setValue1("田晓霞");
        flexibleField.setKey2("email");
        flexibleField.setValue2("田晓霞");
        flexibleField.setKey3("email");
       // flexibleField.setValue3("田晓霞");
        flexibleField.setKey4("email");
        flexibleField.setValue4("田晓霞");
        flexibleField.setKey5("email");
        flexibleField.setKey6("email");
        flexibleField.setValue6("田晓霞");
        teacher.setFlexibleField(flexibleField);
        Teacher teacher1 = I18nUtils.translate(teacher, LanguageType.EN_US);
        Assertions.assertEquals(teacher1.getFlexibleField().getValue7(), "田晓霞");
        Assertions.assertEquals(teacher1.getFlexibleField().getValue1(), "xiaoxia tian");
        Assertions.assertEquals(teacher1.getFlexibleField().getValue2(), "田晓霞");
        Assertions.assertNull(teacher1.getFlexibleField().getValue3());
        Assertions.assertEquals(teacher1.getFlexibleField().getValue4(), "田晓霞");
        Assertions.assertEquals(teacher1.getFlexibleField().getValue6(), "xiaoxia tian");

        FlexibleField flexibleField1 = new FlexibleField();
        flexibleField1.setKey1("username1");
        flexibleField1.setValue1("田晓霞");
        flexibleField1.setKey3("username1");
        flexibleField1.setValue3("田晓霞");
        teacher.setFlexibleField(flexibleField1);
        Teacher teacher2 = I18nUtils.translate(teacher, LanguageType.EN_US);
        Assertions.assertEquals(teacher2.getFlexibleField().getValue1(), "田晓霞");
    }

}

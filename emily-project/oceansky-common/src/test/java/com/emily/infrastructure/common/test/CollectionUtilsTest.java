package com.emily.infrastructure.common.test;

import com.emily.infrastructure.common.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

/**
 * 结合单元测试类
 *
 * @author Emily
 * @since Created in 2023/6/3 9:35 AM
 */
public class CollectionUtilsTest {
    @Test
    public void collection() {
        List list = null;
        Assertions.assertTrue(CollectionUtils.isEmpty(list));
        Assertions.assertTrue(CollectionUtils.isEmpty(Arrays.asList()));
        Assertions.assertTrue(CollectionUtils.isEmpty(new HashSet<>()));
        Assertions.assertTrue(CollectionUtils.isEmpty(new ArrayList<>()));

        Assertions.assertTrue(CollectionUtils.isNotEmpty(Arrays.asList("12")));
        Assertions.assertFalse(CollectionUtils.isNotEmpty(Arrays.asList()));
    }

    @Test
    public void map() {
        Map map = null;
        Assertions.assertTrue(CollectionUtils.isEmpty(map));
        Assertions.assertFalse(CollectionUtils.isNotEmpty(new HashMap<>()));

        List<String> list = CollectionUtils.newArrayList();
        list.add("a");
        Assertions.assertNotNull(list);
        Assertions.assertNotNull(CollectionUtils.newArrayList(list));
    }

    @Test
    public void contains() {
        List<String> list = CollectionUtils.newArrayList("1", "2");
        Assertions.assertEquals(CollectionUtils.contains(list, "1"), true);
        Assertions.assertEquals(CollectionUtils.contains(list, "0"), false);
    }
}

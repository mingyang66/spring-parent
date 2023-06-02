package com.emily.infrastructure.collection.test;

import com.emily.infrastructure.collection.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * @Description :  结合单元测试类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/6/3 9:35 AM
 */
public class CollectionUtilsTest {
    @Test
    public void collection() {
        List list = null;
        Assert.assertTrue(CollectionUtils.isEmpty(list));
        Assert.assertTrue(CollectionUtils.isEmpty(Arrays.asList()));
        Assert.assertTrue(CollectionUtils.isEmpty(new HashSet<>()));
        Assert.assertTrue(CollectionUtils.isEmpty(new ArrayList<>()));

        Assert.assertTrue(CollectionUtils.isNotEmpty(Arrays.asList("12")));
        Assert.assertFalse(CollectionUtils.isNotEmpty(Arrays.asList()));
    }
    @Test
    public void map(){
        Map map = null;
        Assert.assertTrue(CollectionUtils.isEmpty(map));
        Assert.assertFalse(CollectionUtils.isNotEmpty(new HashMap<>()));

        List<String> list = CollectionUtils.newArrayList();
        list.add("a");
        Assert.assertNotNull(list);
        Assert.assertNotNull(CollectionUtils.newArrayList(list));
    }
    @Test
    public void contains(){
        List<String> list = CollectionUtils.newArrayList("1","2");
        Assert.assertEquals(CollectionUtils.contains(list,"1"), true);
        Assert.assertEquals(CollectionUtils.contains(list,"0"), false);
    }
}

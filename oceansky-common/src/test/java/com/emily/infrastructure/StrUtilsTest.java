package com.emily.infrastructure;

import com.emily.infrastructure.str.StrUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Description :
 * @Author :  Emily
 * @CreateDate :  Created in 2023/6/4 2:14 PM
 */
public class StrUtilsTest {
    @Test
    public void toUpperFirstCase() {
        Assert.assertEquals(StrUtils.toUpperFirstCase(null), null);
        Assert.assertEquals(StrUtils.toUpperFirstCase(""), "");
        Assert.assertEquals(StrUtils.toUpperFirstCase(" "), " ");
        Assert.assertEquals(StrUtils.toUpperFirstCase(" a".trim()), "A");
        Assert.assertEquals(StrUtils.toUpperFirstCase("a"), "A");
        Assert.assertEquals(StrUtils.toUpperFirstCase("abc"), "Abc");
    }

    @Test
    public void toLowerFirstCase() {
        Assert.assertEquals(StrUtils.toLowerFirstCase(null), null);
        Assert.assertEquals(StrUtils.toLowerFirstCase(""), "");
        Assert.assertEquals(StrUtils.toLowerFirstCase(" "), " ");
        Assert.assertEquals(StrUtils.toLowerFirstCase(" A"), " A");
        Assert.assertEquals(StrUtils.toLowerFirstCase("A"), "a");
        Assert.assertEquals(StrUtils.toLowerFirstCase("Abc"), "abc");
    }
}

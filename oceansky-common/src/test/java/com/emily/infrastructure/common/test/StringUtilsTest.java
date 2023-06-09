package com.emily.infrastructure.common.test;

import com.emily.infrastructure.common.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Description :
 * @Author :  Emily
 * @CreateDate :  Created in 2023/6/4 2:14 PM
 */
public class StringUtilsTest {
    @Test
    public void toUpperFirstCase() {
        Assert.assertEquals(StringUtils.toUpperFirstCase(null), null);
        Assert.assertEquals(StringUtils.toUpperFirstCase(""), "");
        Assert.assertEquals(StringUtils.toUpperFirstCase(" "), " ");
        Assert.assertEquals(StringUtils.toUpperFirstCase(" a".trim()), "A");
        Assert.assertEquals(StringUtils.toUpperFirstCase("a"), "A");
        Assert.assertEquals(StringUtils.toUpperFirstCase("abc"), "Abc");
    }

    @Test
    public void toLowerFirstCase() {
        Assert.assertEquals(StringUtils.toLowerFirstCase(null), null);
        Assert.assertEquals(StringUtils.toLowerFirstCase(""), "");
        Assert.assertEquals(StringUtils.toLowerFirstCase(" "), " ");
        Assert.assertEquals(StringUtils.toLowerFirstCase(" A"), " A");
        Assert.assertEquals(StringUtils.toLowerFirstCase("A"), "a");
        Assert.assertEquals(StringUtils.toLowerFirstCase("Abc"), "abc");
    }

    @Test
    public void leftPad() {
        Assert.assertEquals(StringUtils.leftPad(null, 5, 'a'), null);
        Assert.assertEquals(StringUtils.leftPad("", 5, '0'), "00000");
        Assert.assertEquals(StringUtils.leftPad("aaaaa", 5, '0'), "aaaaa");
        Assert.assertEquals(StringUtils.leftPad("2", 5, '0'), "00002");

        Assert.assertEquals(StringUtils.leftPad(null, 5, "ab"), null);
        Assert.assertEquals(StringUtils.leftPad(null, 5, null), null);
        Assert.assertEquals(StringUtils.leftPad(null, 5, ""), null);
        Assert.assertEquals(StringUtils.leftPad("", 5, ""), "     ");
        Assert.assertEquals(StringUtils.leftPad("1", 5, ""), "    1");
        Assert.assertEquals(StringUtils.leftPad("1", 5, " "), "    1");
        Assert.assertEquals(StringUtils.leftPad("1", 5, "0"), "00001");
        Assert.assertEquals(StringUtils.leftPad("1", 5, "0A"), "0A0A1");
        Assert.assertEquals(StringUtils.leftPad("1", 5, "0AB"), "0AB01");
        Assert.assertEquals(StringUtils.leftPad("11", 8, "0AB"), "0AB0AB11");
        Assert.assertEquals(StringUtils.leftPad("11", 7, "0AB"), "0AB0A11");
    }

    @Test
    public void rightPad() {
        Assert.assertEquals(StringUtils.rightPad(null, 5, 'a'), null);
        Assert.assertEquals(StringUtils.rightPad("", 5, '0'), "00000");
        Assert.assertEquals(StringUtils.rightPad("aaaaa", 5, '0'), "aaaaa");
        Assert.assertEquals(StringUtils.rightPad("2", 5, '0'), "20000");

        Assert.assertEquals(StringUtils.rightPad(null, 5, "ab"), null);
        Assert.assertEquals(StringUtils.rightPad(null, 5, null), null);
        Assert.assertEquals(StringUtils.rightPad(null, 5, ""), null);
        Assert.assertEquals(StringUtils.rightPad("", 5, ""), "     ");
        Assert.assertEquals(StringUtils.rightPad("1", 5, ""), "1    ");
        Assert.assertEquals(StringUtils.rightPad("1", 5, " "), "1    ");
        Assert.assertEquals(StringUtils.rightPad("1", 5, "0"), "10000");
        Assert.assertEquals(StringUtils.rightPad("1", 5, "0A"), "10A0A");
        Assert.assertEquals(StringUtils.rightPad("1", 5, "0AB"), "10AB0");
        Assert.assertEquals(StringUtils.rightPad("11", 8, "0AB"), "110AB0AB");
        Assert.assertEquals(StringUtils.rightPad("11", 7, "0AB"), "110AB0A");
    }

    @Test
    public void isEmpty() {
        Assert.assertTrue(StringUtils.isEmpty(null));
        Assert.assertTrue(StringUtils.isEmpty(""));
        Assert.assertFalse(StringUtils.isEmpty("a"));
        Assert.assertFalse(StringUtils.isEmpty(" a"));
        Assert.assertFalse(StringUtils.isEmpty(" a "));


        Assert.assertFalse(StringUtils.isNotEmpty(null));
        Assert.assertFalse(StringUtils.isNotEmpty(""));
        Assert.assertTrue(StringUtils.isNotEmpty("a"));
        Assert.assertTrue(StringUtils.isNotEmpty(" a"));
        Assert.assertTrue(StringUtils.isNotEmpty(" a "));
    }

    @Test
    public void length() {
        Assert.assertEquals(StringUtils.length(null), 0);
        Assert.assertEquals(StringUtils.length(""), 0);
        Assert.assertEquals(StringUtils.length("12"), 2);
    }

    @Test
    public void isBlank() {
        Assert.assertEquals(StringUtils.isBlank(null), true);
        Assert.assertEquals(StringUtils.isBlank(""), true);
        Assert.assertEquals(StringUtils.isBlank(" "), true);
        Assert.assertEquals(StringUtils.isBlank(" a"), false);
        Assert.assertEquals(StringUtils.isBlank(" a "), false);

        Assert.assertEquals(StringUtils.isNotBlank(null), false);
        Assert.assertEquals(StringUtils.isNotBlank(""), false);
        Assert.assertEquals(StringUtils.isNotBlank(" "), false);
        Assert.assertEquals(StringUtils.isNotBlank(" a"), true);
        Assert.assertEquals(StringUtils.isNotBlank(" a "), true);
    }

    @Test
    public void isNumeric() {
        Assert.assertFalse(StringUtils.isNumeric(null));
        Assert.assertFalse(StringUtils.isNumeric(""));
        Assert.assertFalse(StringUtils.isNumeric(" "));
        Assert.assertTrue(StringUtils.isNumeric("1"));
        Assert.assertTrue(StringUtils.isNumeric("\u0967\u0968\u0969"));
        Assert.assertFalse(StringUtils.isNumeric("1-"));
        Assert.assertFalse(StringUtils.isNumeric("+1"));
        Assert.assertFalse(StringUtils.isNumeric("-1"));
    }

    @Test
    public void abbreviate() {
        Assert.assertEquals(StringUtils.abbreviate(null, "...", 8), null);
        Assert.assertEquals(StringUtils.abbreviate("", "...", 8), "");
        Assert.assertEquals(StringUtils.abbreviate("a", "...", 8), "a...");
        Assert.assertEquals(StringUtils.abbreviate("abc", "...", 8), "abc...");
        Assert.assertEquals(StringUtils.abbreviate("abcde", "...", 8), "abcde...");
        Assert.assertEquals(StringUtils.abbreviate("ABCDEFGHIJKLMN", "...", 8), "ABCDE...");
    }

    @Test
    public void defaultIfEmpty() {
        Assert.assertEquals(StringUtils.defaultIfEmpty(null, "ab"), "ab");
        Assert.assertEquals(StringUtils.defaultIfEmpty("", "ab"), "ab");
        Assert.assertEquals(StringUtils.defaultIfEmpty(" ", "ab"), " ");
        Assert.assertEquals(StringUtils.defaultIfEmpty("1", "ab"), "1");
    }

    @Test
    public void defaultIfBlank() {
        Assert.assertEquals(StringUtils.defaultIfBlank(null, "ab"), "ab");
        Assert.assertEquals(StringUtils.defaultIfBlank("", "ab"), "ab");
        Assert.assertEquals(StringUtils.defaultIfBlank(" ", "ab"), "ab");
        Assert.assertEquals(StringUtils.defaultIfBlank("1", "ab"), "1");
    }

    @Test
    public void defaultString() {
        Assert.assertEquals(StringUtils.defaultString(null, "ab"), "ab");
        Assert.assertEquals(StringUtils.defaultString("", "ab"), "");
        Assert.assertEquals(StringUtils.defaultString("1", "ab"), "1");
    }

    @Test
    public void startsWith() {
        Assert.assertFalse(StringUtils.startsWith(null, "ab", 0, false));
        Assert.assertFalse(StringUtils.startsWith("", "ab", 0, false));
        Assert.assertFalse(StringUtils.startsWith(null, null, 0, false));
        Assert.assertFalse(StringUtils.startsWith("", "", 0, false));
        Assert.assertTrue(StringUtils.startsWith("abcd", "ab", 0, false));
        Assert.assertTrue(StringUtils.startsWith("abcd", "ab", 0, true));
        Assert.assertFalse(StringUtils.startsWith("Abcd", "ab", 0, false));
        Assert.assertTrue(StringUtils.startsWith("Abcd", "ab", 0, true));
        Assert.assertFalse(StringUtils.startsWith("AbCd", "bc", 1, false));
        Assert.assertTrue(StringUtils.startsWith("AbCd", "bc", 1, true));
        Assert.assertFalse(StringUtils.startsWith("AbCd", "bc", 3, true));
        Assert.assertFalse(StringUtils.startsWith("AbCd", "bc", 4, true));

        Assert.assertFalse(StringUtils.startsWith(null, null));
        Assert.assertFalse(StringUtils.startsWith(null, ""));
        Assert.assertFalse(StringUtils.startsWith(null, "a"));
        Assert.assertFalse(StringUtils.startsWith("", null));
        Assert.assertFalse(StringUtils.startsWith("", ""));
        Assert.assertTrue(StringUtils.startsWith("abb", "ab"));
        Assert.assertFalse(StringUtils.startsWith("abb", "Ab"));

        Assert.assertFalse(StringUtils.startsWithIgnoreCase(null, null));
        Assert.assertFalse(StringUtils.startsWithIgnoreCase(null, ""));
        Assert.assertFalse(StringUtils.startsWithIgnoreCase(null, "a"));
        Assert.assertFalse(StringUtils.startsWithIgnoreCase("", null));
        Assert.assertFalse(StringUtils.startsWithIgnoreCase("", ""));
        Assert.assertTrue(StringUtils.startsWithIgnoreCase("abb", "ab"));
        Assert.assertTrue(StringUtils.startsWithIgnoreCase("abb", "Ab"));
    }

    @Test
    public void endsWith() {
        Assert.assertFalse(StringUtils.endsWith(null, null, false));
        Assert.assertFalse(StringUtils.endsWith(null, "", false));
        Assert.assertFalse(StringUtils.endsWith("", null, false));
        Assert.assertFalse(StringUtils.endsWith("", "", false));
        Assert.assertTrue(StringUtils.endsWith("abcd", "cd", false));
        Assert.assertTrue(StringUtils.endsWith("abcd", "cD", true));

        Assert.assertFalse(StringUtils.endsWith(null, null));
        Assert.assertFalse(StringUtils.endsWith(null, ""));
        Assert.assertFalse(StringUtils.endsWith("", null));
        Assert.assertFalse(StringUtils.endsWith("", ""));
        Assert.assertTrue(StringUtils.endsWith("abcd", "cd"));
        Assert.assertFalse(StringUtils.endsWith("abcd", "cD"));

        Assert.assertFalse(StringUtils.endsWithIgnoreCase(null, null));
        Assert.assertFalse(StringUtils.endsWithIgnoreCase(null, ""));
        Assert.assertFalse(StringUtils.endsWithIgnoreCase("", null));
        Assert.assertFalse(StringUtils.endsWithIgnoreCase("", ""));
        Assert.assertTrue(StringUtils.endsWithIgnoreCase("abcd", "cd"));
        Assert.assertTrue(StringUtils.endsWithIgnoreCase("abcd", "cD"));
    }
}

package com.emily.infrastructure.common.test;

import com.emily.infrastructure.common.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 *
 * @author  Emily
 * @since  Created in 2023/6/4 2:14 PM
 */
public class StringUtilsTest {
    @Test
    public void toUpperFirstCase() {
        Assertions.assertEquals(StringUtils.toUpperFirstCase(null), null);
        Assertions.assertEquals(StringUtils.toUpperFirstCase(""), "");
        Assertions.assertEquals(StringUtils.toUpperFirstCase(" "), " ");
        Assertions.assertEquals(StringUtils.toUpperFirstCase(" a".trim()), "A");
        Assertions.assertEquals(StringUtils.toUpperFirstCase("a"), "A");
        Assertions.assertEquals(StringUtils.toUpperFirstCase("abc"), "Abc");
    }

    @Test
    public void toLowerFirstCase() {
        Assertions.assertEquals(StringUtils.toLowerFirstCase(null), null);
        Assertions.assertEquals(StringUtils.toLowerFirstCase(""), "");
        Assertions.assertEquals(StringUtils.toLowerFirstCase(" "), " ");
        Assertions.assertEquals(StringUtils.toLowerFirstCase(" A"), " A");
        Assertions.assertEquals(StringUtils.toLowerFirstCase("A"), "a");
        Assertions.assertEquals(StringUtils.toLowerFirstCase("Abc"), "abc");
    }

    @Test
    public void leftPad() {
        Assertions.assertEquals(StringUtils.leftPad(null, 5, 'a'), null);
        Assertions.assertEquals(StringUtils.leftPad("", 5, '0'), "00000");
        Assertions.assertEquals(StringUtils.leftPad("aaaaa", 5, '0'), "aaaaa");
        Assertions.assertEquals(StringUtils.leftPad("2", 5, '0'), "00002");

        Assertions.assertEquals(StringUtils.leftPad(null, 5, "ab"), null);
        Assertions.assertEquals(StringUtils.leftPad(null, 5, null), null);
        Assertions.assertEquals(StringUtils.leftPad(null, 5, ""), null);
        Assertions.assertEquals(StringUtils.leftPad("", 5, ""), "     ");
        Assertions.assertEquals(StringUtils.leftPad("1", 5, ""), "    1");
        Assertions.assertEquals(StringUtils.leftPad("1", 5, " "), "    1");
        Assertions.assertEquals(StringUtils.leftPad("1", 5, "0"), "00001");
        Assertions.assertEquals(StringUtils.leftPad("1", 5, "0A"), "0A0A1");
        Assertions.assertEquals(StringUtils.leftPad("1", 5, "0AB"), "0AB01");
        Assertions.assertEquals(StringUtils.leftPad("11", 8, "0AB"), "0AB0AB11");
        Assertions.assertEquals(StringUtils.leftPad("11", 7, "0AB"), "0AB0A11");
    }

    @Test
    public void rightPad() {
        Assertions.assertEquals(StringUtils.rightPad(null, 5, 'a'), null);
        Assertions.assertEquals(StringUtils.rightPad("", 5, '0'), "00000");
        Assertions.assertEquals(StringUtils.rightPad("aaaaa", 5, '0'), "aaaaa");
        Assertions.assertEquals(StringUtils.rightPad("2", 5, '0'), "20000");

        Assertions.assertEquals(StringUtils.rightPad(null, 5, "ab"), null);
        Assertions.assertEquals(StringUtils.rightPad(null, 5, null), null);
        Assertions.assertEquals(StringUtils.rightPad(null, 5, ""), null);
        Assertions.assertEquals(StringUtils.rightPad("", 5, ""), "     ");
        Assertions.assertEquals(StringUtils.rightPad("1", 5, ""), "1    ");
        Assertions.assertEquals(StringUtils.rightPad("1", 5, " "), "1    ");
        Assertions.assertEquals(StringUtils.rightPad("1", 5, "0"), "10000");
        Assertions.assertEquals(StringUtils.rightPad("1", 5, "0A"), "10A0A");
        Assertions.assertEquals(StringUtils.rightPad("1", 5, "0AB"), "10AB0");
        Assertions.assertEquals(StringUtils.rightPad("11", 8, "0AB"), "110AB0AB");
        Assertions.assertEquals(StringUtils.rightPad("11", 7, "0AB"), "110AB0A");
    }

    @Test
    public void isEmpty() {
        Assertions.assertTrue(StringUtils.isEmpty(null));
        Assertions.assertTrue(StringUtils.isEmpty(""));
        Assertions.assertFalse(StringUtils.isEmpty("a"));
        Assertions.assertFalse(StringUtils.isEmpty(" a"));
        Assertions.assertFalse(StringUtils.isEmpty(" a "));


        Assertions.assertFalse(StringUtils.isNotEmpty(null));
        Assertions.assertFalse(StringUtils.isNotEmpty(""));
        Assertions.assertTrue(StringUtils.isNotEmpty("a"));
        Assertions.assertTrue(StringUtils.isNotEmpty(" a"));
        Assertions.assertTrue(StringUtils.isNotEmpty(" a "));
    }

    @Test
    public void length() {
        Assertions.assertEquals(StringUtils.length(null), 0);
        Assertions.assertEquals(StringUtils.length(""), 0);
        Assertions.assertEquals(StringUtils.length("12"), 2);
    }

    @Test
    public void isBlank() {
        Assertions.assertEquals(StringUtils.isBlank(null), true);
        Assertions.assertEquals(StringUtils.isBlank(""), true);
        Assertions.assertEquals(StringUtils.isBlank(" "), true);
        Assertions.assertEquals(StringUtils.isBlank(" a"), false);
        Assertions.assertEquals(StringUtils.isBlank(" a "), false);

        Assertions.assertEquals(StringUtils.isNotBlank(null), false);
        Assertions.assertEquals(StringUtils.isNotBlank(""), false);
        Assertions.assertEquals(StringUtils.isNotBlank(" "), false);
        Assertions.assertEquals(StringUtils.isNotBlank(" a"), true);
        Assertions.assertEquals(StringUtils.isNotBlank(" a "), true);
    }

    @Test
    public void isNumeric() {
        Assertions.assertFalse(StringUtils.isNumeric(null));
        Assertions.assertFalse(StringUtils.isNumeric(""));
        Assertions.assertFalse(StringUtils.isNumeric(" "));
        Assertions.assertTrue(StringUtils.isNumeric("1"));
        Assertions.assertTrue(StringUtils.isNumeric("\u0967\u0968\u0969"));
        Assertions.assertFalse(StringUtils.isNumeric("1-"));
        Assertions.assertFalse(StringUtils.isNumeric("+1"));
        Assertions.assertFalse(StringUtils.isNumeric("-1"));
    }

    @Test
    public void abbreviate() {
        Assertions.assertEquals(StringUtils.abbreviate(null, "...", 8), null);
        Assertions.assertEquals(StringUtils.abbreviate("", "...", 8), "");
        Assertions.assertEquals(StringUtils.abbreviate("a", "...", 8), "a...");
        Assertions.assertEquals(StringUtils.abbreviate("abc", "...", 8), "abc...");
        Assertions.assertEquals(StringUtils.abbreviate("abcde", "...", 8), "abcde...");
        Assertions.assertEquals(StringUtils.abbreviate("ABCDEFGHIJKLMN", "...", 8), "ABCDE...");
    }

    @Test
    public void defaultIfEmpty() {
        Assertions.assertEquals(StringUtils.defaultIfEmpty(null, "ab"), "ab");
        Assertions.assertEquals(StringUtils.defaultIfEmpty("", "ab"), "ab");
        Assertions.assertEquals(StringUtils.defaultIfEmpty(" ", "ab"), " ");
        Assertions.assertEquals(StringUtils.defaultIfEmpty("1", "ab"), "1");
    }

    @Test
    public void defaultIfBlank() {
        Assertions.assertEquals(StringUtils.defaultIfBlank(null, "ab"), "ab");
        Assertions.assertEquals(StringUtils.defaultIfBlank("", "ab"), "ab");
        Assertions.assertEquals(StringUtils.defaultIfBlank(" ", "ab"), "ab");
        Assertions.assertEquals(StringUtils.defaultIfBlank("1", "ab"), "1");
    }

    @Test
    public void defaultString() {
        Assertions.assertEquals(StringUtils.defaultString(null, "ab"), "ab");
        Assertions.assertEquals(StringUtils.defaultString("", "ab"), "");
        Assertions.assertEquals(StringUtils.defaultString("1", "ab"), "1");
    }

    @Test
    public void startsWith() {
        Assertions.assertFalse(StringUtils.startsWith(null, "ab", 0, false));
        Assertions.assertFalse(StringUtils.startsWith("", "ab", 0, false));
        Assertions.assertFalse(StringUtils.startsWith(null, null, 0, false));
        Assertions.assertFalse(StringUtils.startsWith("", "", 0, false));
        Assertions.assertTrue(StringUtils.startsWith("abcd", "ab", 0, false));
        Assertions.assertTrue(StringUtils.startsWith("abcd", "ab", 0, true));
        Assertions.assertFalse(StringUtils.startsWith("Abcd", "ab", 0, false));
        Assertions.assertTrue(StringUtils.startsWith("Abcd", "ab", 0, true));
        Assertions.assertFalse(StringUtils.startsWith("AbCd", "bc", 1, false));
        Assertions.assertTrue(StringUtils.startsWith("AbCd", "bc", 1, true));
        Assertions.assertFalse(StringUtils.startsWith("AbCd", "bc", 3, true));
        Assertions.assertFalse(StringUtils.startsWith("AbCd", "bc", 4, true));

        Assertions.assertFalse(StringUtils.startsWith(null, null));
        Assertions.assertFalse(StringUtils.startsWith(null, ""));
        Assertions.assertFalse(StringUtils.startsWith(null, "a"));
        Assertions.assertFalse(StringUtils.startsWith("", null));
        Assertions.assertFalse(StringUtils.startsWith("", ""));
        Assertions.assertTrue(StringUtils.startsWith("abb", "ab"));
        Assertions.assertFalse(StringUtils.startsWith("abb", "Ab"));

        Assertions.assertFalse(StringUtils.startsWithIgnoreCase(null, null));
        Assertions.assertFalse(StringUtils.startsWithIgnoreCase(null, ""));
        Assertions.assertFalse(StringUtils.startsWithIgnoreCase(null, "a"));
        Assertions.assertFalse(StringUtils.startsWithIgnoreCase("", null));
        Assertions.assertFalse(StringUtils.startsWithIgnoreCase("", ""));
        Assertions.assertTrue(StringUtils.startsWithIgnoreCase("abb", "ab"));
        Assertions.assertTrue(StringUtils.startsWithIgnoreCase("abb", "Ab"));
    }

    @Test
    public void endsWith() {
        Assertions.assertFalse(StringUtils.endsWith(null, null, false));
        Assertions.assertFalse(StringUtils.endsWith(null, "", false));
        Assertions.assertFalse(StringUtils.endsWith("", null, false));
        Assertions.assertFalse(StringUtils.endsWith("", "", false));
        Assertions.assertTrue(StringUtils.endsWith("abcd", "cd", false));
        Assertions.assertTrue(StringUtils.endsWith("abcd", "cD", true));

        Assertions.assertFalse(StringUtils.endsWith(null, null));
        Assertions.assertFalse(StringUtils.endsWith(null, ""));
        Assertions.assertFalse(StringUtils.endsWith("", null));
        Assertions.assertFalse(StringUtils.endsWith("", ""));
        Assertions.assertTrue(StringUtils.endsWith("abcd", "cd"));
        Assertions.assertFalse(StringUtils.endsWith("abcd", "cD"));

        Assertions.assertFalse(StringUtils.endsWithIgnoreCase(null, null));
        Assertions.assertFalse(StringUtils.endsWithIgnoreCase(null, ""));
        Assertions.assertFalse(StringUtils.endsWithIgnoreCase("", null));
        Assertions.assertFalse(StringUtils.endsWithIgnoreCase("", ""));
        Assertions.assertTrue(StringUtils.endsWithIgnoreCase("abcd", "cd"));
        Assertions.assertTrue(StringUtils.endsWithIgnoreCase("abcd", "cD"));
    }

    @Test
    public void getBytes() throws UnsupportedEncodingException {
        Assertions.assertEquals(StringUtils.getBytes("ab", Charset.defaultCharset()).length, 2);

        Assertions.assertEquals(StringUtils.getBytes("ab", "utf-8").length, 2);
    }

    @Test
    public void replace() {
        Assertions.assertEquals(StringUtils.replace("abc", "ab", "AB"), "ABc");
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.replace("abc", "", ""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> StringUtils.replace("abc", null, ""));

        Assertions.assertEquals(StringUtils.replace("abc", 'b', 'B'), "aBc");
    }

    @Test
    public void trim() {
        Assertions.assertEquals(StringUtils.trim(null), null);
        Assertions.assertEquals(StringUtils.trim(""), "");
        Assertions.assertEquals(StringUtils.trim(" a "), "a");

        Assertions.assertEquals(StringUtils.trimToNull(" "), null);
        Assertions.assertEquals(StringUtils.trimToNull(null), null);
        Assertions.assertEquals(StringUtils.trimToNull(" a "), "a");

        Assertions.assertEquals(StringUtils.trimToEmpty(null), "");
        Assertions.assertEquals(StringUtils.trimToEmpty(""), "");
        Assertions.assertEquals(StringUtils.trimToEmpty(" a "), "a");
    }

    @Test
    public void split() {
        Assertions.assertNotNull(StringUtils.split(null, null));
        Assertions.assertNotNull(StringUtils.split("", null));
        Assertions.assertEquals(StringUtils.split("abcd", "").length, 4);
        Assertions.assertEquals(StringUtils.split("abcd", ",").length, 1);
        Assertions.assertEquals(StringUtils.split("ab,cd", ",").length, 2);
        Assertions.assertEquals(StringUtils.split("abcd", "c").length, 2);
    }
}

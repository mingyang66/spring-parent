package com.emily.infrastructure.language.test;

import com.emily.infrastructure.language.i18n.I18nChineseHelper;
import org.junit.Assert;
import org.junit.Test;

/**
 * @Description :  测试类
 * @Author :  Emily
 * @CreateDate :  Created in 2023/5/14 19:18 PM
 */
public class I18nChineseHelperTest {

    @Test
    public void test() {
        String text = "田晓霞、孙少平、孙少安、孙玉厚、孙玉婷、贺丰英、贺秀莲";
        String s = I18nChineseHelper.convertToTraditionalChinese(text);
        System.out.println(s);
        String s1 = I18nChineseHelper.convertToSimplifiedChinese(s);
        System.out.println(s1);
    }

    @Test
    public void test1() {
        System.out.println(I18nChineseHelper.convertToTraditionalChinese("标志"));
        System.out.println(I18nChineseHelper.convertToTraditionalChinese("〇"));
    }

    @Test
    public void testChinese() {
        Assert.assertTrue(I18nChineseHelper.isChinese('〇'));
    }

    @Test
    public void testContainsChinese() {
        Assert.assertTrue(I18nChineseHelper.containsChinese("emily田晓霞girl"));
    }

    @Test
    public void judge() {
        Assert.assertEquals(I18nChineseHelper.convertToTraditionalChinese("讠"), "訁");
        Assert.assertEquals(I18nChineseHelper.convertToTraditionalChinese("饣"), "飠");
        Assert.assertEquals(I18nChineseHelper.convertToTraditionalChinese("纟"), "糹");

        Assert.assertEquals(I18nChineseHelper.convertToTraditionalChinese("捕获"), "捕獲");
        Assert.assertEquals(I18nChineseHelper.convertToTraditionalChinese("获得"), "獲得");
        Assert.assertEquals(I18nChineseHelper.convertToTraditionalChinese("收获"), "收穫");

        Assert.assertEquals(I18nChineseHelper.convertToTraditionalChinese("尽管"), "儘管");
        Assert.assertEquals(I18nChineseHelper.convertToTraditionalChinese("尽力"), "盡力");
        Assert.assertEquals(I18nChineseHelper.convertToTraditionalChinese("前功尽弃"), "前功盡棄");

        Assert.assertEquals(I18nChineseHelper.convertToTraditionalChinese("紫苏"), "紫蘇");
        Assert.assertEquals(I18nChineseHelper.convertToTraditionalChinese("江苏"), "江蘇");
        Assert.assertEquals(I18nChineseHelper.convertToTraditionalChinese("苏维埃"), "蘇維埃");
        Assert.assertEquals(I18nChineseHelper.convertToSimplifiedChinese("蘇維埃"), "苏维埃");
        Assert.assertEquals(I18nChineseHelper.convertToTraditionalChinese("苏醒"), "甦醒");

        Assert.assertEquals(I18nChineseHelper.convertToTraditionalChinese("只言片语"), "隻言片語");
        Assert.assertEquals(I18nChineseHelper.convertToTraditionalChinese("一只"), "一隻");
        Assert.assertEquals(I18nChineseHelper.convertToTraditionalChinese("只不过"), "只不過");
        Assert.assertEquals(I18nChineseHelper.convertToTraditionalChinese("只有"), "只有");

        // 发挥火眼金睛发现不同
        Assert.assertEquals(I18nChineseHelper.convertToTraditionalChinese("特别"), "特別");
        Assert.assertEquals(I18nChineseHelper.convertToSimplifiedChinese("別"), "别");
        Assert.assertEquals(I18nChineseHelper.convertToTraditionalChinese("别扭"), "彆扭");
    }

    @Test
    public void jz() {
        Assert.assertEquals(I18nChineseHelper.convertToSimplifiedChinese("上市發行人可否根據購回股份的授權購回人民幣櫃台及／或港幣櫃台的證券？還是兩者都可以購回？（適用於股本證券）"),
                "上市发行人可否根据购回股份的授权购回人民币柜台及／或港币柜台的证券？还是两者都可以购回？（适用于股本证券）");
        Assert.assertEquals(I18nChineseHelper.convertToSimplifiedChinese("發行人有權自行決定"), "发行人有权自行决定");
        Assert.assertEquals(I18nChineseHelper.convertToSimplifiedChinese("上市發行人可否取消其中一種幣值櫃台？"), "上市发行人可否取消其中一种币值柜台？");
        Assert.assertEquals(I18nChineseHelper.convertToSimplifiedChinese("若某證券有兩個以港幣和人民幣計價、用於交易和交收的櫃台，可否自動成爲「雙櫃台」證券？"), "若某证券有两个以港币和人民币计价、用于交易和交收的柜台，可否自动成为“双柜台”证券？");
        Assert.assertEquals(I18nChineseHelper.convertToSimplifiedChinese("某些有港幣及人民幣櫃台的特定證券，將在雙櫃台模式推出後由交易所不時指定並公佈。"),"某些有港币及人民币柜台的特定证券，将在双柜台模式推出后由交易所不时指定并公布。");
    }
}

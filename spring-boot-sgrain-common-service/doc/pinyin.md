### Java汉字转拼音（简繁体转换）库之JPinYin
JPinyin[下载地址https://mvnrepository.com/artifact/com.github.stuxuhai/jpinyin](https://mvnrepository.com/artifact/com.github.stuxuhai/jpinyin)

>JPinyin是一个开源的汉字转拼音库，它是在PinYin4j的基础上做了改进，支持汉字转拼音、获取汉字拼音首字母、简体繁体相互转换、多音字、词典扩展等；

最新版本
```
<!-- https://mvnrepository.com/artifact/com.github.stuxuhai/jpinyin -->
<dependency>
    <groupId>com.github.stuxuhai</groupId>
    <artifactId>jpinyin</artifactId>
    <version>1.1.8</version>
</dependency>
```
自己封装了个工具类，具体使用方法注解上有说明：
```
package com.yaomy.control.common.control.utils;

import com.github.stuxuhai.jpinyin.ChineseHelper;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import LoggerUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description: 汉字转拼音
 * @Version: 1.0
 */
public class PinYinUtils {
    /**
     * 转换为每个汉字对应拼音首字母字符串
     * @param pinYinStr 需转换的汉字
     * @return 拼音字符串
     */
    public static String getPinYinPrefix(String pinYinStr){
        return getPinYinPrefix(pinYinStr, false);
    }
    /**
     * 转换为每个汉字对应拼音首字母字符串
     * @param pinYinStr 需转换的汉字
     * @param uppercase true 大写，false 默认
     * @return 拼音字符串
     */
    public static String getPinYinPrefix(String pinYinStr, boolean uppercase){
        if(StringUtils.isEmpty(pinYinStr)){
            return pinYinStr;
        }
        try{
            String tempStr = PinyinHelper.getShortPinyin(pinYinStr);
            if(uppercase){
                return StringUtils.upperCase(tempStr);
            } else {
                return tempStr;
            }
        } catch (Exception e){
            e.printStackTrace();
            LoggerUtil.error(PinYinUtils.class, "汉字转拼音首字母异常："+e.toString());
            return pinYinStr;
        }
    }
    /**
     * 获取汉字全品音
     * @param pinYinStr 要转换的字符串
     * @return 汉语拼音
     */
    public static String getPinYin(String pinYinStr){
        return getPinYin(pinYinStr, StringUtils.EMPTY, PinyinFormat.WITHOUT_TONE, false);
    }
    /**
     * 获取汉字全品音
     * @param pinYinStr 要转换的字符串
     * @param uppercase true 大写，false 默认
     * @return 汉语拼音
     */
    public static String getPinYin(String pinYinStr, boolean uppercase){
        return getPinYin(pinYinStr, StringUtils.EMPTY, PinyinFormat.WITHOUT_TONE, uppercase);
    }
    /**
     * 获取汉字全品音
     * @param pinYinStr 要转换的字符串
     * @param separator 分隔符
     * @param uppercase true 大写，false 默认
     * @return 汉语拼音
     */
    public static String getPinYin(String pinYinStr, String separator, boolean uppercase){
        return getPinYin(pinYinStr, separator, PinyinFormat.WITHOUT_TONE, uppercase);
    }
    /**
     * 获取汉字全品音
     * @param pinYinStr 要转换的字符串
     * @param separator 分隔符
     * @param pinyinFormat 拼音格式 WITH_TONE_MARK（带声调）,WITHOUT_TONE（不带声调） WITH_TONE_NUMBER（带声调数字）
     * @param uppercase true 大写，false 默认
     * @return 汉语拼音
     */
    public static String getPinYin(String pinYinStr, String separator, PinyinFormat pinyinFormat, boolean uppercase){
        if(StringUtils.isEmpty(pinYinStr)){
            return pinYinStr;
        }
        try{
            String tempStr = PinyinHelper.convertToPinyinString(pinYinStr, separator, pinyinFormat);
            if(uppercase){
                return StringUtils.upperCase(tempStr);
            } else {
                return tempStr;
            }
        } catch (Exception e){
            e.printStackTrace();
            LoggerUtil.error(PinYinUtils.class, "汉字转拼音首字母异常："+e.toString());
            return pinYinStr;
        }
    }
    /**
     * 判断一个汉字是否为多音字
     * @param c 汉字
     * @return 判断结果，是汉字返回true，否则返回false
     */
    public static boolean hasMultiPinyin(char c){
        return PinyinHelper.hasMultiPinyin(c);
    }

    /**
     * 将单个汉字转换成带声调格式的拼音
     * @param c 需要转换成拼音的汉字
     * @return 字符串的拼音
     */
    public static String[] convertToPinyinArray(char c){
        return PinyinHelper.convertToPinyinArray(c);
    }
    /**
     * 将单个汉字转换为相应格式的拼音
     * @param c 需要转换成拼音的汉字
     * @param pinyinFormat 拼音格式：WITH_TONE_NUMBER--数字代表声调，WITHOUT_TONE--不带声调，WITH_TONE_MARK--带声调
     * @return 汉字的拼音
     */
    public static String[] convertToPinyinArray(char c, PinyinFormat pinyinFormat){
        return PinyinHelper.convertToPinyinArray(c, pinyinFormat);
    }
    /**
     * 繁体字转为简体字
     * @param wordStr 字符串
     * @return 转换后的简体字符串
     */
    public static String convertToSimplifiedChinese(String wordStr){
        if(StringUtils.isEmpty(wordStr)){
            return wordStr;
        }
        return ChineseHelper.convertToSimplifiedChinese(wordStr);
    }
    /**
     * 简体字转换为繁体字
     * @param wordStr 字符串
     * @return 转换后的简体字符串
     */
    public static String convertToTraditionalChinese(String wordStr){
        if(StringUtils.isEmpty(wordStr)){
            return wordStr;
        }
        return ChineseHelper.convertToTraditionalChinese(wordStr);
    }

    /**
     * 判断word是否为简体中文
     * @param word
     * @return true 是，false 否
     */
    public static boolean isChinese(char word){
        return ChineseHelper.isChinese(word);
    }

    /**
     * 判断word是否为繁体字
     * @param word 字符
     * @return true 是，false 否
     */
    public static boolean isTraditionalChinese(char word){
        return ChineseHelper.isTraditionalChinese(word);
    }

    /**
     * 判断字符串是否包含中文
     * @param wordStr 要判断的字符串
     * @return true 包含，false 不包含
     */
    public static boolean containsChinese(String wordStr){
        if(StringUtils.isEmpty(wordStr)){
            return false;
        }
        return ChineseHelper.containsChinese(wordStr);
    }
}
```

GitHub地址：[https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-common-service/pinyin.md](https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-common-service/pinyin.md)

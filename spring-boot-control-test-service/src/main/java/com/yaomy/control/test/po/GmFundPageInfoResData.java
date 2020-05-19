package com.yaomy.control.test.po;

import com.google.common.collect.Lists;


import java.util.List;

public class GmFundPageInfoResData {
    //基金首页板块信息
    private Page page = new Page();

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }


    public static class Page {
        //页面编号
        private String pageCode;
        //板块列表
        private List<Bk> bkList = Lists.newArrayList();

        public String getPageCode() {
            return pageCode;
        }

        public void setPageCode(String pageCode) {
            this.pageCode = pageCode;
        }

        public List<Bk> getBkList() {
            return bkList;
        }

        public void setBkList(List<Bk> bkList) {
            this.bkList = bkList;
        }
    }


    public static class Bk {
        //板块编号
        private String bkCode;
        //板块标题
        private String title;
        //板块子标题
        private String subTitle;
        //是否板块介绍 1-是，0-否
        private Integer isBlockIntroduce;
        //IOS介绍
        private String iosIntroduce;
        //安卓介绍
        private String androidIntroduce;
        //H5介绍
        private String h5Introduce;
        //是否更多，1-是，0-否
        private Integer isMore;
        //更多文字
        private String moreText;
        //IOS更多跳转链接
        private String iosMoreUrl;
        //安卓更多跳转链接
        private String androidMoreUrl;
        //H5更多跳转链接
        private String h5MoreUrl;
        //基金列表
        private List<Fund> fundList = Lists.newArrayList();

        public String getBkCode() {
            return bkCode;
        }

        public void setBkCode(String bkCode) {
            this.bkCode = bkCode;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public Integer getIsBlockIntroduce() {
            return isBlockIntroduce;
        }

        public void setIsBlockIntroduce(Integer isBlockIntroduce) {
            this.isBlockIntroduce = isBlockIntroduce;
        }

        public String getIosIntroduce() {
            return iosIntroduce;
        }

        public void setIosIntroduce(String iosIntroduce) {
            this.iosIntroduce = iosIntroduce;
        }

        public String getAndroidIntroduce() {
            return androidIntroduce;
        }

        public void setAndroidIntroduce(String androidIntroduce) {
            this.androidIntroduce = androidIntroduce;
        }

        public String getH5Introduce() {
            return h5Introduce;
        }

        public void setH5Introduce(String h5Introduce) {
            this.h5Introduce = h5Introduce;
        }

        public Integer getIsMore() {
            return isMore;
        }

        public void setIsMore(Integer isMore) {
            this.isMore = isMore;
        }

        public String getMoreText() {
            return moreText;
        }

        public void setMoreText(String moreText) {
            this.moreText = moreText;
        }

        public String getIosMoreUrl() {
            return iosMoreUrl;
        }

        public void setIosMoreUrl(String iosMoreUrl) {
            this.iosMoreUrl = iosMoreUrl;
        }

        public String getAndroidMoreUrl() {
            return androidMoreUrl;
        }

        public void setAndroidMoreUrl(String androidMoreUrl) {
            this.androidMoreUrl = androidMoreUrl;
        }

        public String getH5MoreUrl() {
            return h5MoreUrl;
        }

        public void setH5MoreUrl(String h5MoreUrl) {
            this.h5MoreUrl = h5MoreUrl;
        }

        public List<Fund> getFundList() {
            return fundList;
        }

        public void setFundList(List<Fund> fundList) {
            this.fundList = fundList;
        }
    }


    public static class Fund {
        //基金ISIN编号
        private String isinCode;
        //是否跳活动链接，1-是，0-否
        private Integer isJumpActivityUrl;
        //IOS活动链接
        private String iosActivityUrl;
        //安卓活动链接
        private String androidActivityUrl;
        //Web板块介绍
        private String webBlockIntroduce;
        //自定义基金名称
        private String customFundName;
        //指标列表
        private List<Indicator> indicatorList = Lists.newArrayList();
        //标签列表
        private List<Label> labelList = Lists.newArrayList();

        public String getIsinCode() {
            return isinCode;
        }

        public void setIsinCode(String isinCode) {
            this.isinCode = isinCode;
        }

        public Integer getIsJumpActivityUrl() {
            return isJumpActivityUrl;
        }

        public void setIsJumpActivityUrl(Integer isJumpActivityUrl) {
            this.isJumpActivityUrl = isJumpActivityUrl;
        }

        public String getIosActivityUrl() {
            return iosActivityUrl;
        }

        public void setIosActivityUrl(String iosActivityUrl) {
            this.iosActivityUrl = iosActivityUrl;
        }

        public String getAndroidActivityUrl() {
            return androidActivityUrl;
        }

        public void setAndroidActivityUrl(String androidActivityUrl) {
            this.androidActivityUrl = androidActivityUrl;
        }

        public String getWebBlockIntroduce() {
            return webBlockIntroduce;
        }

        public void setWebBlockIntroduce(String webBlockIntroduce) {
            this.webBlockIntroduce = webBlockIntroduce;
        }

        public String getCustomFundName() {
            return customFundName;
        }

        public void setCustomFundName(String customFundName) {
            this.customFundName = customFundName;
        }

        public List<Indicator> getIndicatorList() {
            return indicatorList;
        }

        public void setIndicatorList(List<Indicator> indicatorList) {
            this.indicatorList = indicatorList;
        }

        public List<Label> getLabelList() {
            return labelList;
        }

        public void setLabelList(List<Label> labelList) {
            this.labelList = labelList;
        }
    }


    public static class Indicator {
        //是否展示，1-是，0-否
        private Integer isDisplay;
        //指标类型
        private String indicatorType;
        //指标类型文本
        private String indicatorText;
        //指标名称
        private String indicatorName;
        //指标值
        private String indicatorValue;

        public Integer getIsDisplay() {
            return isDisplay;
        }

        public void setIsDisplay(Integer isDisplay) {
            this.isDisplay = isDisplay;
        }

        public String getIndicatorType() {
            return indicatorType;
        }

        public void setIndicatorType(String indicatorType) {
            this.indicatorType = indicatorType;
        }

        public String getIndicatorText() {
            return indicatorText;
        }

        public void setIndicatorText(String indicatorText) {
            this.indicatorText = indicatorText;
        }

        public String getIndicatorName() {
            return indicatorName;
        }

        public void setIndicatorName(String indicatorName) {
            this.indicatorName = indicatorName;
        }

        public String getIndicatorValue() {
            return indicatorValue;
        }

        public void setIndicatorValue(String indicatorValue) {
            this.indicatorValue = indicatorValue;
        }
    }


    public static class Label {
        //是否展示，1-是，0-否
        private Integer isDisplay;
        //标签名
        private String labelName;

        public Integer getIsDisplay() {
            return isDisplay;
        }

        public void setIsDisplay(Integer isDisplay) {
            this.isDisplay = isDisplay;
        }

        public String getLabelName() {
            return labelName;
        }

        public void setLabelName(String labelName) {
            this.labelName = labelName;
        }
    }

}

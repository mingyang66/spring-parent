package com.sgrain.boot.quartz.job;

import com.sgrain.boot.common.enums.DateFormatEnum;
import com.sgrain.boot.common.utils.date.DateUtils;
import com.sgrain.boot.common.utils.json.JSONUtils;
import com.sgrain.boot.web.httpclient.HttpClientService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.Map;

/**
 * @program: spring-parent
 * @description:
 * @create: 2020/08/28
 */
public class ThreadPoolJob extends QuartzJobBean {
    @Autowired
    private HttpClientService httpClientService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Trigger trigger = context.getTrigger();
        String valeu = context.getTrigger().getJobDataMap().getString("tragger_param");
        String jobValue = context.getJobDetail().getJobDataMap().getString("jobDataKey");
        System.out.println(JSONUtils.toJSONPrettyString(context.getMergedJobDataMap()));
        System.out.println(Thread.currentThread().getName() + "--执行作业调度--" + DateUtils.formatDate(new Date(), DateFormatEnum.YYYY_MM_DD_HH_MM_SS.getFormat())+"--"+valeu+"--"+jobValue);
        String url = "http://127.0.0.1:9005/api/threadPool/metrics";
        Map<String, Object> dataMap = httpClientService.get(url, Map.class);
        System.out.println(JSONUtils.toJSONPrettyString(dataMap));
    }
}

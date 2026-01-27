package com.emily.infrastructure.logback.configuration.filter;

import ch.qos.logback.classic.turbo.MarkerFilter;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.FilterReply;
import com.emily.infrastructure.logback.common.StrUtils;

/**
 * 日志的过滤级别
 *
 * @author Emily
 * @since : 2020/08/04
 */
public class LogAcceptMarkerFilter {
    private final Context context;

    public LogAcceptMarkerFilter(Context context) {
        this.context = context;
    }

    /**
     * 全局标记过滤器，接受指定标记的日志记录到文件中
     *
     * @param marker marker标识
     * @return 标记过滤器，将会接受被标记的日志记录到文件中
     */
    public MarkerFilter getFilter(String marker) {
        MarkerFilter filter = new MarkerFilter();
        //过滤器名称
        filter.setName(StrUtils.join("AcceptMarkerFilter-", marker));
        //上下文
        filter.setContext(context);
        //日志过滤级别
        filter.setMarker(marker);
        //设置符合条件的日志接受
        filter.setOnMatch(FilterReply.ACCEPT.name());
        //不符合条件的日志拒绝
        filter.setOnMismatch(FilterReply.DENY.name());
        //添加内部状态信息
        filter.addInfo("Build AcceptMarkerFilter Success");
        //标记为启用状态
        filter.start();
        return filter;
    }
}

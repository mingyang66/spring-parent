package com.emily.infrastructure.logback.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 标记全局过滤器控制
 */
public class Marker {
    /**
     * 接受指定标记的日志记录到文件中
     */
    private List<String> acceptMarker = new ArrayList<>();
    /**
     * 拒绝标记的日志记录到文件中
     */
    private List<String> denyMarker = new ArrayList<>();

    public List<String> getAcceptMarker() {
        return acceptMarker;
    }

    public void setAcceptMarker(List<String> acceptMarker) {
        this.acceptMarker = acceptMarker;
    }

    public List<String> getDenyMarker() {
        return denyMarker;
    }

    public void setDenyMarker(List<String> denyMarker) {
        this.denyMarker = denyMarker;
    }
}

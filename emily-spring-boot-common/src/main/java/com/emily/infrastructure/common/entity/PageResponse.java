package com.emily.infrastructure.common.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Emily
 * @program: spring-parent
 * @description: 分页结果
 * @create: 2021/04/12
 */
public class PageResponse<T> implements Serializable {
    /**
     * 页码
     */
    private Integer pageIndex;
    /**
     * 页面数据大小
     */
    private Integer pageSize;
    /**
     * 总页数
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalPages;
    /**
     * 总数据量
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long totalSizes;
    /**
     * 结果集
     */
    private List<T> data;

    /**
     * 获取分页实例对象
     *
     * @param data 结果集
     * @param <T>  结果类型
     */
    public static <T> PageResponse<T> of(List<T> data) {
        return of(1, 20, null, null, data);
    }

    /**
     * 获取分页实例对象
     *
     * @param pageIndex 页码
     * @param pageSize  页面数据大小
     * @param data      结果集
     * @param <T>       结果类型
     */
    public static <T> PageResponse<T> of(Integer pageIndex, Integer pageSize, List<T> data) {
        return of(pageIndex, pageSize, null, null, data);
    }

    /**
     * 获取分页实例对象
     *
     * @param pageIndex  页码
     * @param pageSize   页面数据大小
     * @param totalPages 总页数
     * @param totalSizes 总数据量
     * @param data       结果集
     * @param <T>        结果类型
     */
    public static <T> PageResponse<T> of(Integer pageIndex, Integer pageSize, Integer totalPages, Long totalSizes, List<T> data) {
        return new PageResponse<>(pageIndex, pageSize, totalPages, totalSizes, data);
    }

    public PageResponse(Integer pageIndex, Integer pageSize, Integer totalPages, Long totalSizes, List<T> data) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.totalPages = totalPages;
        this.totalSizes = totalSizes;
        this.data = data;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Long getTotalSizes() {
        return totalSizes;
    }

    public void setTotalSizes(Long totalSizes) {
        this.totalSizes = totalSizes;
    }

    public List<T> getData() {
        return this.data == null ? new ArrayList<>() : this.data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}

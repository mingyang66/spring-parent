package com.emily.infrastructure.common.data;


import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @program: spring-parent
 * @description: 分页查询参数
 * @create: 2021/04/12
 */
public class PageRequest implements Serializable {
    /**
     * 页码
     */
    private Integer pageIndex = 1;
    /**
     * 页面数据大小
     */
    private Integer pageSize = 20;
    /**
     * 排序字段
     */
    private List<String> sorts;

    public static PageRequest of(Integer pageIndex, Integer pageSize) {
        return of(pageIndex, pageSize, null);
    }

    public static PageRequest of(Integer pageIndex, Integer pageSize, List<String> sorts) {
        return new PageRequest(pageIndex, pageSize, sorts);
    }

    public PageRequest(Integer pageIndex, Integer pageSize, List<String> sorts) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.sorts = sorts;
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

    public List<String> getSorts() {
        return sorts;
    }

    public void setSorts(List<String> sorts) {
        this.sorts = sorts;
    }

    /**
     * 排序字段转换为Map字典类型
     */
    public Optional<Map<String, Integer>> sortsToMap() {
        if (this.sorts == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(sorts.stream().filter(s -> s.length() > 2).collect(Collectors.toMap(s -> s.split("=")[0], s -> Integer.valueOf(s.split("=")[1]))));
    }
}

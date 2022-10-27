package com.emily.infrastructure.test.po.sensitive;

import com.emily.infrastructure.common.sensitive.JsonIgnore;
import com.emily.infrastructure.common.sensitive.SensitiveType;

/**
 * @Description :
 * @Author :  姚明洋
 * @CreateDate :  Created in 2022/10/27 2:51 下午
 */
public class MapperIgnore {
    @JsonIgnore(SensitiveType.USERNAME)
    private String id;

    private String total;
    @JsonIgnore
    private String entity_id;
    private String percentage;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }
}

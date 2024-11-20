package com.emily.infrastructure.sample.web.controller.valid;

import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.Length;

/**
 * @author :  Emily
 * @since :  2024/1/7 10:50 AM
 */
public class LengthReq {
    @Min(value = 10, message = "长度必须大于10")
    public int length;
    @Min(value = 5, message = "数字长度必须大于5")
    public String width;
    @Length(min = 10, max = 20, message = "长度必须大于10小于20")
    public String cd;
}

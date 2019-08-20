package com.yaomy.control.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yaomy.control.common.po.BaseResponse;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: Description
 * @ProjectName: spring-parent
 * @Package: com.yaomy.common.utils.HttpUtils
 * @Date: 2019/7/18 9:34
 * @Version: 1.0
 */
public class HttpUtils {
    /**
     * 异常输出工具类
     */
    public static void writeError(BaseResponse bs, HttpServletResponse response) throws IOException {
        response.setContentType("application/json,charset=utf-8");
        response.setStatus(bs.getStatus());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getOutputStream(), bs);
    }
    /**
     * SUCESS输出工具类
     */
    public static void writeSuccess(BaseResponse bs, HttpServletResponse response) throws IOException {
        response.setContentType("application/json,charset=utf-8");
        response.setStatus(bs.getStatus());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(response.getOutputStream(), bs);
    }
}

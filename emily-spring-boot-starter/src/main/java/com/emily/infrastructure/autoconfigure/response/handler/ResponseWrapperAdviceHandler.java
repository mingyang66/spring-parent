package com.emily.infrastructure.autoconfigure.response.handler;

import com.emily.infrastructure.autoconfigure.response.ResponseWrapperProperties;
import com.emily.infrastructure.autoconfigure.response.annotation.ApiResponseWrapperIgnore;
import com.emily.infrastructure.core.entity.BaseResponse;
import com.emily.infrastructure.core.entity.BaseResponseBuilder;
import com.emily.infrastructure.core.exception.HttpStatusType;
import com.emily.infrastructure.core.helper.MatchUtils;
import com.emily.infrastructure.json.JsonUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @Description :  返回值包装类统一处理
 * @Author :  Emily
 * @CreateDate :  Created in 2023/7/1 3:02 PM
 */
@RestControllerAdvice
public class ResponseWrapperAdviceHandler implements ResponseBodyAdvice<Object> {

    private final ResponseWrapperProperties properties;

    public ResponseWrapperAdviceHandler(ResponseWrapperProperties properties) {
        this.properties = properties;
    }

    /**
     * 指定支持的数据类型
     *
     * @param returnType    the return type
     * @param converterType the selected converter type
     * @return true-支持所有类型
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * -------------------------------------------------
     * 参数说明：
     * body:如果返回值是ResponseEntity类型，则此方法拿到的是去除外层ResponseEntity后的body
     * response:可以通过此对象更改响应对象请求头信息
     * -------------------------------------------------
     *
     * @param body                  the body to be written
     * @param returnType            the return type of the controller method
     * @param selectedContentType   the content type selected through content negotiation
     * @param selectedConverterType the converter type selected to write to the response
     * @param request               the current request
     * @param response              the current response
     * @return 包装处理后的数据
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 如果返回值已经是BaseResponse类型(包括控制器直接返回是BaseResponse和返回是ResponseEntity<BaseResponse>类型)，则直接返回
        if (body instanceof BaseResponse) {
            return body;
        }
        // 如果控制器上标注类忽略包装注解，则直接返回
        else if (returnType.hasMethodAnnotation(ApiResponseWrapperIgnore.class)) {
            return body;
        }
        // 如果请求URL在指定的排除URL集合，则直接返回
        else if (MatchUtils.match(properties.getExclude(), request.getURI().getPath())) {
            return body;
        }
        // 如果返回值是数据流类型，则直接返回
        else if (MediaType.APPLICATION_OCTET_STREAM.equals(selectedContentType)) {
            return body;
        }

        //------------------------------------------对返回值进行包装处理分割线-----------------------------------------------------------------
        BaseResponseBuilder<Object> builder = new BaseResponseBuilder<>()
                .withStatus(HttpStatusType.OK.getStatus())
                .withMessage(HttpStatusType.OK.getMessage());
        // 如果返回值是void类型，则直接返回BaseResponse空对象
        if (returnType.getParameterType().equals(Void.class)) {
            return builder.build();
        }
        // 如果是字符串类型，将其包装成BaseResponse类型
        // 如果是字符串类型，外层有ResponseEntity包装，将其包装成BaseResponse类型
        else if (MediaType.TEXT_PLAIN.equals(selectedContentType)) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return JsonUtils.toJSONString(builder.withData(body).build());
        }

        return builder.withData(body).build();
    }
}

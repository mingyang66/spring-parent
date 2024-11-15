package com.emily.infrastructure.web.response.interceptor;

import com.emily.infrastructure.common.RegexPathMatcher;
import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.web.exception.type.AppStatusType;
import com.emily.infrastructure.web.response.ResponseProperties;
import com.emily.infrastructure.web.response.annotation.ApiResponsePackIgnore;
import com.emily.infrastructure.web.response.entity.BaseResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Nonnull;

/**
 * ---------------------------------------------------------------
 * 使用说明：
 * 1.此AOP拦截器只会拦截到使用@ResponseBody获取ResponseEntity返回的响应；
 * 2.如果在springboot中使用HttpServletResponse.getOutputStream.write()将图片返回给客户端，那么ResponseBodyAdvice是无法拦截到响应；
 * RequestMapping参数说明：
 * 1.produces:指定返回的内容类型，仅当请求header中的Accept类型中包含该指定类型才能返回；
 * 2.consumes:指定处理请求的提交内容类型（ContentType），如：application/json
 * ---------------------------------------------------------------
 * <p>
 * 返回值包装类统一处理
 *
 * @author Emily
 * @since Created in 2023/7/1 3:02 PM
 */
@RestControllerAdvice
public class DefaultResponseAdviceInterceptor implements ResponseBodyAdvice<Object> {
    /**
     * 是 Spring Boot 2.2 版本及以上版本中引入的 Actuator V3 版本的媒体类型，用于获取应用程序运行时的信息和指标
     */
    public static final String APPLICATION_ACTUATOR_JSON = "application/vnd.spring-boot.actuator.v3+json";

    private final ResponseProperties properties;

    public DefaultResponseAdviceInterceptor(ResponseProperties properties) {
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
    public boolean supports(@Nonnull MethodParameter returnType, @Nonnull Class<? extends HttpMessageConverter<?>> converterType) {
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
    public Object beforeBodyWrite(Object body, @Nonnull MethodParameter returnType, @Nonnull MediaType selectedContentType, @Nonnull Class<? extends HttpMessageConverter<?>> selectedConverterType, @Nonnull ServerHttpRequest request, @Nonnull ServerHttpResponse response) {
        // 如果返回值已经是BaseResponse类型(包括控制器直接返回是BaseResponse和返回是ResponseEntity<BaseResponse>类型)，则直接返回
        if (body instanceof BaseResponse) {
            return body;
        }
        // 如果控制器上标注类忽略包装注解，则直接返回
        else if (returnType.hasMethodAnnotation(ApiResponsePackIgnore.class)) {
            return body;
        }
        // 如果请求URL在指定的排除URL集合，则直接返回
        else if (RegexPathMatcher.matcherAny(properties.getExclude(), request.getURI().getPath())) {
            return body;
        }
        // 如果返回值是数据流类型，则直接返回
        else if (MediaType.APPLICATION_OCTET_STREAM.equals(selectedContentType)
                || MediaType.APPLICATION_PDF.equals(selectedContentType)
                || MediaType.IMAGE_PNG.equals(selectedContentType)
                || MediaType.IMAGE_JPEG.equals(selectedContentType)
                || MediaType.IMAGE_GIF.equals(selectedContentType)
                || APPLICATION_ACTUATOR_JSON.equals(selectedContentType.toString())) {
            return body;
        }

        //------------------------------------------对返回值进行包装处理分割线-----------------------------------------------------------------
        BaseResponse<Object> baseResponse = new BaseResponse<>()
                .status(AppStatusType.OK.getStatus())
                .message(AppStatusType.OK.getMessage());
        // 如果返回值是void类型，则直接返回BaseResponse空对象
        if (returnType.getParameterType().equals(Void.class)) {
            return baseResponse;
        }
        // 如果是字符串类型，将其包装成BaseResponse类型
        // 如果是字符串类型，外层有ResponseEntity包装，将其包装成BaseResponse类型
        else if (MediaType.TEXT_PLAIN.equals(selectedContentType)) {
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            return JsonUtils.toJSONString(baseResponse.data(body));
        }

        return baseResponse.data(body);
    }
}

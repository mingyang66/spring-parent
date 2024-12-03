package com.otter.test.servlet;


import com.emily.infrastructure.common.constant.HeaderInfo;
import com.otter.infrastructure.servlet.RequestUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Enumeration;
import java.util.Map;

import static org.mockito.Mockito.when;

// 假设这是一个使用JUnit和Mockito的测试类
public class RequestUtilsTest {

    private HttpServletRequest request;
    private ServletRequestAttributes requestAttributes;

    @BeforeEach
    public void setUp() {
        // 使用Mockito来模拟一个HttpServletRequest对象
        request = Mockito.mock(HttpServletRequest.class);
        // 你可以根据需要进一步配置mock对象的行为
        requestAttributes = Mockito.mock(ServletRequestAttributes.class);
        RequestContextHolder.setRequestAttributes(requestAttributes);
        when(requestAttributes.getRequest()).thenReturn(request);
    }

    @AfterEach
    public void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void testGetClientIp_NotNull() {
        // 假设getClientIp方法应该返回一个非空字符串
        when(request.getHeader(HeaderInfo.X_FORWARDED_FOR)).thenReturn("127.0.0.1");
        // 执行断言
        Assertions.assertNotNull(RequestUtils.getClientIp(request));
    }

    @Test
    public void testIsInternet_True() {
        Assertions.assertTrue(RequestUtils.isInternet("127.0.0.1"));
        Assertions.assertTrue(RequestUtils.isInternet("172.30.71.3"));
        Assertions.assertTrue(RequestUtils.isInternet("10.30.71.35"));
        Assertions.assertFalse(RequestUtils.isInternet("156.23.59.45"));
    }

    @Test
    public void getHeaderOrDefault() {
        when(RequestUtils.getHeader(HeaderInfo.LANGUAGE, false)).thenReturn("en");
        Assertions.assertEquals(RequestUtils.getHeaderOrDefault(HeaderInfo.LANGUAGE, "zh-CN"), "en");

        when(RequestUtils.getHeader(HeaderInfo.LANGUAGE, false)).thenReturn("");
        Assertions.assertEquals(RequestUtils.getHeaderOrDefault(HeaderInfo.LANGUAGE, "zh-CN"), "zh-CN");

        when(RequestUtils.getHeader(HeaderInfo.LANGUAGE, false)).thenReturn("  ");
        Assertions.assertEquals(RequestUtils.getHeaderOrDefault(HeaderInfo.LANGUAGE, "zh-CN"), "zh-CN");

        when(RequestUtils.getHeader(HeaderInfo.LANGUAGE, false)).thenReturn(null);
        Assertions.assertEquals(RequestUtils.getHeaderOrDefault(HeaderInfo.LANGUAGE, "zh-CN"), "zh-CN");
    }

    @Test
    public void getHeaders() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> RequestUtils.getHeaders(null));
        Assertions.assertEquals(RequestUtils.getHeaders(request).size(), 0);

        when(request.getHeaderNames()).thenReturn(new Enumeration<String>() {
            private final String[] headers = {"x-forwarded-for", "language"};
            private int index = 0;

            @Override
            public boolean hasMoreElements() {
                return index < headers.length;
            }

            @Override
            public String nextElement() {
                return headers[index++];
            }
        });
        when(request.getHeader(HeaderInfo.X_FORWARDED_FOR)).thenReturn("127.0.0.1");
        when(request.getHeader(HeaderInfo.LANGUAGE)).thenReturn("en");
        Map<String, Object> headers = RequestUtils.getHeaders(request);
        Assertions.assertEquals(headers.size(), 2);
        Assertions.assertEquals(headers.get(HeaderInfo.LANGUAGE), "en");
        Assertions.assertEquals(headers.get(HeaderInfo.X_FORWARDED_FOR), "127.0.0.1");
    }
}

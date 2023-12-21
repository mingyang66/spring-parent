package com.emily.infrastructure.core.helper;


import com.emily.infrastructure.core.constant.HeaderInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

// 假设这是一个使用JUnit和Mockito的测试类
public class RequestUtilsTest {

    private HttpServletRequest request;

    @BeforeEach
    public void setUp() {
        // 使用Mockito来模拟一个HttpServletRequest对象
        request = Mockito.mock(HttpServletRequest.class);
        // 你可以根据需要进一步配置mock对象的行为
    }

    @Test
    public void testGetClientIp_NotNull() {
        // 假设getClientIp方法应该返回一个非空字符串
        Mockito.when(request.getHeader(HeaderInfo.X_FORWARDED_FOR)).thenReturn("127.0.0.1");
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
}

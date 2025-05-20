package com.emily.infrastructure.test.web;

import com.emily.infrastructure.common.constant.HeaderInfo;
import com.emily.infrastructure.language.i18n.LanguageType;
import com.emily.infrastructure.language.i18n.registry.I18nSimpleRegistry;
import com.emily.infrastructure.web.exception.entity.BusinessException;
import com.emily.infrastructure.web.exception.entity.RemoteInvokeException;
import com.emily.infrastructure.web.response.enums.ApplicationStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;


/**
 * @author :  姚明洋
 * @since :  2025/5/19 下午4:50
 */
public class BusinessExceptionTest {
    @Test
    void testBusinessException() {
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            throw new BusinessException();
        });
        Assertions.assertEquals("网络异常，请稍后再试", exception.getMessage());

        BusinessException exception1 = Assertions.assertThrows(BusinessException.class, () -> {
            throw new BusinessException(100000, "你好{0},我是{1}", true, "李世民", "长孙嘉敏");
        });
        Assertions.assertEquals("你好李世民,我是长孙嘉敏", exception1.getMessage());

        BusinessException exception2 = Assertions.assertThrows(BusinessException.class, () -> {
            throw new BusinessException(100000, "你好{0},我是{1}", true, "李世民");
        });
        Assertions.assertEquals("你好李世民,我是{1}", exception2.getMessage());

        BusinessException exception3 = Assertions.assertThrows(BusinessException.class, () -> {
            throw new BusinessException(100000, "你好{0},我是{1}", true);
        });
        Assertions.assertEquals("你好{0},我是{1}", exception3.getMessage());

        BusinessException exception4 = Assertions.assertThrows(BusinessException.class, () -> {
            throw new BusinessException(ApplicationStatus.ILLEGAL_ARGUMENT);
        });
        Assertions.assertEquals("非法参数", exception4.getMessage());

        BusinessException exception5 = Assertions.assertThrows(BusinessException.class, () -> {
            throw new BusinessException(ApplicationStatus.ILLEGAL_ARGUMENT.getStatus(), ApplicationStatus.ILLEGAL_ARGUMENT.getMessage());
        });
        Assertions.assertEquals("非法参数", exception5.getMessage());
    }

    @Test
    void testBusinessExceptionEn() {
        I18nSimpleRegistry.getEnRegistry().putAll(Map.of(
                "网络异常，请稍后再试", "Network exception, please try again later.",
                "你好{0},我是{1}", "Hello {0},I am {1}",
                "非法参数", "IllegalArgument"));
        try (MockedStatic<RequestContextHolder> mocked = mockStatic(RequestContextHolder.class)) {
            //创建模拟对象
            HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
            ServletRequestAttributes mockRequestAttributes = Mockito.mock(ServletRequestAttributes.class);
            when(mockRequestAttributes.getRequest()).thenReturn(mockRequest);
            when(mockRequest.getHeader(HeaderInfo.LANGUAGE)).thenReturn(LanguageType.EN.getCode());

            //设置静态方法行为
            mocked.when(RequestContextHolder::getRequestAttributes).thenReturn(mockRequestAttributes);

            BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
                throw new BusinessException();
            });
            Assertions.assertEquals("Network exception, please try again later.", exception.getMessage());

            BusinessException exception1 = Assertions.assertThrows(BusinessException.class, () -> {
                throw new BusinessException(100000, "你好{0},我是{1}", true, "李世民", "长孙嘉敏");
            });
            Assertions.assertEquals("Hello 李世民,I am 长孙嘉敏", exception1.getMessage());

            BusinessException exception2 = Assertions.assertThrows(BusinessException.class, () -> {
                throw new BusinessException(100000, "你好{0},我是{1}", true, "李世民");
            });
            Assertions.assertEquals("Hello 李世民,I am {1}", exception2.getMessage());

            BusinessException exception3 = Assertions.assertThrows(BusinessException.class, () -> {
                throw new BusinessException(100000, "你好{0},我是{1}", true);
            });
            Assertions.assertEquals("Hello {0},I am {1}", exception3.getMessage());

            BusinessException exception4 = Assertions.assertThrows(BusinessException.class, () -> {
                throw new BusinessException(ApplicationStatus.ILLEGAL_ARGUMENT);
            });
            Assertions.assertEquals("IllegalArgument", exception4.getMessage());

            BusinessException exception5 = Assertions.assertThrows(BusinessException.class, () -> {
                throw new BusinessException(ApplicationStatus.ILLEGAL_ARGUMENT.getStatus(), ApplicationStatus.ILLEGAL_ARGUMENT.getMessage());
            });
            Assertions.assertEquals("IllegalArgument", exception5.getMessage());
        }
    }

    @Test
    void testRemoteInvokeException() {
        RemoteInvokeException exception = Assertions.assertThrows(RemoteInvokeException.class, () -> {
            throw new RemoteInvokeException();
        });
        Assertions.assertEquals("网络异常，请稍后再试", exception.getMessage());

        RemoteInvokeException exception1 = Assertions.assertThrows(RemoteInvokeException.class, () -> {
            throw new RemoteInvokeException(100000, "你好{0},我是{1}", true, "李世民", "长孙嘉敏");
        });
        Assertions.assertEquals("你好李世民,我是长孙嘉敏", exception1.getMessage());

        RemoteInvokeException exception2 = Assertions.assertThrows(RemoteInvokeException.class, () -> {
            throw new RemoteInvokeException(100000, "你好{0},我是{1}", true, "李世民");
        });
        Assertions.assertEquals("你好李世民,我是{1}", exception2.getMessage());

        RemoteInvokeException exception3 = Assertions.assertThrows(RemoteInvokeException.class, () -> {
            throw new RemoteInvokeException(100000, "你好{0},我是{1}", true);
        });
        Assertions.assertEquals("你好{0},我是{1}", exception3.getMessage());

        RemoteInvokeException exception4 = Assertions.assertThrows(RemoteInvokeException.class, () -> {
            throw new RemoteInvokeException(ApplicationStatus.ILLEGAL_ARGUMENT);
        });
        Assertions.assertEquals("非法参数", exception4.getMessage());

        RemoteInvokeException exception5 = Assertions.assertThrows(RemoteInvokeException.class, () -> {
            throw new RemoteInvokeException(ApplicationStatus.ILLEGAL_ARGUMENT.getStatus(), ApplicationStatus.ILLEGAL_ARGUMENT.getMessage());
        });
        Assertions.assertEquals("非法参数", exception5.getMessage());
    }

    @Test
    void testRemoteInvokeExceptionEn() {
        I18nSimpleRegistry.getEnRegistry().putAll(Map.of(
                "网络异常，请稍后再试", "Network exception, please try again later.",
                "你好{0},我是{1}", "Hello {0},I am {1}",
                "非法参数", "IllegalArgument"));
        try (MockedStatic<RequestContextHolder> mocked = mockStatic(RequestContextHolder.class)) {
            //创建模拟对象
            HttpServletRequest mockRequest = Mockito.mock(HttpServletRequest.class);
            ServletRequestAttributes mockRequestAttributes = Mockito.mock(ServletRequestAttributes.class);
            when(mockRequestAttributes.getRequest()).thenReturn(mockRequest);
            when(mockRequest.getHeader(HeaderInfo.LANGUAGE)).thenReturn(LanguageType.EN.getCode());

            //设置静态方法行为
            mocked.when(RequestContextHolder::getRequestAttributes).thenReturn(mockRequestAttributes);

            RemoteInvokeException exception = Assertions.assertThrows(RemoteInvokeException.class, () -> {
                throw new RemoteInvokeException();
            });
            Assertions.assertEquals("Network exception, please try again later.", exception.getMessage());

            RemoteInvokeException exception1 = Assertions.assertThrows(RemoteInvokeException.class, () -> {
                throw new RemoteInvokeException(100000, "你好{0},我是{1}", true, "李世民", "长孙嘉敏");
            });
            Assertions.assertEquals("Hello 李世民,I am 长孙嘉敏", exception1.getMessage());

            RemoteInvokeException exception2 = Assertions.assertThrows(RemoteInvokeException.class, () -> {
                throw new RemoteInvokeException(100000, "你好{0},我是{1}", true, "李世民");
            });
            Assertions.assertEquals("Hello 李世民,I am {1}", exception2.getMessage());

            RemoteInvokeException exception3 = Assertions.assertThrows(RemoteInvokeException.class, () -> {
                throw new RemoteInvokeException(100000, "你好{0},我是{1}", true);
            });
            Assertions.assertEquals("Hello {0},I am {1}", exception3.getMessage());

            RemoteInvokeException exception4 = Assertions.assertThrows(RemoteInvokeException.class, () -> {
                throw new RemoteInvokeException(ApplicationStatus.ILLEGAL_ARGUMENT);
            });
            Assertions.assertEquals("IllegalArgument", exception4.getMessage());

            RemoteInvokeException exception5 = Assertions.assertThrows(RemoteInvokeException.class, () -> {
                throw new RemoteInvokeException(ApplicationStatus.ILLEGAL_ARGUMENT.getStatus(), ApplicationStatus.ILLEGAL_ARGUMENT.getMessage());
            });
            Assertions.assertEquals("IllegalArgument", exception5.getMessage());
        }
    }

}

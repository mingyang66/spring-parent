package com.emily.test.aop;

import com.emily.infrastructure.aop.utils.MethodInvocationUtils;
import com.emily.test.aop.entity.MethodEntity;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * @author :  Emily
 * @since :  2024/12/3 下午9:37
 */
public class MethodInvocationUtilsTest {
    private MethodInvocation invocation;
    private Function<Object, Boolean> function;
    private BiFunction<Parameter, Object, Object> biFunction;

    @BeforeEach
    void beforeEach() {
        invocation = Mockito.mock(MethodInvocation.class);
        function = Mockito.mock(Function.class);
        biFunction = Mockito.mock(BiFunction.class);
    }

    @Test
    public void testGetMethodArgs_withNullInvocation_shouldThrowException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            MethodInvocationUtils.getMethodArgs(null, function, biFunction);
        });
    }

    @Test
    public void testGetMethodArgs_withNullSupplier_shouldThrowException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            MethodInvocationUtils.getMethodArgs(invocation, null, biFunction);
        });
    }

    @Test
    public void testGetMethodArgs_withNullConsumer_shouldThrowException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            MethodInvocationUtils.getMethodArgs(invocation, function, null);
        });
    }

    @Test
    public void testGetMethodArgs_withNoArguments_shouldReturnEmptyMap() {
        when(invocation.getArguments()).thenReturn(new Object[]{});
        Map<String, Object> params = MethodInvocationUtils.getMethodArgs(invocation, function, biFunction);
        Assertions.assertEquals(params.size(), 0);
    }

    @Test
    public void testGetMethodArgs_withArguments_shouldReturnProcessedMap() throws NoSuchMethodException {
        String arg1 = "尤五";
        String arg2 = null;
        String arg3 = "123456";
        Object[] args = {arg1, arg2, arg3};

        Parameter param1 = MethodEntity.class.getMethod("getUsername", String.class, String.class, String.class).getParameters()[0];
        Parameter param2 = MethodEntity.class.getMethod("getUsername", String.class, String.class, String.class).getParameters()[1];
        Parameter param3 = MethodEntity.class.getMethod("getUsername", String.class, String.class, String.class).getParameters()[2];
        Parameter[] params = {param1, param2, param3};
        Assertions.assertEquals(params.length, 3);

        when(invocation.getArguments()).thenReturn(args);
        when(invocation.getMethod()).thenReturn(MethodEntity.class.getMethod("getUsername", String.class, String.class, String.class));
        //when(invocation.getMethod().getParameters()).thenReturn(params);
        //when(function.apply(arg1)).thenReturn(true);
        when(function.apply(anyString())).thenReturn(false);
        when(biFunction.apply(param1, arg1)).thenReturn("尤五");
        when(biFunction.apply(param3, arg3)).thenReturn("processedArg1");

        Map<String, Object> result = MethodInvocationUtils.getMethodArgs(invocation, function, biFunction);

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("尤五", result.get(param1.getName()));
        Assertions.assertNull(result.get(param2.getName()));
        Assertions.assertEquals("processedArg1", result.get(param3.getName()));
    }

    @Test
    public void testGetMethodArgs_withArguments_allParameterNotNull() throws NoSuchMethodException {
        String arg1 = "尤五";
        String arg2 = "胡雪岩";
        String arg3 = "123456";
        Object[] args = {arg1, arg2, arg3};

        Parameter param1 = MethodEntity.class.getMethod("getUsername", String.class, String.class, String.class).getParameters()[0];
        Parameter param2 = MethodEntity.class.getMethod("getUsername", String.class, String.class, String.class).getParameters()[1];
        Parameter param3 = MethodEntity.class.getMethod("getUsername", String.class, String.class, String.class).getParameters()[2];
        Parameter[] params = {param1, param2, param3};
        Assertions.assertEquals(params.length, 3);

        when(invocation.getArguments()).thenReturn(args);
        when(invocation.getMethod()).thenReturn(MethodEntity.class.getMethod("getUsername", String.class, String.class, String.class));
        //when(invocation.getMethod().getParameters()).thenReturn(params);
        //when(function.apply(arg1)).thenReturn(true);
        when(function.apply(anyString())).thenReturn(false);
        when(biFunction.apply(param1, arg1)).thenReturn("尤五");
        when(biFunction.apply(param2, arg2)).thenReturn("王有龄");
        when(biFunction.apply(param3, arg3)).thenReturn("processedArg1");

        Map<String, Object> result = MethodInvocationUtils.getMethodArgs(invocation, function, biFunction);

        Assertions.assertEquals(3, result.size());
        Assertions.assertEquals("尤五", result.get(param1.getName()));
        Assertions.assertEquals("王有龄", result.get(param2.getName()));
        Assertions.assertEquals("processedArg1", result.get(param3.getName()));
    }
}

### springboot通过HttpServletRequestWrapper获取所有请求参数

> springboot通过拦截器获取参数有两种方式，一种通过request.getParameter获取Get方式传递的参数，另外一种是通过request.getInputStream或reques.getReader获取通过POST/PUT/DELETE/PATCH传递的参数；

##### 1.拦截器获取参数有哪些方式

- @PathVariable注解是REST风格url获取参数的方式，只能用在GET请求类型，通过getParameter获取参数
- @RequestParam注解支持GET和POST/PUT/DELETE/PATCH方式，Get方式通过getParameter获取参数和post方式通过getInputStream或getReader获取参数
- @RequestBody注解支持POST/PUT/DELETE/PATCH，可以通过getInputStream和getReader获取参数
- HttpServletRequest参数可以通过getParameter和getInputStream或getReader获取参数

上述通过getInputStream或getReader在拦截器中获取会导致控制器拿到的参数为空，这是因为流读取一次之后流的标志位已经发生了变化，无法多次读取参数；

##### 2.通过HttpServletRequestWrapper包装类每次读取参数后再回写参数

```java
package com.sgrain.boot.common.servlet;

import com.sgrain.boot.common.utils.io.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * @Description: 对HttpServletRequest进行重写，
 * 1、用来接收application/json参数数据类型，即@RequestBody注解标注的参数,解决多次读取问题
 * 2、用来解决注解@RequestParam通过POST/PUT/DELETE/PATCH方法传递参数，解决多次读取问题
 * 首先看一下springboot控制器三个注解：
 * 1、@PathVariable注解是REST风格url获取参数的方式，只能用在GET请求类型，通过getParameter获取参数
 * 2、@RequestParam注解支持GET和POST/PUT/DELETE/PATCH方式，Get方式通过getParameter获取参数和post方式通过getInputStream或getReader获取参数
 * 3、@RequestBody注解支持POST/PUT/DELETE/PATCH，可以通过getInputStream和getReader获取参数
 * @create: 2020/8/19
 */
public class RequestWrapper extends HttpServletRequestWrapper {
    //参数字节数组
    private byte[] requestBody;
    //Http请求对象
    private HttpServletRequest request;

    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.request = request;
    }

    /**
     * @return
     * @throws IOException
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        /**
         * 每次调用此方法时将数据流中的数据读取出来，然后再回填到InputStream之中
         * 解决通过@RequestBody和@RequestParam（POST方式）读取一次后控制器拿不到参数问题
         */
        if (null == this.requestBody) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IOUtils.copy(request.getInputStream(), baos);
            this.requestBody = baos.toByteArray();
        }

        final ByteArrayInputStream bais = new ByteArrayInputStream(requestBody);
        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {

            }

            @Override
            public int read() {
                return bais.read();
            }
        };
    }

    public byte[] getRequestBody() {
        return requestBody;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
}
```

##### 3.回写参数的包装类写好之后接下来就是加入过滤器链之中，如下：

```java
package com.sgrain.boot.web.filter;

import com.sgrain.boot.common.servlet.RequestWrapper;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
* @Description: 拦截所有请求过滤器，并将请求类型是HttpServletRequest类型的请求替换为自定义{@link com.sgrain.boot.common.servlet.RequestWrapper}
* @create: 2020/8/19
*/
@Component
@WebFilter(filterName = "channelFilter", urlPatterns = {"/*"})
public class ChannelFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            ServletRequest requestWrapper = null;
            if (request instanceof HttpServletRequest) {
                requestWrapper = new RequestWrapper((HttpServletRequest) request);
            }
            if (requestWrapper == null) {
                chain.doFilter(request, response);
            } else {
                chain.doFilter(requestWrapper, response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void destroy() {
    }

}

```

这样每个请求都会经过上面的过滤器，会将每个HttpServletRequest请求转换为包装类RequestWrapper,以后在请求到达控制器资源之前就可以拿到我们想要的参数；

##### 4.在AOP拦截器中获取参数工具方法（各种请求参数都可以拿到）如下：

```java
    /**
     * 获取请求入参
     *
     * @param request
     * @return
     */
    public static Map<String, Object> getParameterMap(HttpServletRequest request) {
        Map<String, Object> paramMap = new LinkedHashMap<>();
        if(request instanceof RequestWrapper){
            RequestWrapper requestWrapper = (RequestWrapper) request;
            Map<String, Object> body = getParameterMap(requestWrapper.getRequestBody());
            if (!CollectionUtils.isEmpty(body)) {
                paramMap.putAll(body);
            }
        }
        Enumeration<String> names = request.getParameterNames();
        while (names.hasMoreElements()) {
            String key = names.nextElement();
            paramMap.put(key, request.getParameter(key));
        }

        return paramMap;
    }

    /**
     * 获取参数对象
     *
     * @param params
     * @return
     */
    public static Map<String, Object> getParameterMap(byte[] params) {
        try {
            return JSONUtils.toObject(params, Map.class);
        } catch (Exception e) {
            return convertParameterToMap(IOUtils.toString(params, CharsetUtils.UTF_8));
        }
    }


    /**
     * 将参数转换为Map类型
     *
     * @param param
     * @return
     */
    public static Map<String, Object> convertParameterToMap(String param) {
        if (StringUtils.isEmpty(param)) {
            return Collections.emptyMap();
        }
        Map<String, Object> pMap = Maps.newLinkedHashMap();
        String[] pArray = StringUtils.split(param, CharacterUtils.AND_AIGN);
        for (int i = 0; i < pArray.length; i++) {
            String[] array = StringUtils.split(pArray[i], CharacterUtils.EQUAL_SIGN);
            if (array.length == 2) {
                pMap.put(array[0], array[1]);
            }
        }
        return pMap;
    }
```

GitHub源码：[https://github.com/mingyang66/spring-parent/tree/master/sgrain-spring-boot-common](https://github.com/mingyang66/spring-parent/tree/master/sgrain-spring-boot-common)
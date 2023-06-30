#### 解锁新技能《springboot全局异常处理@ExceptionHandler如何获取控制器方法上的注解》

> springboot全局异常处理通常都是用@RestControllerAdvice标注异常处理类，使用@ExceptionHandler标注在捕获具体异常的方法上，我们可以通过异常参数获取异常信息，那如果我想获取抛出异常的具体控制器及其上面标注的注解如何获取呢？

##### 一、@ExceptionHandler标记的全局异常处理方法可以接受如下参数

- HttpServletRequest类型参数，用于获取请求头信息和请求参数信息；
- HttpServletResponse类型参数，用于设置响应状态码和响应头信息；
- Exception类型参数，用于获取异常对象；
- WebRequest类型参数，用于获取请求信息；
- Model类型参数，用于天剑异常信息到Model，方便前段页面显示；
- HandlerMethod类型参数，可以让我们在异常处理方法中获取处理异常的方法信息，包括方法名、方法所在类的名称、方法的参数列表等。

##### 二、通过参数HandlerMethod判定是否标注指定注解案例：

```java
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = Exception.class)
    public Object exceptionHandler(Exception e, HttpServletRequest request, HandlerMethod handlerMethod) {
        // 获取控制器对象
        Object controller = handlerMethod.getBean();
        // 获取控制器方法
        Method method = handlerMethod.getMethod();
        if (method.isAnnotationPresent(ApiResponseWrapperIgnore.class)) {
            return e.getMessage();
        }
        return "xxx";
    }
```

> 可以在异常处理方法中天剑HandlerMethod参数，通过此方法可以获取控制器对象，控制器方法对象，可以通过方法对象判定控制器方法标记了哪些注解；

三、
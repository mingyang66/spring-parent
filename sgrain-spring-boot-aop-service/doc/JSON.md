### JSON工具类

#### 1.支持对象转字符串-toJSONString
```
    /**
     * @Description 对象转换为json字符串,支持List、Map、Collection、字符串
     * @Version  1.0
     */
    public static <T> String toJSONString(T o){
       return toJSONString(o, Include.ALWAYS);
    }
```
#### 2.支持对象转字符串多种处理模式-toJSONString
```
    /**
     * @Description 对象转换为json字符串,支持List、Map、Collection、字符串
     * @param include 定义javaBean的那些属性需要序列化
     *              ALWAYS：始终包含javaBean的值，与属性的值无关。
     *              NON_NULL：表示只包含非null的属性值。
     *              NON_ABSENT：表示属性值为null,或者JAVA8、Guava中的Optional
     *              NON_EMPTY：表示非null、""和数组集合isEmpty()=false都将会被忽略
     *              NON_DEFAULT：表示POJO类属性的值为缺省值是不序列化，如User类的 int age = 0; String username = null;
     *              CUSTOM:自定义，根据过滤器等
     *              USE_DEFAULTS：...
     * @Version  1.0
     */
    public static <T> String toJSONString(T o, Include include){
        try{
            if(null == include){
                include = Include.ALWAYS;
            }
            objectMapper.setSerializationInclusion(include);
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e){
            e.printStackTrace();
            LoggerUtil.error(JSONUtils.class, e.toString());
            return null;
        }
    }
```
#### 3.支持对象转字符串格式化输出-toJSONPrettyString
```
    /**
     * @Description 带格式化， 对象转换为json字符串,支持List、Map、Collection、字符串
     * @Version  1.0
     */
    public static <T> String toJSONPrettyString(T o){
       return toJSONPrettyString(o, Include.ALWAYS);
    }
```
#### 4.支持对象转字符串格式化输出-toJSONPrettyString
```
   /**
     * @Description 带格式化， 对象转换为json字符串,支持List、Map、Collection、字符串
     * @param include 定义javaBean的那些属性需要序列化
     *              ALWAYS：始终包含javaBean的值，与属性的值无关。
     *              NON_NULL：表示只包含非null的属性值。
     *              NON_ABSENT：表示属性值为null,或者JAVA8、Guava中的Optional
     *              NON_EMPTY：表示非null、""和数组集合isEmpty()=false都将会被忽略
     *              NON_DEFAULT：表示POJO类属性的值为缺省值是不序列化，如User类的 int age = 0; String username = null;
     *              CUSTOM:自定义，根据过滤器等
     *              USE_DEFAULTS：...
     * @Version  1.0
     */
    public static <T> String toJSONPrettyString(T o, Include include){
        try{
            if(null == include){
                include = Include.ALWAYS;
            }
            objectMapper.setSerializationInclusion(include);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
        } catch (JsonProcessingException e){
            e.printStackTrace();
            LoggerUtil.error(JSONUtils.class, e.toString());
            return null;
        }
    }
```
#### 5.JSON字符串转JAVA对象
```
   /**
     * @Description JSON字符串转换为java对象,支持List、Map、Collection、字符串
     * @Version  1.0
     */
    public static <T> T toJavaBean(String str, Class<T> responseType){
        try {
            return objectMapper.readValue(str, responseType);
        } catch (JsonParseException e){
            e.printStackTrace();
            LoggerUtil.error(JSONUtils.class, e.toString());
            return null;
        } catch (JsonMappingException e){
            e.printStackTrace();
            LoggerUtil.error(JSONUtils.class, e.toString());
            return null;
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(JSONUtils.class, e.toString());
            return null;
        }
    }
```
#### 6.从文件中读取json字符串转换为java对象
```
   /**
     * @Description JSON字符串转换为java对象,支持List、Map、Collection、字符串
     * @Version  1.0
     */
    public static <T> T toJavaBean(File file, Class<T> responseType){
        try {
            return objectMapper.readValue(file, responseType);
        } catch (JsonMappingException e){
            e.printStackTrace();
            LoggerUtil.error(JSONUtils.class, e.toString());
            return null;
        } catch (JsonParseException e){
            e.printStackTrace();
            LoggerUtil.error(JSONUtils.class, e.toString());
            return null;
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(JSONUtils.class, e.toString());
            return null;
        }
    }
```
#### 7.将对象写入到指定的文件中
```
   /**
     * @Description 将对象写入文件
     * @Version  1.0
     */
    public static boolean writeToFile(File file, Object o){
        try{
            objectMapper.writeValue(file, o);
            return true;
        } catch (JsonMappingException e){
            e.printStackTrace();
            LoggerUtil.error(JSONUtils.class, e.toString());
        } catch (JsonGenerationException e){
            e.printStackTrace();
            LoggerUtil.error(JSONUtils.class, e.toString());
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(JSONUtils.class, e.toString());
        }
        return false;
    }
```

#### 8.将对象格式化写入到指定文件中
```
   /**
     * @Description 格式化，将对象写入文件
     * @Version  1.0
     */
    public static boolean writeToFilePretty(File file, Object o){
        try{
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, o);
            return true;
        } catch (JsonMappingException e){
            e.printStackTrace();
            LoggerUtil.error(JSONUtils.class, e.toString());
        } catch (JsonGenerationException e){
            e.printStackTrace();
            LoggerUtil.error(JSONUtils.class, e.toString());
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(JSONUtils.class, e.toString());
        }
        return false;
    }
```
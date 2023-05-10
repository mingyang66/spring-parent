### jackson将JSON字符串转换成复杂的数据类型

```
    private static ObjectMapper objectMapper = new ObjectMapper();
   /**
     * 示例1：
     *         List<Map<Integer, String>> list = Lists.newArrayList();
     *         Map<Integer, String> map = Maps.newHashMap();
     *         map.put(12, "gg");
     *         map.put(34, "sd");
     *         list.add(map);
     *         List<Map<Integer, String>> list1 = toJavaBean(JSONUtils.toJSONString(list), ArrayList.class, HashMap.class);
     * 示例2：
     *         Map<Integer, String> map = Maps.newHashMap();
     *         map.put(12, "gg");
     *         map.put(34, "sd");
     *         Map<Integer, String> map1 = toJavaBean(JSONUtils.toJSONString(map), HashMap.class, Integer.class, String.class);
     * @param jsonString JSON字符串
     * @param parametrized 数据类型最外层class或者泛型实际的class, 如List<Map<String, Integer>>的List.class 或者Map<String, Integer>中的Map.class
     * @param parameterClasses 参数内部类型，如List<Map<String, Object>中的Map.class 或者Map<String, Integer>中的String.class、Integer.class
     * @param <T>
     * @return
     */
    public static <T> T toJavaBean(String jsonString, Class<?> parametrized, Class<?>... parameterClasses){
        try{
            JavaType javaType = javaType(parametrized, parameterClasses);
            return objectMapper.readValue(jsonString, javaType);
        } catch (JsonParseException e){
            e.printStackTrace();
            LoggerUtil.error(JSONUtils.class, e.toString());
        } catch (JsonMappingException e){
            e.printStackTrace();
            LoggerUtil.error(JSONUtils.class, e.toString());
        } catch (IOException e){
            e.printStackTrace();
            LoggerUtil.error(JSONUtils.class, e.toString());
        }
        return null;
    }
    /**
     *  示例1：
     *  Map<Integer, String> map = Maps.newHashMap();
     *  JavaType javaType = javaType(Map.class, Integer.class, String.class);
     *  示例2：
     *   List<Map<Integer, String>> list = Lists.newArrayList();
     *   Map<Integer, String> map = Maps.newHashMap();
     *   map.put(12, "gg");
     *   map.put(34, "sd");
     *   list.add(map);
     *   JavaType javaType = javaType(List.class, Map.class);
     * @param parametrized 实际的数据类型，即最外层数据类型List
     * @param parameterClasses 内部参数类型，即Set.class Bean.class
     * @return
     */
    public static JavaType javaType(Class<?> parametrized, Class<?>... parameterClasses){
        return objectMapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
    }
```    

上面只列出了工具包的部分代码，其它的可以参考我的源码；
GitHub源码地址：[https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-common-service/src/main/java/com/yaomy/control/common/control/utils/JSONUtils.java](https://github.com/mingyang66/spring-parent/blob/master/spring-boot-control-common-service/src/main/java/com/yaomy/control/common/control/utils/JSONUtils.java)
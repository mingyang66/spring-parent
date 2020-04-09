package com.sgrain.boot.common.utils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.sgrain.boot.common.po.BaseRequest;
import com.sgrain.boot.common.utils.json.JSONUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * @Description: 读取需要隐藏手机号的配置
 * @Date: 2019/11/13 13:39
 * @Version: 1.0
 */
public class HiddenUtils {
    /**
     * 隐藏字段规则
     */
    private static Map<String, Multimap<Integer, String>> map = Maps.newHashMap();
    /**
     * 隐藏字段规则分割后的长度
     */
    private static final int LEN = 3;
    /**
     * 分隔符
     */
    private static final String SEPARATOR_CHARS = "|";
    /**
     * 读取隐藏字段规则
     */
    public static Map<String, Multimap<Integer, String>> getHiddenRule(){
        return map;
    }

    /**
     * 新增需要隐藏的字段规则，路由|类型|字段名
     */
    public static void addHiddenRule(String hidden){
        if(StringUtils.isEmpty(hidden)){
            return;
        }
        String[] hiddenStr = StringUtils.split(hidden, SEPARATOR_CHARS);
        if(hiddenStr.length < LEN){
            return;
        }
        if(map.containsKey(hiddenStr[0])){
            map.get(hiddenStr[0]).put(NumberUtils.toInt(hiddenStr[1]), hiddenStr[2]);
        } else {
            Multimap<Integer, String> mapType = ArrayListMultimap.create();
            mapType.put(NumberUtils.toInt(hiddenStr[1]), hiddenStr[2]);
            map.put(hiddenStr[0], mapType);
        }
    }

    /**
     * 新增多个需要隐藏的字段规则，路由|类型|字段名
     */
    public static void addHiddenRule(String...hidden){
        if(ObjectUtils.isEmpty(hidden)){
            return;
        }
        for (int i=0; i<hidden.length;i++){
            addHiddenRule(hidden[i]);
        }
    }

    /**
     * 隐藏参数字段
     * @param paramMap  参数字段
     * @param route 路由
     * @return
     */
    public static Map<String, Object> hidden(Map<String, Object> paramMap, String route){
        if(ObjectUtils.isEmpty(paramMap)){
            return paramMap;
        }
        if(!map.containsKey(route)){
            return paramMap;
        }
        Multimap<Integer, String> routeMap = map.get(route);
        Map<String, Object> pMap = Maps.newHashMap();
        pMap.putAll(paramMap);
        pMap.forEach((key, value)->{
            if(ObjectUtils.isEmpty(value)){
                return;
            }
            if(value instanceof BaseRequest){
                Map<String, Object> objectMap = JSONUtils.toJavaBean(JSONUtils.toJSONString(value), Map.class);
                routeMap.keySet().forEach((type)->{
                    routeMap.get(type).forEach((fieldName)->{
                        if(objectMap.containsKey(fieldName) && objectMap.get(fieldName) != null){
                            if(HiddenType.PHONE.getType() == NumberUtils.toInt(type.toString())){
                                objectMap.put(fieldName, PhoneUtils.hidden(objectMap.get(fieldName).toString()));
                            }
                        }
                    });
                });
                pMap.put(key, objectMap);
            } else {
                routeMap.keySet().forEach((type)->{
                    routeMap.get(type).forEach((fieldName)->{
                        if(StringUtils.equals(fieldName, key) && !ObjectUtils.isEmpty(value)){
                            if(HiddenType.PHONE.getType() == NumberUtils.toInt(type.toString())){
                                pMap.put(fieldName, PhoneUtils.hidden(value.toString()));
                            }
                        }
                    });
                });
            }

        });

        return pMap;
    }
    /**
     * 隐藏字段类型枚举类
     */
    public enum HiddenType{
        PHONE(1, "手机号");

        private Integer type;
        private String desc;
        HiddenType(Integer type, String desc){
           this.type = type;
           this.desc = desc;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }
    }
}

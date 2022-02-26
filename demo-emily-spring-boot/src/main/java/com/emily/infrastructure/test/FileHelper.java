package com.emily.infrastructure.test;

import com.emily.infrastructure.common.utils.json.JSONUtils;

import java.net.URL;
import java.util.Arrays;


/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/05/17
 */
public class FileHelper {

    public static void main(String[] args) {
        String[] con = new String[]{"1", "2", "3", "4", "5"};
        System.out.println(JSONUtils.toJSONString(con));
        System.arraycopy(con, 2, con, 0, 3);
        System.out.println(JSONUtils.toJSONString(con));
        Arrays.fill(con, 3, 5, null);
        System.out.println(JSONUtils.toJSONString(con));
        con = Arrays.copyOf(con, 8);
        System.out.println(JSONUtils.toJSONString(con));
        con = Arrays.copyOf(con, 3);
        System.out.println(JSONUtils.toJSONString(con));

    }
}

package com.emily.framework.datasource;

/**
 * @program: spring-parent
 * @description:
 * @author: 姚明洋
 * @create: 2021/05/17
 */
public class Test {
    public static void main(String[] args) {
       try {
           String s = null;
           s.length();
       } catch (Exception e){
           throw e;
       } finally {
           System.out.println("finally");
       }
    }
}

package com.emily.infrastructure.test.security.entity;

import com.emily.infrastructure.security.annotation.SecurityModel;

import java.util.List;

/**
 * @author :  Emily
 * @since :  2025/2/8 下午5:42
 */
@SecurityModel
public class UserColl {
    private List<Address> list;
    private List<String> listStr;
    private List<Integer> listInt;

    public List<String> getListStr() {
        return listStr;
    }

    public void setListStr(List<String> listStr) {
        this.listStr = listStr;
    }

    public List<Integer> getListInt() {
        return listInt;
    }

    public void setListInt(List<Integer> listInt) {
        this.listInt = listInt;
    }

    public List<Address> getList() {
        return list;
    }

    public void setList(List<Address> list) {
        this.list = list;
    }
}

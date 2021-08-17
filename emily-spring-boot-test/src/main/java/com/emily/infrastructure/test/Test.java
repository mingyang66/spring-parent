package com.emily.infrastructure.test;

import com.emily.infrastructure.common.utils.bean.BeanUtils;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.test.po.Job;
import com.emily.infrastructure.test.po.User;

/**
 * @program: spring-parent
 * @description:
 * @author: Emily
 * @create: 2021/05/17
 */
public class Test {
    public static void main(String[] args) {
        Job job = new Job();
        job.setA("2332");
        job.setId(23L);
        job.setJobNumber(34L);
        job.setJobDesc("wererw");


        User user = new User();
        user.setUsername("asdf");
        user.setPassword("23");
        user.setJob(job);

        //User user1 = new User();
        //BeanUtils.copyProperties(user, user1);
        User user1 = BeanUtils.deepCopy(user);
        System.out.println(JSONUtils.toJSONPrettyString(user1));
        user1.getJob().setJobDesc("描述");
        user1.setPassword("密码");
        System.out.println(JSONUtils.toJSONPrettyString(user1));
        System.out.println(JSONUtils.toJSONPrettyString(user));


    }

}

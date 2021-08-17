package com.emily.infrastructure.test;

import com.emily.infrastructure.common.enums.AppHttpStatus;
import com.emily.infrastructure.common.exception.BusinessException;
import com.emily.infrastructure.common.utils.json.JSONUtils;
import com.emily.infrastructure.test.po.Job;
import com.emily.infrastructure.test.po.User;
import org.springframework.beans.BeanUtils;

import java.io.*;

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
        User user1 = deepCopy(user);
        System.out.println(JSONUtils.toJSONPrettyString(user1));
        user1.getJob().setJobDesc("描述");
        user1.setPassword("密码");
        System.out.println(JSONUtils.toJSONPrettyString(user1));
        System.out.println(JSONUtils.toJSONPrettyString(user));


    }

    /**
     * 深度拷贝
     *
     * @param obj 原始对象
     * @param <T> 对象类型
     * @return
     */
    public static <T> T deepCopy(T obj) {
        try {
            // 序列化
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            oos.writeObject(obj);

            //反序列化
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);

            return (T) ois.readObject();
        } catch (NotSerializableException exception) {
            throw new BusinessException(AppHttpStatus.EXCEPTION.getStatus(), "未实现序列化接口");
        } catch (Exception exception) {
            throw new BusinessException(AppHttpStatus.EXCEPTION.getStatus(), "深度拷贝数据异常");
        }
    }
}

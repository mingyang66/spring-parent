package com.emily.infrastructure.test;

import com.emily.infrastructure.json.JsonUtils;
import com.emily.infrastructure.sensitive.SensitiveUtils;
import com.emily.infrastructure.test.po.sensitive.UploadRequest;
import org.junit.Test;

/**
 *  脱敏单元测试类
 * @author  Emily
 * @since  Created in 2023/5/20 9:17 AM
 */
public class SensitiveUtilsTest {
    @Test
    public void test(){
        UploadRequest request = new UploadRequest();
        request.setAge(12);
        request.setAgeW(13);
        request.setFileName("test");
        Object s = SensitiveUtils.acquireElseGet(request);
        System.out.println(JsonUtils.toJSONPrettyString(s));
    }
}

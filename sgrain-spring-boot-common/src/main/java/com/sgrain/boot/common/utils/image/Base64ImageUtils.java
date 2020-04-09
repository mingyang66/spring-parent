package com.sgrain.boot.common.utils.image;

import org.apache.commons.lang3.ArrayUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;


/**
 * @Description: 图片Base64相互转换
 * @Date: 2019/12/9 15:29
 * @Version: 1.0
 */
public class Base64ImageUtils {
    /**
     * 将图片转换成Base64编码
     * @param imgFile 待处理图片
     * @return
     */
    public static String imageToBase64(String imgFile) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        try(InputStream in = new FileInputStream(imgFile)){
            data = new byte[in.available()];
            in.read(data);
        } catch (IOException e){
            e.printStackTrace();
        }
        return new String(Base64.getEncoder().encode(data));
    }

    /**
     * 对字节数组字符串进行Base64解码并生成图片
     * @param imgStr 图片数据
     * @return
     */
    public static byte[] base64ToImage(String imgStr) {

        try {
            // Base64解码
            byte[] b = Base64.getDecoder().decode(imgStr);
            for (int i = 0; i < b.length; ++i) {
                // 调整异常数据
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            return b;
        } catch (Exception e) {
            e.printStackTrace();
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
    }
}

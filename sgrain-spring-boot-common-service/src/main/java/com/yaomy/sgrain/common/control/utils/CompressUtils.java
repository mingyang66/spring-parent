package com.yaomy.sgrain.common.control.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.InflaterInputStream;

/**
 * @Description: 压缩解压缩工具类
 * @Version: 1.0
 */
public class CompressUtils {
    /**
     * 解压缩数据
     * @param data 字节数组
     */
    public static byte[] decompress(byte[] data) {
        byte[] decompressData = new byte[data.length];
        /**
         * ByteArrayInputStream包含一个内部缓冲区，其中存储从流中读取的字节；
         * 包含一个内部技术器用于跟踪read方法读取的下一个字节；
         * 关闭ByteArrayInputStream是无效的，该类中的方法可以在流关闭以后调用，而不会生成IOException异常
         */
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        /**
         * InflaterInputStream类实现了一个过滤器，用于以“deflate”压缩格式解压缩数据
         * 它还用做其它解压缩过滤器的基础，如：GZIPInputStream
         */
        InflaterInputStream decompress = new InflaterInputStream(inputStream);
        /**
         * 这个类实现一个输出流，其中数据被写入字节数组。当数据写入缓冲区时，缓冲区会自动增长。
         * 关闭ByteArrayOutputStream是无效的，该类中的方法可以在流关闭以后调用，而不会生成IOException异常
         */
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        try {
            while (true) {
                int len = decompress.read(decompressData, 0, decompressData.length);
                if (len <= 0) {
                    break;
                }
                outputStream.write(decompressData, 0, len);
            }
            outputStream.flush();
            /**
             * 穿件新分配的字节流数组，它的大小是ByteArrayOutputStream输出流的大小
             */
           return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                decompress.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;

    }
}

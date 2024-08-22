package com.emily.infrastructure.image;

import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;

/**
 * @author :  姚明洋
 * @since :  2024/8/20 下午7:12
 */
public class ImageTest {
    public static void main(String[] args) {
        try {
            // 指定源图片和输出缩略图的路径
            File sourceFile = new File("/Users/yaomingyang/Documents/1.jpg");
            File destFile = new File("/Users/yaomingyang/Documents/2.jpg");

            // 使用Thumbnails类的of方法加载图片，然后使用size方法指定缩略图的大小
            // 最后使用toFile方法保存缩略图
            Thumbnails.of(sourceFile)
                    .size(160, 160) // 设定缩略图大小为160x160
                    .toFile(destFile);

            System.out.println("缩略图生成成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package com.emily.cloud.test.api.po;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Random;

/**
 * @description: Description
 * @Author Emily
 * @Date: 2020/6/29 11:33
 * @Version: 1.0
 */
public class ValidateCodeUtils {
    /**
     * 验证码字符集
     */
    private static final char[] CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
            'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    /**
     * 验证码字符集
     */
    private static final char[] NUMBERCHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    /**
     * 干扰线数量
     */
    private static final int LINES = 5;
    /**
     * 宽度
     */
    private static final int WIDTH = 80;
    /**
     * 高度
     */
    private static final int HEIGHT = 40;
    /**
     * 字体大小
     */
    private static final int FONT_SIZE = 25;

    /**
     * 生成随机验证码及图片
     *
     * @param size 长度
     * @return Map<字符, 文件流>
     */
    public static Map.Entry<String, Object> createImage(int size) {
        StringBuffer sb = new StringBuffer();
        // 1.创建空白图片
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        // 2.获取图片画笔
        Graphics graphic = image.getGraphics();
        // 3.设置画笔颜色
        graphic.setColor(new Color(255, 255, 255));
        // 4.绘制矩形背景
        graphic.fillRect(0, 0, WIDTH, HEIGHT);
        // 5.画随机字符
        Random ran = new Random();
        for (int i = 0; i < size; i++) {
            // 取随机字符索引
            int n = ran.nextInt(CHARS.length);
            // 设置随机颜色
            graphic.setColor(getRandomColor(100, 200));
            // 设置字体大小
            graphic.setFont(new Font("Times New Roman", Font.BOLD, FONT_SIZE));
            // 画字符
            graphic.drawString(
                    CHARS[n] + "", i * WIDTH / size, HEIGHT * 2 / 3);
            // 记录字符
            sb.append(CHARS[n]);
        }
        // 6.画干扰线
        for (int i = 0; i < LINES; i++) {
            // 设置随机颜色
            graphic.setColor(getRandomColor(0, 50));
            // 随机画线
            graphic.drawLine(ran.nextInt(WIDTH), ran.nextInt(HEIGHT),
                    ran.nextInt(WIDTH), ran.nextInt(HEIGHT));
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpeg", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new AbstractMap.SimpleEntry<>(sb.toString(), baos.toByteArray());

    }

    /**
     * 随机取色
     */
    public static Color getRandomColor(int fc, int bc) {
        Random random = new Random();
        fc = Math.min(fc, 255);
        bc = Math.min(bc, 255);
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    public static String getRandomNumber(int size) {
        StringBuilder sb = new StringBuilder();
        Random ran = new Random();
        for (int i = 0; i < size; i++) {
            // 取随机字符索引
            int n = ran.nextInt(NUMBERCHARS.length);
            // 记录字符
            sb.append(NUMBERCHARS[n]);
        }
        return sb.toString();
    }
}

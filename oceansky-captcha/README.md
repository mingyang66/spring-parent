#### 开源pom依赖引用

```xml
<dependency>
  <groupId>io.github.mingyang66</groupId>
  <artifactId>oceansky-captcha</artifactId>
  <version>4.3.2</version>
</dependency>
```



#### 解锁新技能《Java绘制2D图形验证码方法》

##### 一、常见的图形验证码有如下三种：

- 字母数字混合（干扰线）；
- 纯数字（干扰线）；
- 纯字母（干扰线）；

##### 二、示例图如下：

![在这里插入图片描述](https://img-blog.csdnimg.cn/3ecd6b043020491285f454c3153771c3.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/dc548a6df21941dd8f195d9952f961f5.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/adebbf4bf4514056acf10902b95f0818.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/5279ae1474fd40248d6eff36507aa96d.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/0fcbfc81417847a892a06dd1a8d03b19.png)

![在这里插入图片描述](https://img-blog.csdnimg.cn/1bb824ce409c45aa9caddc232b8347a6.png)

##### 三、案例代码

问题一：绘制验证码的(x,y)坐标相对的点是哪里？

答案：左上角

问题二：绘制图形验证码的x坐标起始位置计算方法？

答案：(画板宽度-验证码字符文本宽度）/2

问题三：如何逐个计算每个字符绘制的x坐标位置？

答案：起始x坐标+(验证码宽度/验证码字符个数)*第n-1个字符

问题四：如何计算y轴坐标确保验证码上下居中？

答案：(height - (fm.getAscent() + fm.getDescent())) / 2 + fm.getAscent();

理解上述公式之前大家应该先弄清楚什么是基线(baseline)、升度(ascent)、降度(descent)、行间距（lead）

```java
public class CaptchaUtils {
    /**
     * 数字
     */
    private static final String[] DIGIT = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
    /**
     * 字母
     */
    private static final String[] LETTER = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
            "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    /**
     * 字母数字
     */
    private static final String[] ALPHANUMERIC = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n",
            "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    /**
     * 图片格式
     */
    public static final String FORMAT_NAME = "JPEG";

    /**
     * @param width    画板宽度
     * @param height   画板高度
     * @param count    验证码个数
     * @param fontSize 字体大小
     * @return
     * @Description: 绘制数字图形验证码，不带干扰线
     */
    public static Captcha createDigit(int width, int height, int count, int fontSize) throws IOException {
        return createDigit(width, height, count, fontSize, false, null);
    }

    /**
     * @param width     画板宽度
     * @param height    画板高度
     * @param count     验证码个数
     * @param fontSize  字体大小
     * @param line      是否画干扰线
     * @param lineCount 干扰线数量
     * @return
     * @Description: 绘制数字图形验证码
     */
    public static Captcha createDigit(int width, int height, int count, int fontSize, boolean line, Integer lineCount) throws IOException {
        /**
         * 字体名又分为两大类：中文字体名：宋体，楷体，黑体等；
         * 　　　　　　　　　　英文字体名：Arial,Times New Roman等；
         */
        Font font = new Font("Times New Roman", Font.ITALIC, fontSize);
        // 绘制的字符串
        String[] code = getDrawCode(CaptchaType.DIGIT, count);
        return create2D(width, height, code, font, line, lineCount);
    }

    /**
     * @param width    画板宽度
     * @param height   画板高度
     * @param count    验证码个数
     * @param fontSize 字体大小
     * @return
     * @throws IOException
     * @Description: 绘制字母图形验证码
     */
    public static Captcha createLetter(int width, int height, int count, int fontSize) throws IOException {
        return createLetter(width, height, count, fontSize, false, null);
    }

    /**
     * @param width     画板宽度
     * @param height    画板高度
     * @param count     验证码个数
     * @param fontSize  字体大小
     * @param line      是否画干扰线
     * @param lineCount 干扰线数量
     * @return
     * @throws IOException
     * @Description: 绘制字母图形验证码
     */
    public static Captcha createLetter(int width, int height, int count, int fontSize, boolean line, Integer lineCount) throws IOException {
        /**
         * 字体名又分为两大类：中文字体名：宋体，楷体，黑体等；
         * 　　　　　　　　　　英文字体名：Arial,Times New Roman等；
         */
        Font font = new Font("Times New Roman", Font.ITALIC, fontSize);
        // 绘制的字符串
        String[] code = getDrawCode(CaptchaType.LETTER, count);
        return create2D(width, height, code, font, line, lineCount);
    }

    /**
     * @param width    画板宽度
     * @param height   画板高度
     * @param count    验证码个数
     * @param fontSize 字体大小
     * @return
     * @throws IOException
     * @Description: 绘制数字字母图形验证码
     */
    public static Captcha createAlphanumeric(int width, int height, int count, int fontSize) throws IOException {
        return createAlphanumeric(width, height, count, fontSize, false, null);
    }

    /**
     * @param width     画板宽度
     * @param height    画板高度
     * @param count     验证码个数
     * @param fontSize  字体大小
     * @param line      是否画干扰线
     * @param lineCount 干扰线数量
     * @return
     * @throws IOException
     * @Description: 绘制数字字母图形验证码
     */
    public static Captcha createAlphanumeric(int width, int height, int count, int fontSize, boolean line, Integer lineCount) throws IOException {
        /**
         * 字体名又分为两大类：中文字体名：宋体，楷体，黑体等；
         * 　　　　　　　　　　英文字体名：Arial,Times New Roman等；
         */
        Font font = new Font("Times New Roman", Font.ITALIC, fontSize);
        // 绘制的字符串
        String[] code = getDrawCode(CaptchaType.ALPHANUMERIC, count);
        return create2D(width, height, code, font, line, lineCount);
    }

    /**
     * @param width     画板宽度
     * @param height    画板高度
     * @param code      验证码
     * @param font      字体
     * @param line      是否绘制干扰线
     * @param lineCount 干扰线条数
     * @return 生成的图形验证码对象
     * @throws IOException
     * @Description: 生成图形验证码
     */
    public static Captcha create2D(int width, int height, String[] code, Font font, boolean line, Integer lineCount) throws IOException {
        // 创建空白图片
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        // 获取图片画笔
        Graphics graphic = image.getGraphics();
        // 绘制矩形背景(实心矩形)
        graphic.fillRect(0, 0, width, height);
        /**
         * Font.PLAIN（普通）
         * Font.BOLD（加粗）
         * Font.ITALIC（斜体）
         * Font.BOLD+ Font.ITALIC（粗斜体）
         */
        graphic.setFont(font);
        // 画图形验证码
        drawCode(graphic, width, height, code);
        // 画干扰线
        if (line) {
            drawLine(graphic, width, height, lineCount);
        }
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        ImageIO.write(image, FORMAT_NAME, bas);
        return new CaptchaBuilder().code(StringUtils.join(code)).image(bas.toByteArray()).build();

    }

    /**
     * @param graphic 画板对象
     * @param width   画板宽度
     * @param height  画板高度
     * @param code    图形验证码字符串
     * @Description 画图形验证码
     */
    protected static void drawCode(Graphics graphic, int width, int height, String[] code) {
        // 计算文字长度，计算居中的x点坐标，即字符串左边位置
        FontMetrics fm = graphic.getFontMetrics(graphic.getFont());
        // 字符串宽度
        int textWidth = fm.stringWidth(StringUtils.join(code));
        // 字符串左侧x坐标
        int x = (width - textWidth) / 2;
        int y = (height - (fm.getAscent() + fm.getDescent())) / 2 + fm.getAscent();
        for (int i = 0; i < code.length; i++) {
            // 设置随机颜色
            graphic.setColor(new Color(RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255)));
            // 绘制字符串
            graphic.drawString(code[i], x + (textWidth / code.length) * i, y);
        }
    }

    /**
     * 获取绘制的验证码
     *
     * @param type  字符串类型
     * @param count 字符个数
     * @return 字符串数组
     */
    protected static String[] getDrawCode(CaptchaType type, int count) {
        String[] code = new String[count];
        Random ran = new Random();
        for (int i = 0; i < count; i++) {
            switch (type) {
                case DIGIT:
                    code[i] = DIGIT[ran.nextInt(DIGIT.length)];
                    break;
                case LETTER:
                    code[i] = LETTER[ran.nextInt(LETTER.length)];
                    break;
                default:
                    code[i] = ALPHANUMERIC[ran.nextInt(ALPHANUMERIC.length)];
                    break;

            }
        }
        return code;
    }

    /**
     * @param graphic   画板对象
     * @param width     画板宽度
     * @param height    画板高度
     * @param lineCount 干扰线条数
     * @Description 绘制干扰线
     */
    protected static void drawLine(Graphics graphic, int width, int height, Integer lineCount) {
        // 画干扰线
        for (int i = 0; i < lineCount; i++) {
            // 设置随机颜色
            graphic.setColor(new Color(RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255), RandomUtils.nextInt(0, 255)));
            // 随机画线
            graphic.drawLine(RandomUtils.nextInt(0, width), RandomUtils.nextInt(0, height), RandomUtils.nextInt(0, width), RandomUtils.nextInt(0, height));
        }
    }
}

```

案例完整代码请参考：[https://github.com/mingyang66/spring-parent](https://github.com/mingyang66/spring-parent)
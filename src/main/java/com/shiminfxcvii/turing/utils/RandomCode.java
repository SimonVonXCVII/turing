package com.shiminfxcvii.turing.utils;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.web.server.header.CacheControlServerHttpHeadersWriter;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * 随机码生成类
 *
 * @author ShiminFXCVII
 * @since 2022/6/14 11:56 周二
 */
@Component
public class RandomCode {

    // 随机产生数字与字母组合的字符串
    private static final String STRING = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
    // 图片高
    private static final int HEIGHT = 25;
    // 图片宽
    private static final int WIDTH = 95;
    // 颜色数组
    private static final Color[] COLORS = new Color[]{Color.BLACK, Color.RED, Color.MAGENTA, Color.BLUE};
    // 字体
    private static final Font FONT = new Font(Font.SANS_SERIF, Font.BOLD, 18);
    // 随机数
    private static final Random RANDOM = new Random();

    /**
     * 生成随机验证码图片并写入相应中
     *
     * @param response the response.
     * @return 验证码字符串
     * @throws IOException 将内存中的图片通过流动形式输出到客户端时可能会抛出该异常
     * @author ShiminFXCVII
     * @since 2022/6/14 11:56
     */
    public String getCaptcha(HttpServletResponse response) throws IOException {
        // BufferedImage 类是具有缓冲区的 Image 类，Image 类是用于描述图像信息的类
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_BGR);
        // Graphics 可以在图像上进行各种绘制操作
        Graphics graphics = image.getGraphics();
        // 填充矩形图片
        graphics.fillRect(0, 0, WIDTH, HEIGHT);
        // 设置字符字体
        graphics.setFont(FONT);
        // 绘制字符
        StringBuilder randomString = new StringBuilder();
        // 设置字符数量
        for (int i = 1; i < 5; i++) {
            // 设置单个字符颜色
            graphics.setColor(COLORS[RANDOM.nextInt(COLORS.length)]);
            // 将图形上下文的原点平移到当前坐标系中的点
            graphics.translate(RANDOM.nextInt(3), RANDOM.nextInt(3));
            // 生成随机字符
            String rand = String.valueOf(STRING.charAt(RANDOM.nextInt(STRING.length())));
            // 绘制字符
            graphics.drawString(rand, 13 * i, 16);
            // 拼接字符串
            randomString.append(rand);
        }
        // 绘制干扰线
        // 设置干扰线数量
        for (int i = 1; i < 50; i++) {
            int x = RANDOM.nextInt(WIDTH);
            int y = RANDOM.nextInt(HEIGHT);
            int xl = RANDOM.nextInt(i);
            int yl = RANDOM.nextInt(i);
            // 设置干扰线颜色
            graphics.setColor(new Color(RANDOM.nextInt(255), RANDOM.nextInt(255), RANDOM.nextInt(255)));
            // 绘制干扰线
            graphics.drawLine(x, y, x + xl, y + yl);
        }
        // 释放 Graphics 对象正在使用的任何系统资源。调用 dispose 后不能使用 Graphics 对象。
        graphics.dispose();
        // 设置响应类型，告诉浏览器输出的内容为图片
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        // 设置响应头信息，告诉浏览器不要缓存此内容
        response.setHeader(HttpHeaders.CACHE_CONTROL, CacheControlServerHttpHeadersWriter.CACHE_CONTRTOL_VALUE);
        response.setHeader(HttpHeaders.EXPIRES, CacheControlServerHttpHeadersWriter.EXPIRES_VALUE);
        response.setHeader(HttpHeaders.PRAGMA, CacheControlServerHttpHeadersWriter.PRAGMA_VALUE);
        // 将内存中的图片通过流动形式输出到客户端
        ImageIO.write(image, MediaType.IMAGE_JPEG.getSubtype(), response.getOutputStream());
        return randomString.toString();
    }

    /**
     * 根据指定长度生成字母和数字的随机数
     * 0~9 的 ASCII 为 48~57
     * A~Z 的 ASCII 为 65~90
     * a~z 的 ASCII 为 97~122
     *
     * @param length 随机数长度
     * @return 生成的随机数
     * @author ShiminFXCVII
     * @since 10/12/2022 1:05 PM
     */
    public String getRandom(int length) {
        StringBuilder sb = new StringBuilder(length);
        // 随机用以下三个随机生成器
        Random rand = new Random();
        Random random = new Random();
        int data;
        for (int i = 0; i < length; i++) {
            int index = rand.nextInt(3);
            // 目的是随机选择生成数字，大小写字母
            switch (index) {
                case 0 -> {
                    // 仅仅会生成 0~9
                    data = random.nextInt(10);
                    sb.append(data);
                }
                case 1 -> {
                    // 保证只会产生 65~90 之间的整数
                    data = random.nextInt(26) + 65;
                    sb.append((char) data);
                }
                case 2 -> {
                    // 保证只会产生 97~122 之间的整数
                    data = random.nextInt(26) + 97;
                    sb.append((char) data);
                }
            }
        }
        return sb.toString();
    }
}
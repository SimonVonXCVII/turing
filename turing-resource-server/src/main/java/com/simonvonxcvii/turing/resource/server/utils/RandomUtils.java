//package com.simonvonxcvii.turing.utils;
//
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.security.web.server.header.CacheControlServerHttpHeadersWriter;
//import org.springframework.stereotype.Component;
//
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.util.Random;
//
/// **
// * 随机数值工具类
// * TODO 有没有现成框架？有 Kaptcha，但是早已没有更新，reCAPTCHA？
// *  使用 OIDC 模式就没有必要使用这种验证码了吧？
// *
// * @author Simon Von
// * @since 2022/6/14 11:56 周二
// */
//@Component
//public class RandomUtils {
//
//    /**
//     * 随机产生数字与字母组合的字符串
//     */
//    private static final String STRING = "0123456789ABCDEFGHJKLMNPQRSTUVWXYZ";
//    /**
//     * 图片高
//     */
//    private static final int HEIGHT = 30;
//    /**
//     * 图片宽
//     */
//    private static final int WIDTH = 100;
//    /**
//     * 颜色数组
//     */
//    private static final Color[] COLORS = new Color[]{Color.BLACK, Color.RED, Color.MAGENTA, Color.BLUE};
//    /**
//     * 字体
//     */
//    private static final Font FONT = new Font(Font.SANS_SERIF, Font.BOLD, 18);
//    /**
//     * 随机数
//     */
//    private static final Random RANDOM = new Random();
//    /**
//     * 使用指定的线宽以及端点和连接样式的默认值构造实体 BasicStroke。
//     */
//    private static final BasicStroke BASIC_STROKE = new BasicStroke(2.0F);
//
//    /**
//     * 生成随机验证码图片并写入相应中
//     *
//     * @param response the response.
//     * @return 验证码字符串
//     * @throws IOException 将内存中的图片通过流动形式输出到客户端时可能会抛出该异常
//     * @author Simon Von
//     * @since 2022/6/14 11:56
//     */
//    public String getCaptcha(HttpServletResponse response) throws IOException {
//        // 构造预定义图像类型之一的缓冲图像。图像的色彩空间是默认的 sRGB 空间。
//        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_BGR);
//        // 此方法返回 Graphics2D，但此处是为了向后兼容。createGraphics 更方便，因为它被声明为返回 Graphics2D。
//        Graphics graphics = image.getGraphics();
//        // 填充指定的矩形。矩形的左边缘和右边缘位于 x 和 x + 宽度 - 1。顶部和底部边缘位于 y 和 y + 高度 - 1。
//        // 生成的矩形覆盖一个区域宽度像素宽乘高度像素高。矩形使用图形上下文的当前颜色填充。
//        graphics.fillRect(0, 0, WIDTH, HEIGHT);
//        // 将此图形上下文的字体设置为指定的字体。使用此图形上下文的所有后续文本操作都使用此字体。空参数将被静默忽略。
//        graphics.setFont(FONT);
//        // 设置 Graphics2D 上下文的描边。
//        ((Graphics2D) graphics).setStroke(BASIC_STROKE);
//        // 绘制字符
//        StringBuilder randomString = new StringBuilder();
//        // 四个字符和四条干扰线
//        for (int i = 1; i < 5; i++) {
//            // 绘制字符
//            // 将此图形上下文的当前颜色设置为指定的颜色。使用此图形上下文的所有后续图形操作都使用此指定颜色。空参数将被静默忽略。
//            graphics.setColor(COLORS[RANDOM.nextInt(COLORS.length)]);
//            // 将图形上下文的原点转换为当前坐标系中的点 （x， y）。修改此图形上下文，使其新原点对应于此图形上下文的原始坐标系中的点 （x， y）。
//            // 在此图形上下文的后续渲染操作中使用的所有坐标都将相对于此新原点。
//            graphics.translate(RANDOM.nextInt(3), RANDOM.nextInt(3));
//            // 生成随机字符
//            String rand = String.valueOf(STRING.charAt(RANDOM.nextInt(STRING.length())));
//            // 使用此图形上下文的当前字体和颜色绘制指定字符串给出的文本。最左侧字符的基线位于此图形上下文坐标系中的位置 （x， y）。
//            graphics.drawString(rand, 13 * i, 16);
//            // 拼接字符串
//            randomString.append(rand);
//            // 绘制干扰线
//            // 使用当前颜色在此图形上下文坐标系中的点 （x1， y1） 和 （x2， y2） 之间绘制一条线。
//            graphics.drawLine(RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT), RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT));
//        }
//        // 释放 Graphics 对象正在使用的任何系统资源。调用 dispose 后不能使用 Graphics 对象。
//        graphics.dispose();
//        // 设置响应类型，告诉浏览器输出的内容为图片
//        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
//        // 设置响应头信息，告诉浏览器不要缓存此内容
//        response.setHeader(HttpHeaders.CACHE_CONTROL, CacheControlServerHttpHeadersWriter.CACHE_CONTROL_VALUE);
//        response.setHeader(HttpHeaders.EXPIRES, CacheControlServerHttpHeadersWriter.EXPIRES_VALUE);
//        response.setHeader(HttpHeaders.PRAGMA, CacheControlServerHttpHeadersWriter.PRAGMA_VALUE);
//        // 将内存中的图片通过流动形式输出到客户端
//        ImageIO.write(image, MediaType.IMAGE_JPEG.getSubtype(), response.getOutputStream());
//        return randomString.toString();
//    }
//
//    /**
//     * 根据指定长度生成字母和数字的随机数
//     * 0~9 的 ASCII 为 48~57
//     * A~Z 的 ASCII 为 65~90
//     * a~z 的 ASCII 为 97~122
//     *
//     * @param length 随机数长度
//     * @return 生成的随机数
//     * @author Simon Von
//     * @since 10/12/2022 1:05 PM
//     */
//    public String getRandom(int length) {
//        StringBuilder sb = new StringBuilder(length);
//        // 随机用以下三个随机生成器
//        Random rand = new Random();
//        Random random = new Random();
//        int data;
//        for (int i = 0; i < length; i++) {
//            int index = rand.nextInt(3);
//            // 目的是随机选择生成数字，大小写字母
//            switch (index) {
//                case 0 -> {
//                    // 仅仅会生成 0~9
//                    data = random.nextInt(10);
//                    sb.append(data);
//                }
//                case 1 -> {
//                    // 保证只会产生 65~90 之间的整数
//                    data = random.nextInt(26) + 65;
//                    sb.append((char) data);
//                }
//                case 2 -> {
//                    // 保证只会产生 97~122 之间的整数
//                    data = random.nextInt(26) + 97;
//                    sb.append((char) data);
//                }
//            }
//        }
//        return sb.toString();
//    }
//}

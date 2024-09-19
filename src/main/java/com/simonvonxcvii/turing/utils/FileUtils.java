package com.simonvonxcvii.turing.utils;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import org.springframework.core.io.ClassPathResource;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件工具类
 *
 * @author SimonVonXCVII
 * @since 11/24/2022 11:35 PM
 */
public class FileUtils {

    private static final ITextRenderer RENDERER = new ITextRenderer();

    static {
        // 携带图片,将图片标签转换为 itext 自己的图片对象
        RENDERER.getSharedContext().setReplacedElementFactory(new Base64ImgReplacedElementFactory());
        RENDERER.getSharedContext().getTextRenderer().setSmoothingThreshold(0);
        ITextFontResolver fontResolver = RENDERER.getFontResolver();
        // 字体文件路径
        ClassPathResource classPathResource = new ClassPathResource("/font/simsun.ttc");
        // true 和 false 暂时没有发现区别
        try {
            fontResolver.addFont(classPathResource.getPath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成 PDF 字节流
     *
     * @param htmlString 模板转换成的字符串
     * @return PDF 字节流
     * @author SimonVonXCVII
     * @since 11/24/2022 11:39 PM
     */
    public static byte[] generatePDF(String htmlString) {
        RENDERER.setDocumentFromString(htmlString);
        // 处理图片
        // 如果是本地图片使用 file：,这里指定图片的父级目录。html上写相对路径，
        RENDERER.getSharedContext().setBaseURL("file:/enland/myproject/mine/point");
        RENDERER.layout();
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            RENDERER.createPDF(os);
            return os.toByteArray();
        } catch (IOException | DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将多个文件转换为一个 zip 压缩文件
     *
     * @param files       文件数组
     * @param zipFilename 需要生成的 zip 目标文件
     * @author SimonVonXCVII
     * @since 12/3/2022 8:06 PM
     */
    public void generateZip(File[] files, File zipFilename) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(zipFilename);
             ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream))) {
            // 创建读写缓冲区
            byte[] bytes = new byte[1024 * 10];
            for (File file : files) {
                // 创建ZIP实体，并添加进压缩包
                ZipEntry zipEntry = new ZipEntry(file.getName());
                zipOutputStream.putNextEntry(zipEntry);
                // 读取待压缩的文件并写进压缩包里
                try (FileInputStream fileInputStream = new FileInputStream(file);
                     BufferedInputStream bufferInputStream = new BufferedInputStream(fileInputStream, 1024 * 10)) {
                    int read;
                    while ((read = bufferInputStream.read(bytes, 0, 1024 * 10)) != -1) {
                        zipOutputStream.write(bytes, 0, read);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

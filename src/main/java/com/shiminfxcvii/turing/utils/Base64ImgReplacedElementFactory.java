package com.shiminfxcvii.turing.utils;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextFSImage;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;

import java.io.IOException;
import java.util.Base64;

/**
 * Base64 工具类
 *
 * @author ShiminFXCVII
 * @since 11/28/2022 3:27 PM
 */
public class Base64ImgReplacedElementFactory implements ReplacedElementFactory {

    /**
     * 实现 createReplacedElement 替换 html 中的 Img 标签
     *
     * @param c         上下文
     * @param box       盒子
     * @param uac       回调
     * @param cssWidth  css 宽
     * @param cssHeight css 高
     * @return ReplacedElement
     */
    @Override
    public ReplacedElement createReplacedElement(LayoutContext c, BlockBox box, UserAgentCallback uac, int cssWidth, int cssHeight) {
        Element e = box.getElement();
        if (e == null) {
            return null;
        }
        String nodeName = e.getNodeName();
        // 找到 img 标签
        if (nodeName.equals("img")) {
            String attribute = e.getAttribute("src");
            FSImage fsImage;
            try {
                // 生成 itext 图像
                fsImage = buildImage(attribute, uac);
            } catch (BadElementException | IOException e1) {
                fsImage = null;
            }
            if (fsImage != null) {
                // 对图像进行缩放
                if (cssWidth != -1 || cssHeight != -1) {
                    fsImage.scale(cssWidth, cssHeight);
                }
                return new ITextImageElement(fsImage);
            }
        }
        return null;
    }

    /**
     * 编解码 base64 并生成 itext 图像
     */
    protected FSImage buildImage(String srcAttr, UserAgentCallback uac) throws IOException, BadElementException {
        FSImage fiImg;
        // 图片的 src 要为 src="https://img-blog.csdnimg.cn/2022010616555048137.jpg" 这种 base64 格式
        if (srcAttr.toLowerCase().startsWith("data:image/")) {
            String base64Code = srcAttr.substring(srcAttr.indexOf("base64,") + "base64,".length());
            // 解码
            byte[] decodedBytes = Base64.getDecoder().decode(base64Code);
            fiImg = new ITextFSImage(Image.getInstance(decodedBytes));
        } else {
            fiImg = uac.getImageResource(srcAttr).getImage();
        }
        return fiImg;
    }

    @Override
    public void reset() {
    }

    @Override
    public void remove(Element arg0) {
    }

    @Override
    public void setFormSubmissionListener(FormSubmissionListener arg0) {
    }

}

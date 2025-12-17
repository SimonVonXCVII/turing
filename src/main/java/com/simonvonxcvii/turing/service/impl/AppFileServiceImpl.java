package com.simonvonxcvii.turing.service.impl;

import com.simonvonxcvii.turing.entity.AppFile;
import com.simonvonxcvii.turing.enums.FileTypeEnum;
import com.simonvonxcvii.turing.model.dto.UploadFileDTO;
import com.simonvonxcvii.turing.repository.jpa.AppFileJpaRepository;
import com.simonvonxcvii.turing.service.IAppFileService;
import com.simonvonxcvii.turing.utils.UserUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

/**
 * 文件表 服务实现类
 *
 * @author Simon Von
 * @since 2023-04-01 23:08:08
 */
@RequiredArgsConstructor
@Service
public class AppFileServiceImpl implements IAppFileService {

    private static final Log log = LogFactory.getLog(AppFileServiceImpl.class);

    private final AppFileJpaRepository appFileJpaRepository;

    /**
     * 上传文件
     *
     * @param multipartFile    文件
     * @param originalFilename 原文件名
     * @param suffix           文件后缀
     * @param bizType          文件类型
     * @param remark           备注
     * @param isCompress       是否压缩
     * @param isApp            是否 app 端上传
     * @return 文件 id、文件名
     * @author Simon Von
     * @since 2023-04-01 23:08:08
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UploadFileDTO uploadFile(
            MultipartFile multipartFile, String originalFilename, String suffix, FileTypeEnum bizType, String remark,
            Boolean isCompress, boolean isApp
    ) throws IOException {
        byte[] bytes;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();
             InputStream is = multipartFile.getInputStream()) {
            // 是否压缩 TODO 尝试使用其它方式
            // 这样写的原因在于 isCompress 可以为 null（拆箱的 'isCompress' 可能产生 'java.lang.NullPointerException'）
            if (Boolean.TRUE.equals(isCompress)) {
//                Thumbnails.of(is)
//                        // 设置缩略图的比例因子
//                        .scale(0.9f)
//                        // 设置将缩略图写入外部目标（例如文件或输出流）时用于压缩缩略图的压缩算法的输出质量。
//                        // 该值是介于 0.0f 和 1.0f 之间的浮点数，其中 0.0f 表示最低质量，1.0f 表示压缩编解码器应使用的最高质量设置。
//                        // 调用此方法来设置此参数是可选的。
//                        .outputQuality(0.9f)
//                        // 创建缩略图并将其写入 OutputStream。
//                        // 要调用此方法，缩略图必须是从单一来源创建的。
//                        // 请注意，在将缩略图写入 OutputStream 完成后，不会调用 OutputStream.close() 方法。
//                        .toOutputStream(os);
            } else {
                byte[] buf = new byte[1024];
                int len;
                while ((len = is.read(buf)) != -1) {
                    os.write(buf, 0, len);
                }
            }
            bytes = os.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("上传文件失败：" + e.getMessage());
        }
        // 生成 md5
        String md5 = DigestUtils.md5DigestAsHex(bytes);
        // 通过 md5 查询文件
        Optional<AppFile> appFileOptional = appFileJpaRepository.findOneByMd5(md5);
        UploadFileDTO dto = new UploadFileDTO();
        // 如果文件存在则直接返回文件信息
        if (appFileOptional.isPresent()) {
            // 设置需要返回的文件信息
            dto.setId(appFileOptional.get().getId());
            dto.setFilename(originalFilename);
            return dto;
        }
        // 保存文件名
        String filename = UUID.randomUUID() + suffix; // todo
        // 将文件保存在不同的路径
        File directory = new File("/shiting/project/turing/file/" + bizType.getValue());
        // 如果当前不存在该文件夹则创建
        if (!directory.exists() && directory.mkdirs()) {
            log.debug("已创建文件夹: " + directory);
        }
        File file = new File(directory, filename);
        Path path = file.toPath();
        Files.write(path, bytes);
        AppFile appFile = new AppFile();
        // 所有者 id
        appFile.setOwnerId(UserUtils.getId());
        // 文件名
        appFile.setFilename(filename);
        // 原始文件名
        appFile.setOriginFilename(originalFilename);
        // 后缀
        appFile.setSuffix(suffix);
        // 内容类型
        appFile.setContentType(Files.probeContentType(path));
        // 内容长度
        appFile.setContentLength(Files.size(path));
        // md5
        appFile.setMd5(md5);
        // 存放路径
        appFile.setPath(path.toString());
        // 业务类型
        appFile.setBizType(bizType);
        // 备注
        appFile.setRemark(remark);
        appFileJpaRepository.save(appFile);
        // 设置需要返回的文件信息
        dto.setId(appFile.getId());
        dto.setFilename(originalFilename);
        return dto;
    }

    /**
     * 根据文件 id 获取文件
     *
     * @param id       文件 id
     * @param response 响应
     * @author Simon Von
     * @since 2023-04-01 23:08:08
     */
    @Override
    public void getFileById(Integer id, HttpServletResponse response) throws IOException {
        AppFile appFile = appFileJpaRepository.findById(id).orElseThrow(() -> new RuntimeException("没有找到该文件"));
        String appFilePath = appFile.getPath();
        if (appFilePath.isBlank()) {
            throw new RuntimeException("文件路径为空");
        }
        File file = new File(appFilePath);
        if (!file.isFile()) {
            throw new RuntimeException("无法获取该文件，当前所在路径中没有该文件");
        }
        // 设置发送到客户端的响应的内容类型（如果尚未提交响应）
        response.setContentType(appFile.getContentType());
        // 将发送到客户端的响应的字符编码（MIME 字符集）设置为 UTF-8
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // 文件名
        String filename = appFile.getOriginFilename();
        // 处理空格变成加号的问题
        // TODO: 2023/4/11 是否还需要处理？先注释，不对再放开
//        filename = filename.replaceAll("\\+", "%20");
        // 使用给定的名称和值设置响应标头
        // 使用 ContentDisposition 构建 CONTENT_DISPOSITION 可以避免文件名称乱码的问题
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build()
                .toString());
        // 设置响应中内容正文的长度
        response.setContentLengthLong(appFile.getContentLength());
        // 将文件中的所有字节复制到输出流
        Files.copy(file.toPath(), response.getOutputStream());
    }

    /**
     * 根据图片文件 id 获取原始图片
     *
     * @param id       文件 id
     * @param response 响应
     * @author Simon Von
     * @since 2023-04-01 23:08:08
     */
    @Override
    public void getOriginalImageById(Integer id, HttpServletResponse response) throws IOException {
        AppFile appFile = appFileJpaRepository.findById(id).orElseThrow(() -> new RuntimeException("没有找到该图片"));
        String appFilePath = appFile.getPath();
        if (!StringUtils.hasText(appFilePath)) {
            throw new RuntimeException("图片路径为空");
        }
        File file = new File(appFilePath);
        if (!file.isFile()) {
            throw new RuntimeException("无法获取该图片，当前所在路径中没有该图片");
        }
        // 设置发送到客户端的响应的内容类型（如果尚未提交响应）
        response.setContentType(appFile.getContentType());
        // 将发送到客户端的响应的字符编码（MIME 字符集）设置为 UTF-8
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // 文件名
        String filename = appFile.getOriginFilename();
        // 处理空格变成加号的问题
        // TODO: 2023/4/11 是否还需要处理？先注释，不对再放开
//        filename = filename.replaceAll("\\+", "%20");
        // 使用给定的名称和值设置响应标头
        // 使用 ContentDisposition 构建 CONTENT_DISPOSITION 可以避免文件名称乱码的问题
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build()
                .toString());
        // 设置响应中内容正文的长度
        response.setContentLengthLong(appFile.getContentLength());
        // 将文件中的所有字节复制到输出流
        Files.copy(file.toPath(), response.getOutputStream());
    }

    /**
     * 根据文件 id 删除文件
     *
     * @param id 文件 id
     * @author Simon Von
     * @since 2022/8/17 20:07
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Integer id) {
        appFileJpaRepository.deleteById(id);
    }

}

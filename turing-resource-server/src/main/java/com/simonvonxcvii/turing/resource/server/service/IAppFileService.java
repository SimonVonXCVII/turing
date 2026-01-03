package com.simonvonxcvii.turing.resource.server.service;

import com.simonvonxcvii.turing.resource.server.enums.FileTypeEnum;
import com.simonvonxcvii.turing.resource.server.model.dto.UploadFileDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * <p>
 * 文件表 服务类
 * </p>
 *
 * @author Simon Von
 * @since 2023-04-01 23:08:08
 */
public interface IAppFileService {

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
    UploadFileDTO uploadFile(MultipartFile multipartFile, String originalFilename, String suffix,
                             FileTypeEnum bizType, String remark, Boolean isCompress, boolean isApp) throws IOException;

    /**
     * 根据文件 id 获取文件
     *
     * @param id       文件 id
     * @param response 响应
     * @author Simon Von
     * @since 2023-04-01 23:08:08
     */
    void getFileById(Integer id, HttpServletResponse response) throws IOException;

    /**
     * 根据图片文件 id 获取原始图片
     *
     * @param id       文件 id
     * @param response 响应
     * @author Simon Von
     * @since 2023-04-01 23:08:08
     */
    void getOriginalImageById(Integer id, HttpServletResponse response) throws IOException;

    /**
     * 根据文件 id 删除文件
     *
     * @param id 文件 id
     * @author Simon Von
     * @since 2023-04-01 23:08:08
     */
    void deleteById(Integer id);

}

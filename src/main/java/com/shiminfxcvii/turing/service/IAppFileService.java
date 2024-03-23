package com.shiminfxcvii.turing.service;

import com.shiminfxcvii.turing.enums.FileTypeEnum;
import com.shiminfxcvii.turing.model.dto.UploadFileDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * <p>
 * 文件表 服务类
 * </p>
 *
 * @author ShiminFXCVII
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
     * @author ShiminFXCVII
     * @since 2023-04-01 23:08:08
     */
    UploadFileDTO uploadFile(MultipartFile multipartFile, String originalFilename, String suffix,
                             FileTypeEnum bizType, String remark, Boolean isCompress, boolean isApp) throws IOException;

    /**
     * 根据文件 id 获取文件
     *
     * @param id       文件 id
     * @param response 响应
     * @author ShiminFXCVII
     * @since 2023-04-01 23:08:08
     */
    void getFileById(String id, HttpServletResponse response) throws IOException;

    /**
     * 根据图片文件 id 获取原始图片
     *
     * @param id       文件 id
     * @param response 响应
     * @author ShiminFXCVII
     * @since 2023-04-01 23:08:08
     */
    void getOriginalImageById(String id, HttpServletResponse response) throws IOException;

    /**
     * 根据文件 id 删除文件
     *
     * @param id 文件 id
     * @author ShiminFXCVII
     * @since 2023-04-01 23:08:08
     */
    void deleteById(String id);

}

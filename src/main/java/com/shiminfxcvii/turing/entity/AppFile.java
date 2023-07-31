package com.shiminfxcvii.turing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shiminfxcvii.turing.enums.FileTypeEnum;
import com.shiting.soil.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 文件表
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2023-04-01 23:08:08
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("soil_app_file")
public class AppFile extends BaseEntity {

    /**
     * 所有者 id
     */
    private String ownerId;

    /**
     * 文件名
     */
    private String filename;

    /**
     * 原始文件名
     */
    private String originFilename;

    /**
     * 后缀
     */
    private String suffix;

    /**
     * 内容类型
     */
    private String contentType;

    /**
     * 内容长度
     */
    private Long contentLength;

    /**
     * md5
     */
    private String md5;

    /**
     * 存放路径
     */
    private String path;

    /**
     * 业务类型
     */
    private FileTypeEnum bizType;

    /**
     * 备注
     */
    private String remark;

}
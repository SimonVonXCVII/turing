package com.shiminfxcvii.turing.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * UploadFileDTO
 *
 * @author ShiminFXCVII
 * @since 2023/4/3 10:52
 */
@Getter
@Setter
public class UploadFileDTO {

    /**
     * 文件 id
     */
    @Schema(description = "文件 id")
    private String id;

    /**
     * 文件名
     */
    @Schema(description = "文件名")
    private String filename;

}
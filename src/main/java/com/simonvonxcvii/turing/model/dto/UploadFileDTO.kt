package com.simonvonxcvii.turing.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * UploadFileDTO
 *
 * @author Simon Von
 * @since 2023/4/3 10:52
 */
@Getter
@Setter
public class UploadFileDTO {

    /**
     * 文件 id
     */
    private Integer id;

    /**
     * 文件名
     */
    private String filename;

}

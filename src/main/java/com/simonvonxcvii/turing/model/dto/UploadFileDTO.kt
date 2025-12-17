package com.simonvonxcvii.turing.model.dto

/**
 * UploadFile DTO
 * 
 * @author Simon Von
 * @since 2023/4/3 10:52
 */
data class UploadFileDTO(
    /**
     * 文件 id
     */
    var id: Int? = null,

    /**
     * 文件名
     */
    var filename: String? = null
)

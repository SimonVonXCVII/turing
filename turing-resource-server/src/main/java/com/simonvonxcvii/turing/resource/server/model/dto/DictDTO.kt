package com.simonvonxcvii.turing.resource.server.model.dto

import com.simonvonxcvii.turing.resource.server.model.query.PageQuery
import jakarta.validation.constraints.NotBlank
import java.io.Serializable

/**
 * DTO for [com.simonvonxcvii.turing.resource.server.entity.Dict]
 * 
 * @author Simon Von
 * @since 12/30/2022 4:03 PM
 */
data class DictDTO(
    /**
     * 字典 id
     */
    var id: Int? = null,

    /**
     * 字典类型
     */
    @field:NotBlank(message = "字典类型不能为空")
    var type: String? = null,

    /**
     * 上级字典 id
     */
    var pid: Int? = null,

    /**
     * 字典名称
     */
    @field:NotBlank(message = "字典名称不能为空")
    var name: String? = null,

    /**
     * 字典值
     */
    @field:NotBlank(message = "字典值不能为空")
    var value: String? = null,

    /**
     * 字典说明
     */
    var description: String? = null,

    /**
     * 字典排序
     */
    var sort: Int? = null,

    /**
     * 下级字典
     */
    var children: MutableList<DictDTO>? = mutableListOf()
) : Serializable, PageQuery()

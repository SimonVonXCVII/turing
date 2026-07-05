package com.simonvonxcvii.turing.common.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.io.Serializable

/**
 * DTO for [com.simonvonxcvii.turing.common.entity.Permission]
 * 
 * @author Simon Von
 * @since 12/30/2022 4:03 PM
 */
data class PermissionDto(
    /**
     * 权限 id
     */
    var id: Int? = null,

    /**
     * 上级权限 id
     */
    var pid: Int? = null,

    /**
     * 权限名称
     */
    @field:NotBlank(message = "权限名称不能为空")
    var name: String? = null,

    /**
     * 权限编码
     */
    @field:NotBlank(message = "权限名称不能为空")
    var code: String? = null,

    /**
     * 排序编号
     */
    @field:NotNull(message = "排序编号不能为空")
    var sort: Int? = null,

    /**
     * 子级权限集合
     */
    var children: MutableList<PermissionDto>? = mutableListOf()
) : Serializable

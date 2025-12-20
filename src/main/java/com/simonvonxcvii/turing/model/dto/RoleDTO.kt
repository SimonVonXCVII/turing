package com.simonvonxcvii.turing.model.dto

import com.simonvonxcvii.turing.model.query.PageQuery
import jakarta.validation.constraints.NotNull
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * DTO for [com.simonvonxcvii.turing.entity.Role]
 *
 * @author Simon Von
 * @since 12/30/2022 4:03 PM
 */
data class RoleDTO(
    /**
     * 角色 id
     */
    var id: Int? = null,

    /**
     * 角色名称
     */
    //    @NotBlank(message = "角色名称不能为空")
    var name: String? = null,

    /**
     * 角色编码
     */
    //    @NotBlank(message = "角色编码不能为空")
    var authority: String? = null,

    /**
     * 状态
     */
    @field:NotNull(message = "状态不能为空")
    var status: Byte? = null,

    /**
     * 备注
     */
//    @NotBlank(message = "备注不能为空")
    var remark: String? = null,

    /**
     * 创建时间
     */
    var createdDate: LocalDateTime? = null,

    /**
     * 权限 id 集合
     */
//    @NotEmpty(message = "权限 id 集合不能为空")
    var permissions: MutableSet<Int>? = mutableSetOf(),

    /**
     * 起始创建时间
     */
    var startTime: LocalDate? = null,

    /**
     * 截止创建时间
     */
    var endTime: LocalDate? = null
) : Serializable, PageQuery()

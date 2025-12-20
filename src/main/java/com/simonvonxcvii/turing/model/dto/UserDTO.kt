package com.simonvonxcvii.turing.model.dto

import com.simonvonxcvii.turing.model.query.PageQuery
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.io.Serializable
import java.time.LocalDateTime

/**
 * DTO for [com.simonvonxcvii.turing.entity.User]
 *
 * @author Simon Von
 * @since 12/30/2022 4:03 PM
 */
data class UserDTO(
    /**
     * 用户姓名
     */
    @field:NotBlank(message = "用户姓名不能为空")
    var realName: String? = null,

    /**
     * 用户手机号
     */
    @field:NotNull(message = "用户手机号不能为空")
    var mobile: Long? = null,

    /**
     * 用户性别
     */
    @field:NotBlank(message = "用户性别不能为空")
    var gender: String? = null,

    /**
     * 单位 id
     */
    @field:NotBlank(message = "单位 id 不能为空")
    var orgId: Int? = null,

    /**
     * 单位名称
     */
    var orgName: String? = null,

    /**
     * 部门
     */
    var department: String? = null,

    /**
     * 登录账号
     */
    @field:NotBlank(message = "登录账号不能为空")
    var username: String? = null,

    /**
     * 是否已过期
     */
    var accountNonExpired: Boolean? = null,

    /**
     * 是否已锁定
     */
    var accountNonLocked: Boolean? = null,

    /**
     * 是否凭证已过期
     */
    var credentialsNonExpired: Boolean? = null,

    /**
     * 是否启用
     */
    var enabled: Boolean? = null,

    /**
     * 是否单位管理员
     */
    var manager: Boolean? = null,

    /**
     * 是否需要重新设置密码
     */
    var needResetPassword: Boolean? = null,

    /**
     * 用户角色
     */
    var authorities: MutableCollection<RoleDTO>? = mutableListOf(),

    /**
     * 用户角色编码
     */
    var roles: MutableSet<String>? = mutableSetOf(),

    /**
     * 用户 id
     */
    var id: Int? = null,

    /**
     * 创建时间
     */
    var createdDate: LocalDateTime? = null,

    /**
     * 角色集合
     */
    @field:NotEmpty(message = "角色集合不能为空")
    var roleIdList: MutableList<Int>? = mutableListOf()
) : Serializable, PageQuery()

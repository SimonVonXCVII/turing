package com.simonvonxcvii.turing.model.dto

import com.simonvonxcvii.turing.model.query.PageQuery
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

/**
 * Menu DTO
 *
 * @author Simon Von
 * @since 12/30/2022 4:03 PM
 */
class MenuDTO(
    /**
     * 菜单 id
     */
    var id: Int? = null,

    /**
     * 菜单类型：目录、菜单、按钮、内嵌、外链
     *
     * @see com.simonvonxcvii.turing.enums.MenuTypeEnum
     */
    @field:NotBlank(message = "菜单类型不能为空")
    var type: String = "",

    /**
     * 菜单名称
     */
    @field:NotBlank(message = "菜单名称不能为空")
    @field:Size(min = 3, message = "菜单名称至少 2 个字符")
    var name: String? = null,

    /**
     * 上级菜单 id
     */
    var pid: Int? = null,

    /**
     * 路由地址
     */
    var path: String? = null,

    /**
     * 激活路径
     */
    var activePath: String? = null,

    /**
     * 页面组件
     */
    var component: String? = null,

    /**
     * 权限标识
     */
    var authCode: String? = null,

    /**
     * 状态
     */
    @field:NotNull(message = "状态不能为空")
    var status: Byte? = null,

    /**
     * 菜单元数据
     */
    @field:NotNull(message = "菜单元数据不能为空")
    var meta: MenuMetaDTO = MenuMetaDTO(),

    /**
     * 子菜单
     */
    var children: MutableList<MenuDTO>? = mutableListOf()
) : PageQuery()

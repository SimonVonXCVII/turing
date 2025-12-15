package com.simonvonxcvii.turing.model.dto

import com.simonvonxcvii.turing.model.query.PageQuery
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

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
     * 上级菜单 id
     */
    var pid: Int? = null,

    /**
     * 系统权限 id
     */
    var permissionId: Int? = null,

    /**
     * 菜单名称
     */
    @field:NotBlank(message = "菜单名称不能为空")
    var name: String? = null,

    /**
     * 菜单标题
     */
    var title: String? = null,

    /**
     * 菜单类型：目录、菜单、按钮
     *
     * @see com.simonvonxcvii.turing.enums.MenuTypeEnum
     */
    @field:NotBlank(message = "菜单类型不能为空")
    var type: String? = null,

    /**
     * 权限标识
     */
    var authCode: String? = null,

    /**
     * 路由地址
     */
    @field:NotBlank(message = "路由地址不能为空")
    var path: String? = null,

    /**
     * 页面组件
     */
    @field:NotBlank(message = "页面组件不能为空")
    var component: String? = null,

    /**
     * 状态
     */
    @field:NotNull(message = "状态不能为空")
    var status: Byte? = null,

    /**
     * 图标
     */
    var icon: String? = null,

    /**
     * 菜单排序
     */
    var sort: Int? = null,

    /**
     * 是否显示
     */
    var showed: Boolean? = null,

    /**
     * 是否缓存
     */
    var cached: Boolean? = null,

    /**
     * 是否外链
     */
    var external: Boolean? = null,

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

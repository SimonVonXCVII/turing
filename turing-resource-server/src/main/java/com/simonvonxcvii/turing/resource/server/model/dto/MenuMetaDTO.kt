package com.simonvonxcvii.turing.resource.server.model.dto

import jakarta.validation.constraints.NotBlank
import java.io.Serializable

/**
 * DTO for [com.simonvonxcvii.turing.resource.server.entity.MenuMeta]
 *
 * @author Simon Von
 * @since 12/30/2022 4:03 PM
 */
data class MenuMetaDTO(
    /**
     * 标题
     */
    @field:NotBlank(message = "标题不能为空")
    var title: String = "",

    /**
     * 图标
     */
    var icon: String? = null,

    /**
     * 激活图标
     */
    var activeIcon: String? = null,

    /**
     * 徽标类型
     *
     * @see com.simonvonxcvii.turing.resource.server.enums.MenuBadgeTypeEnum
     */
    var badgeType: String? = null,

    /**
     * 徽章内容
     */
    var badge: String? = null,

    /**
     * 徽标样式
     *
     * @see com.simonvonxcvii.turing.resource.server.enums.MenuBadgeVariantsEnum
     */
    var badgeVariants: String? = null,

    /**
     * 缓存标签页
     */
    var keepAlive: Boolean? = false,

    /**
     * 固定在标签
     */
    var affixTab: Boolean? = false,

    /**
     * 隐藏菜单
     */
    var hideInMenu: Boolean? = false,

    /**
     * 隐藏子菜单
     */
    var hideChildrenInMenu: Boolean? = false,

    /**
     * 在面包屑中隐藏
     */
    var hideInBreadcrumb: Boolean? = false,

    /**
     * 在标签栏中隐藏
     */
    var hideInTab: Boolean? = false,

    /**
     * 内嵌-链接地址
     */
    var iframeSrc: String? = null,

    /**
     * 外链-链接地址
     */
    var link: String? = null
) : Serializable

package com.simonvonxcvii.turing.model.dto

/**
 * Menu Meta DTO
 *
 * @author Simon Von
 * @since 12/30/2022 4:03 PM
 */
class MenuMetaDTO(
    /**
     * 菜单名称
     */
    var title: String? = null,

    /**
     * 菜单图标
     */
    var icon: String? = null,

    /**
     * 是否隐藏菜单
     */
    var hideMenu: Boolean = false
)

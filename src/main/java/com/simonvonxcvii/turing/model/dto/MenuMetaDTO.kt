package com.simonvonxcvii.turing.model.dto

import jakarta.validation.constraints.NotBlank

/**
 * Menu Meta DTO
 *
 * @author Simon Von
 * @since 12/30/2022 4:03 PM
 */
class MenuMetaDTO(
    /**
     * 标题
     */
    @field:NotBlank(message = "标题不能为空")
    var title: String = "",

    /**
     * 菜单图标
     */
    var icon: String? = null,

    /**
     * 是否隐藏菜单
     */
    var hideMenu: Boolean? = null
)

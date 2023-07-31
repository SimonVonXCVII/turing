package com.shiminfxcvii.turing.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * The MenuQuery
 *
 * @author ShiminFXCVII
 * @since 3/7/2023 8:43 PM
 */
@Schema(name = "MenuQuery")
@Getter
@Setter
public class MenuQuery {

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    private String name;

    /**
     * 菜单类型：目录、菜单、按钮
     */
    @Schema(description = "菜单类型：目录、菜单、按钮")
    private String type;

    /**
     * 菜单路径
     */
    @Schema(description = "菜单路径")
    private String path;

    /**
     * 组件路径
     */
    @Schema(description = "组件路径")
    private String component;

    /**
     * 菜单排序
     */
    @Schema(description = "菜单排序")
    private Integer sort;

    /**
     * 是否显示
     */
    @Schema(description = "是否显示")
    private Boolean show;

    /**
     * 是否缓存
     */
    @Schema(description = "是否缓存")
    private Boolean cache;

    /**
     * 是否外链
     */
    @Schema(description = "是否外链")
    private Boolean external;

}
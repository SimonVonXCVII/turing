package com.shiminfxcvii.turing.model.cmd;

import com.shiminfxcvii.turing.utils.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "MenuCmd")
@Getter
@Setter
public class MenuCmd {

    /**
     * 菜单 id
     */
    @Schema(description = "菜单 id")
    @NotNull(message = "菜单 id 不能为空", groups = Update.class)
    private String id;

    /**
     * 上级菜单 id
     */
    @Schema(description = "上级菜单 id")
    private String pid;

    /**
     * 系统权限 id
     */
    @Schema(description = "系统权限 id")
    @NotNull(message = "系统权限 id 不能为空")
    private String permissionId;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    @NotBlank(message = "菜单名称不能为空")
    private String name;

    /**
     * 菜单标题
     */
    @Schema(description = "菜单标题")
    @NotBlank(message = "菜单标题不能为空")
    private String title;

    /**
     * 菜单路径
     */
    @Schema(description = "菜单路径")
    @NotBlank(message = "菜单路径不能为空")
    private String path;

    /**
     * 菜单类型：目录、菜单、按钮
     */
    @Schema(description = "菜单类型：目录、菜单、按钮")
    @NotBlank(message = "菜单类型不能为空")
    private String type;

    /**
     * 组件路径
     */
    @Schema(description = "组件路径")
    @NotBlank(message = "组件路径不能为空")
    private String component;

    /**
     * 图标
     */
    @Schema(description = "图标")
    @NotBlank(message = "组件路径不能为空")
    private String icon;

    /**
     * 菜单排序
     */
    @Schema(description = "菜单排序")
    @NotNull(message = "菜单排序不能为空")
    private Integer sort;

    /**
     * 是否显示
     */
    @Schema(description = "是否显示")
    @NotNull(message = "是否显示不能为空")
    private Boolean show;

    /**
     * 是否缓存
     */
    @Schema(description = "是否缓存")
    @NotNull(message = "是否缓存不能为空")
    private Boolean cache;

    /**
     * 是否外链
     */
    @Schema(description = "是否外链")
    @NotNull(message = "是否外链不能为空")
    private Boolean external;

}
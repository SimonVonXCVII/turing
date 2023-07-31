package com.shiminfxcvii.turing.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Schema(name = "MenuDTO")
@Getter
@Setter
public class MenuDTO {

    /**
     * 菜单 id
     */
    @Schema(description = "菜单 id")
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
    private String permissionId;

    /**
     * 菜单名称
     */
    @Schema(description = "菜单名称")
    private String name;

    /**
     * 菜单标题
     */
    @Schema(description = "菜单标题")
    private String title;

    /**
     * 系统权限
     */
    @Schema(description = "系统权限")
    private String permission;

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
     * 图标
     */
    @Schema(description = "图标")
    private String icon;

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

    /**
     * 菜单元数据
     */
    @Schema(description = "菜单元数据")
    private MenuMetaDTO meta;

    /**
     * 子菜单
     */
    @Schema(description = "子菜单")
    private List<MenuDTO> children = new LinkedList<>();

}
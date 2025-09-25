package com.simonvonxcvii.turing.model.dto;

import com.simonvonxcvii.turing.model.query.PageQuery;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedList;
import java.util.List;

@Accessors(chain = true)
@Getter
@Setter
public class MenuDTO extends PageQuery {
    /**
     * 菜单 id
     */
    public Integer id;
    /**
     * 上级菜单 id
     */
    public Integer pid;
    /**
     * 菜单元数据
     */
    public MenuMetaDTO meta;
    /**
     * 子菜单
     */
    public List<MenuDTO> children = new LinkedList<>();
    /**
     * 系统权限 id
     */
    @NotBlank(message = "系统权限 id 不能为空")
    private Integer permissionId;
    /**
     * 菜单名称
     */
    @NotBlank(message = "菜单名称不能为空")
    private String name;
    /**
     * 菜单标题
     */
    @NotBlank(message = "菜单标题不能为空")
    private String title;
    /**
     * 系统权限
     */
    private String permission;
    /**
     * 菜单类型：目录、菜单、按钮
     */
    @NotBlank(message = "菜单类型不能为空")
    private String type;
    /**
     * 菜单路径
     */
    @NotBlank(message = "菜单路径不能为空")
    private String path;
    /**
     * 组件路径
     */
    @NotBlank(message = "组件路径不能为空")
    private String component;
    /**
     * 图标
     */
    private String icon;
    /**
     * 菜单排序
     */
    @NotNull(message = "菜单排序不能为空")
    private Short sort;
    /**
     * 是否显示
     */
    @NotNull(message = "是否显示不能为空")
    private Boolean showed;
    /**
     * 是否缓存
     */
    @NotNull(message = "是否缓存不能为空")
    private Boolean cached;
    /**
     * 是否外链
     */
    @NotNull(message = "是否外链不能为空")
    private Boolean external;
}

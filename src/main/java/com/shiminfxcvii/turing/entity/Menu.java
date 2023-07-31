package com.shiminfxcvii.turing.entity;

import com.shiting.soil.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 菜单表
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-26 18:25:51
 */
@Getter
@Setter
@Accessors(chain = true)
public class Menu extends BaseEntity {

    /**
     * 上级菜单 id
     */
    private String pid;
    /**
     * 系统权限 id
     */
    private String permissionId;
    /**
     * 菜单名称
     */
    private String name;
    /**
     * 标头
     */
    private String title;
    /**
     * 菜单类型：目录、菜单、按钮
     */
    private String type;
    /**
     * 菜单路径
     */
    private String path;
    /**
     * 组件路径
     */
    private String component;
    /**
     * 图标
     */
    private String icon;
    /**
     * 菜单排序
     */
    private Integer sort;
    /**
     * 是否缓存
     */
    private Boolean cache;
    /**
     * 是否显示
     */
    private Boolean show;
    /**
     * 是否外链
     */
    private Boolean external;

}
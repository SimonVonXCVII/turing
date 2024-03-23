package com.shiminfxcvii.turing.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

/**
 * <p>
 * 菜单表
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-26 18:25:51
 */
@Accessors(chain = true)
@Getter
@Setter
@ToString
@Entity
@Table(schema = "public", name = "turing_menu")
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_menu SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@Where(clause = "deleted = FALSE")
public class Menu extends AbstractAuditable {

    /**
     * ES 索引名称
     */
    public static final String ES_INDEX = "turing_menu";

    /**
     * Redis key 前缀
     */
    public static final String REDIS_KEY_PREFIX = ES_INDEX + ":";

    public static final String PID = "pid";
    public static final String PERMISSION_ID = "permissionId";
    public static final String NAME = "name";
    public static final String TITLE = "title";
    public static final String TYPE = "type";
    public static final String PATH = "path";
    public static final String COMPONENT = "component";
    public static final String ICON = "icon";
    public static final String SORT = "sort";
    public static final String SHOWED = "showed";
    public static final String CACHED = "cached";
    public static final String EXTERNAL = "external";

    /**
     * 上级菜单 id
     */
    public String pid;
    /**
     * 系统权限 id
     */
    public String permissionId;
    /**
     * 菜单名称
     */
    public String name;
    /**
     * 标头
     */
    public String title;
    /**
     * 菜单类型：目录、菜单、按钮
     */
    public String type;
    /**
     * 菜单路径
     */
    public String path;
    /**
     * 组件路径
     */
    public String component;
    /**
     * 图标
     */
    public String icon;
    /**
     * 排序编号
     */
    public int sort;
    /**
     * 是否显示
     */
    public boolean showed;
    /**
     * 是否缓存
     */
    public boolean cached;
    /**
     * 是否外链
     */
    public boolean external;
}

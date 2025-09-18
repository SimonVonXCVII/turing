package com.simonvonxcvii.turing.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import lombok.Getter
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

/**
 * 菜单表
 *
 * @author Simon Von
 * @since 2022-12-26 18:25:51
 */
@Getter
@Entity
@Table(
    schema = "public",
    name = "turing_menu",
    uniqueConstraints = [
        UniqueConstraint(name = "con_public_turing_menu_constraint_1", columnNames = arrayOf("id")),
        UniqueConstraint(columnNames = arrayOf("permission_id", "name", "title", "path", "sort"))
    ]
)
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_menu SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@SQLRestriction("deleted = FALSE")
data class Menu(
    /**
     * 上级菜单 id
     */
    @Column(name = "pid", columnDefinition = "INTEGER")
    @Comment("上级菜单 id")
    var pid: Int? = null,

    /**
     * 系统权限 id
     */
    @Column(name = "permission_id", nullable = false, columnDefinition = "INTEGER")
    @Comment("系统权限 id")
    var permissionId: Int = 0,

    /**
     * 菜单名称
     */
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR", length = 64)
    @Comment("菜单名称")
    var name: String = "",

    /**
     * 标头
     */
    @Column(name = "title", nullable = false, columnDefinition = "VARCHAR", length = 64)
    @Comment("标头")
    var title: String = "",

    /**
     * 菜单类型：目录、菜单、按钮
     */
    @Column(name = "type", nullable = false, columnDefinition = "VARCHAR", length = 32)
    @Comment("菜单类型：目录、菜单、按钮")
    var type: String = "",

    /**
     * 菜单路径
     */
    @Column(name = "path", nullable = false, columnDefinition = "VARCHAR", length = 128)
    @Comment("菜单路径")
    var path: String = "",

    /**
     * 组件路径
     */
    @Column(name = "component", nullable = false, columnDefinition = "VARCHAR", length = 128)
    @Comment("组件路径")
    var component: String = "",

    /**
     * 图标
     */
    @Column(name = "icon", columnDefinition = "VARCHAR", length = 128)
    @Comment("图标")
    var icon: String? = null,

    /**
     * 排序编号
     */
    @Column(name = "sort", nullable = false, columnDefinition = "SMALLINT")
    @Comment("排序编号")
    var sort: Short = 0,

    /**
     * 是否显示
     */
    @Column(name = "showed", nullable = false, columnDefinition = "BOOLEAN")
    @Comment("是否显示")
    @get:JvmName("isShowed")
    var showed: Boolean = true,

    /**
     * 是否缓存
     */
    @Column(name = "cached", nullable = false, columnDefinition = "BOOLEAN")
    @Comment("是否缓存")
    @get:JvmName("isCached")
    var cached: Boolean = true,

    /**
     * 是否外链
     */
    @Column(name = "external", nullable = false, columnDefinition = "BOOLEAN")
    @Comment("是否外链")
    @get:JvmName("isExternal")
    var external: Boolean = false
) : AbstractAuditable() {
    companion object {
        /**
         * ES 索引名称
         */
        const val ES_INDEX = "turing_menu"

        /**
         * Redis key 前缀
         */
        const val REDIS_KEY_PREFIX = "$ES_INDEX:"

        const val PID = "pid"
        const val PERMISSION_ID = "permissionId"
        const val NAME = "name"
        const val TITLE = "title"
        const val TYPE = "type"
        const val PATH = "path"
        const val COMPONENT = "component"
        const val ICON = "icon"
        const val SORT = "sort"
        const val SHOWED = "showed"
        const val CACHED = "cached"
        const val EXTERNAL = "external"
    }
}

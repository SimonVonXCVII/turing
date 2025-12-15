package com.simonvonxcvii.turing.entity

import com.simonvonxcvii.turing.enums.MenuTypeEnum
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

/**
 * 菜单表
 * Transient 属性的字段，建议添加到圆括号 () 中，而不是花括号 {} 中，因为可以存在于生成的 toString() 等方法中
 *
 * @author Simon Von
 * @since 2022-12-26 18:25:51
 */
@Entity
@Table(
    schema = "public",
    name = "turing_menu",
    uniqueConstraints = [
        UniqueConstraint(name = "con_public_turing_menu_constraint_1", columnNames = arrayOf("id"))
    ],
    comment = "菜单表"
)
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_menu SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@SQLRestriction("deleted = FALSE")
data class Menu(
    /**
     * 上级菜单 id
     */
    @Column(columnDefinition = "INTEGER", comment = "上级菜单 id")
    var pid: Int? = null,

    /**
     * 系统权限 id
     */
    @Column(unique = true, columnDefinition = "INTEGER", comment = "系统权限 id")
    var permissionId: Int? = 0,

    /**
     * 菜单名称
     */
    @Column(unique = true, nullable = false, columnDefinition = "VARCHAR(64)", comment = "菜单名称")
    var name: String = "",

    /**
     * 标头
     */
    @Column(unique = true, nullable = false, columnDefinition = "VARCHAR(64)", comment = "标头")
    var title: String = "",

    /**
     * 菜单类型：目录、菜单、按钮
     *
     * @see MenuTypeEnum
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(64)", comment = "菜单类型：目录、菜单、按钮")
    var type: MenuTypeEnum = MenuTypeEnum.MENU,

    /**
     * 权限标识
     */
    @Column(unique = true, columnDefinition = "VARCHAR(128)", comment = "权限标识")
    var authCode: String? = null,

    /**
     * 路由地址
     */
    @Column(unique = true, columnDefinition = "VARCHAR(128)", comment = "路由地址")
    var path: String? = null,

    /**
     * 页面组件
     */
    @Column(columnDefinition = "VARCHAR(128)", comment = "页面组件")
    var component: String? = null,

    /**
     * 状态
     */
    @Column(nullable = false, columnDefinition = "SMALLINT", comment = "状态")
    var status: Byte = 1,

    /**
     * 图标
     */
    @Column(columnDefinition = "VARCHAR(128)", comment = "图标")
    var icon: String? = null,

    /**
     * 排序编号
     */
    @Column(unique = true, columnDefinition = "INTEGER", comment = "排序编号")
    var sort: Int? = 0,

    /**
     * 是否显示
     */
    @Column(columnDefinition = "BOOLEAN", comment = "是否显示")
    @get:JvmName("isShowed")
    var showed: Boolean? = true,

    /**
     * 是否缓存
     */
    @Column(columnDefinition = "BOOLEAN", comment = "是否缓存")
    @get:JvmName("isCached")
    var cached: Boolean? = true,

    /**
     * 是否外链
     */
    @Column(columnDefinition = "BOOLEAN", comment = "是否外链")
    @get:JvmName("isExternal")
    var external: Boolean? = false
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
        const val AUTH_CODE = "authCode"
        const val PATH = "path"
        const val COMPONENT = "component"
        const val STATUS = "status"
        const val ICON = "icon"
        const val SORT = "sort"
        const val SHOWED = "showed"
        const val CACHED = "cached"
        const val EXTERNAL = "external"
    }
}

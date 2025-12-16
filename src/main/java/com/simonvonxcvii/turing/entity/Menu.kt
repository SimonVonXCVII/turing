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
     * 菜单类型：目录、菜单、按钮、内嵌、外链
     *
     * @see MenuTypeEnum
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(64)", comment = "菜单类型：目录、菜单、按钮、内嵌、外链")
    var type: MenuTypeEnum = MenuTypeEnum.MENU,

    /**
     * 菜单名称
     */
    @Column(unique = true, nullable = false, columnDefinition = "VARCHAR(64)", comment = "菜单名称")
    var name: String = "",

    /**
     * 上级菜单 id
     */
    @Column(columnDefinition = "INTEGER", comment = "上级菜单 id")
    var pid: Int? = null,

    /**
     * 路由地址
     */
    @Column(columnDefinition = "VARCHAR(128)", comment = "路由地址")
    var path: String? = null,

    /**
     * 激活路径
     */
    @Column(columnDefinition = "VARCHAR(128)", comment = "激活路径")
    var activePath: String? = null,

    /**
     * 页面组件
     */
    @Column(columnDefinition = "VARCHAR(128)", comment = "页面组件")
    var component: String? = null,

    /**
     * 权限标识
     */
    @Column(unique = true, columnDefinition = "VARCHAR(128)", comment = "权限标识")
    var authCode: String? = null,

    /**
     * 状态
     */
    @Column(nullable = false, columnDefinition = "SMALLINT", comment = "状态")
    var status: Byte = 1
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

        const val TYPE = "type"
        const val NAME = "name"
        const val PID = "pid"
        const val PATH = "path"
        const val ACTIVE_PATH = "activePath"
        const val COMPONENT = "component"
        const val AUTH_CODE = "authCode"
        const val STATUS = "status"
    }
}

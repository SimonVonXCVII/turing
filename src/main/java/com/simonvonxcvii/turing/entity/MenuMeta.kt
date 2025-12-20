package com.simonvonxcvii.turing.entity

import com.simonvonxcvii.turing.enums.MenuBadgeTypeEnum
import com.simonvonxcvii.turing.enums.MenuBadgeVariantsEnum
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

/**
 * 菜单元数据表
 * Transient 属性的字段，建议添加到圆括号 () 中，而不是花括号 {} 中，因为可以存在于生成的 toString() 等方法中
 *
 * @author Simon Von
 * @since 12/16/25 1:17 AM
 */
@Entity
@Table(
    schema = "public",
    name = "turing_menu_meta",
    uniqueConstraints = [
        UniqueConstraint(name = "con_public_turing_menu_meta_constraint_1", columnNames = arrayOf("id"))
    ],
    comment = "菜单元数据表"
)
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_menu_meta SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@SQLRestriction("deleted = FALSE")
class MenuMeta(
    /**
     * 标题
     */
    @Column(nullable = false, columnDefinition = "VARCHAR(64)", comment = "标题")
    var title: String = "",

    /**
     * 图标
     */
    @Column(columnDefinition = "VARCHAR(128)", comment = "图标")
    var icon: String? = null,

    /**
     * 激活图标
     */
    @Column(columnDefinition = "VARCHAR(128)", comment = "激活图标")
    var activeIcon: String? = null,

    /**
     * 徽标类型
     *
     * @see MenuBadgeTypeEnum
     */
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(8)", comment = "徽标类型")
    var badgeType: MenuBadgeTypeEnum? = null,

    /**
     * 徽章内容
     */
    @Column(columnDefinition = "VARCHAR(128)", comment = "徽章内容")
    var badge: String? = null,

    /**
     * 徽标样式
     *
     * @see MenuBadgeVariantsEnum
     */
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(16)", comment = "徽标样式")
    var badgeVariants: MenuBadgeVariantsEnum? = null,

    /**
     * 缓存标签页
     */
    @Column(columnDefinition = "BOOLEAN", comment = "缓存标签页")
    @get:JvmName("isKeepAlive")
    var keepAlive: Boolean? = false,

    /**
     * 固定在标签
     */
    @Column(columnDefinition = "BOOLEAN", comment = "固定在标签")
    @get:JvmName("isAffixTab")
    var affixTab: Boolean? = false,

    /**
     * 隐藏菜单
     */
    @Column(columnDefinition = "BOOLEAN", comment = "隐藏菜单")
    @get:JvmName("isHideInMenu")
    var hideInMenu: Boolean? = false,

    /**
     * 隐藏子菜单
     */
    @Column(columnDefinition = "BOOLEAN", comment = "隐藏子菜单")
    @get:JvmName("isHideChildrenInMenu")
    var hideChildrenInMenu: Boolean? = false,

    /**
     * 在面包屑中隐藏
     */
    @Column(columnDefinition = "BOOLEAN", comment = "隐藏子菜单")
    @get:JvmName("isHideInBreadcrumb")
    var hideInBreadcrumb: Boolean? = false,

    /**
     * 在标签栏中隐藏
     */
    @Column(columnDefinition = "BOOLEAN", comment = "在标签栏中隐藏")
    @get:JvmName("isHideInTab")
    var hideInTab: Boolean? = false,

    /**
     * 内嵌-链接地址
     */
    @Column(columnDefinition = "VARCHAR(128)", comment = "内嵌-链接地址")
    var iframeSrc: String? = null,

    /**
     * 外链-链接地址
     */
    @Column(columnDefinition = "VARCHAR(128)", comment = "外链-链接地址")
    var link: String? = null
) : AbstractAuditable() {
    /**
     * 菜单
     */
    @OneToOne(mappedBy = "meta")
    var menu: Menu? = null

    companion object {
        /**
         * ES 索引名称
         */
        const val ES_INDEX = "turing_menu_meta"

        /**
         * Redis key 前缀
         */
        const val REDIS_KEY_PREFIX = "$ES_INDEX:"

        const val TITLE = "title"
        const val ICON = "icon"
        const val ACTIVE_ICON = "activeIcon"
        const val BADGE_TYPE = "badgeType"
        const val BADGE = "badge"
        const val BADGE_VARIANTS = "badgeVariants"
        const val KEEP_ALIVE = "keepAlive"
        const val AFFIX_TAB = "affixTab"
        const val HIDE_IN_MENU = "hideInMenu"
        const val HIDE_CHILDREN_IN_MENU = "hideChildrenInMenu"
        const val HIDE_IN_BREADCRUMB = "hideInBreadcrumb"
        const val HIDE_IN_TAB = "hideInTab"
        const val IFRAME_SRC = "iframeSrc"
        const val LINK = "link"
    }
}

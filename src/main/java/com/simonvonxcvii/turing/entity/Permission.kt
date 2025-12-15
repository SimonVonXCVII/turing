package com.simonvonxcvii.turing.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

/**
 * 权限表
 * Transient 属性的字段，建议添加到圆括号 () 中，而不是花括号 {} 中，因为可以存在于生成的 toString() 等方法中
 *
 * @author Simon Von
 * @since 2022-12-22 16:22:49
 */
@Entity
@Table(
    schema = "public",
    name = "turing_permission",
    uniqueConstraints = [
        UniqueConstraint(name = "con_public_turing_permission_constraint_1", columnNames = arrayOf("id"))
    ],
    comment = "权限表"
)
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_permission SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@SQLRestriction("deleted = FALSE")
data class Permission(
    /**
     * 上级权限 id
     */
    @Column(columnDefinition = "INTEGER", comment = "上级权限 id")
    var pid: Int? = null,

    /**
     * 权限名称
     */
    @Column(unique = true, nullable = false, columnDefinition = "VARCHAR(32)", comment = "权限名称")
    var name: String = "",

    /**
     * 权限编码
     */
    @Column(unique = true, columnDefinition = "VARCHAR(32)", comment = "权限编码")
    var code: String? = null,

    /**
     * 排序编号
     */
    @Column(unique = true, nullable = false, columnDefinition = "INTEGER", comment = "排序编号")
    var sort: Int = 0,
) : AbstractAuditable() {
    companion object {
        /**
         * ES 索引名称
         */
        const val ES_INDEX = "turing_permission"

        /**
         * Redis key 前缀
         */
        const val REDIS_KEY_PREFIX = "$ES_INDEX:"

        const val PID = "pid"
        const val NAME = "name"
        const val CODE = "code"
        const val SORT = "sort"
    }
}

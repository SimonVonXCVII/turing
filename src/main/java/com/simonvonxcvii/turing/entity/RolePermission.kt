package com.simonvonxcvii.turing.entity

import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

/**
 * 角色与权限关联记录表
 * Transient 属性的字段，建议添加到圆括号 () 中，而不是花括号 {} 中，因为可以存在于生成的 toString() 等方法中
 *
 * @author Simon Von
 * @since 2022-12-22 16:22:50
 */
@Entity
@Table(
    schema = "public",
    name = "turing_role_permission",
    uniqueConstraints = [
        UniqueConstraint(name = "con_public_turing_role_permission_constraint_1", columnNames = arrayOf("id"))
    ],
    comment = "角色与权限关联记录表"
)
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_role_permission SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@SQLRestriction("deleted = FALSE")
class RolePermission(
    /**
     * 角色 id
     * ⚠️注意：这将创建外键
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id", nullable = false, comment = "角色 id")
    var role: Role = Role(),

    /**
     * 权限 id
     * ⚠️注意：这将创建外键
     */
    @ManyToOne(optional = false)
    @JoinColumn(name = "permission_id", nullable = false, comment = "权限 id")
    var permission: Permission = Permission()
) : AbstractAuditable() {
    companion object {
        /**
         * ES 索引名称
         */
        const val ES_INDEX = "turing_role_permission"

        /**
         * Redis key 前缀
         */
        const val REDIS_KEY_PREFIX = "$ES_INDEX:"

        const val ROLE_ID = "roleId"
        const val PERMISSION_ID = "permissionId"
    }
}

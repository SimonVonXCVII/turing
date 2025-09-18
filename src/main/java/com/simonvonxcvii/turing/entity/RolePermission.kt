package com.simonvonxcvii.turing.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

/**
 * 角色与权限关联记录表
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
    ]
)
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_role_permission SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@SQLRestriction("deleted = FALSE")
data class RolePermission(
    /**
     * 角色 id
     */
    @Column(name = "role_id", nullable = false, columnDefinition = "INTEGER")
    @Comment("角色 id")
    var roleId: Int = 0,

    /**
     * 权限 id
     */
    @Column(name = "permission_id", nullable = false, columnDefinition = "INTEGER")
    @Comment("权限 id")
    var permissionId: Int = 0
) : AbstractAuditable() {
    companion object {
        /**
         * ES 索引名称
         */
        const val ES_INDEX: String = "turing_role_permission"

        /**
         * Redis key 前缀
         */
        const val REDIS_KEY_PREFIX: String = "$ES_INDEX:"

        const val ROLE_ID: String = "roleId"
        const val PERMISSION_ID: String = "permissionId"
    }
}

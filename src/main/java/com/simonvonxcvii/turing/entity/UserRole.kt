package com.simonvonxcvii.turing.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

/**
 * 角色与用户关联记录表
 * Transient 属性的字段，建议添加到圆括号 () 中，而不是花括号 {} 中，因为可以存在于生成的 toString() 等方法中
 *
 * @author Simon Von
 * @since 2022-12-22 16:22:50
 */
@Entity
@Table(
    schema = "public",
    name = "turing_user_role",
    uniqueConstraints = [
        UniqueConstraint(name = "con_public_turing_user_role_constraint_1", columnNames = arrayOf("id"))
    ]
)
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_user_role SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@SQLRestriction("deleted = FALSE")
data class UserRole(
    /**
     * 用户 id
     */
    @Column(nullable = false, columnDefinition = "INTEGER")
    @Comment("用户 id")
    var userId: Int = 0,

    /**
     * 角色 id
     */
    @Column(nullable = false, columnDefinition = "INTEGER")
    @Comment("角色 id")
    var roleId: Int = 0
) : AbstractAuditable() {
    companion object {
        /**
         * ES 索引名称
         */
        const val ES_INDEX = "turing_user_role"

        /**
         * Redis key 前缀
         */
        const val REDIS_KEY_PREFIX = "$ES_INDEX:"

        const val USER_ID = "userId"
        const val ROLE_ID = "roleId"
    }
}

package com.simonvonxcvii.turing.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.springframework.security.core.GrantedAuthority

/**
 * 角色表
 * Transient 属性的字段，建议添加到圆括号 () 中，而不是花括号 {} 中，因为可以存在于生成的 toString() 等方法中
 *
 * @author Simon Von
 * @since 2022-12-22 16:22:50
 */
@Entity
@Table(
    schema = "public",
    name = "turing_role",
    uniqueConstraints = [
        UniqueConstraint(name = "con_public_turing_role_constraint_1", columnNames = arrayOf("id"))
    ]
)
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_role SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@SQLRestriction("deleted = FALSE")
data class Role(
    /**
     * 角色编码
     */
    @Column(unique = true, nullable = false, columnDefinition = "VARCHAR(64)")
    @Comment("角色编码")
    @get:JvmName("getAuthorityValue")
    var authority: String = "",

    /**
     * 角色名称
     */
    @Column(unique = true, nullable = false, columnDefinition = "VARCHAR(64)")
    @Comment("角色名称")
    var name: String = "",

    /**
     * 角色说明
     */
    @Column(columnDefinition = "VARCHAR(128)")
    @Comment("角色说明")
    var description: String? = null
) : AbstractAuditable(), GrantedAuthority {
    override fun getAuthority(): String {
        return authority
    }

    companion object {
        /**
         * ES 索引名称
         */
        const val ES_INDEX: String = "turing_role"

        /**
         * Redis key 前缀
         */
        const val REDIS_KEY_PREFIX: String = "$ES_INDEX:"

        const val NAME: String = "name"
        const val AUTHORITY: String = "authority"
        const val DESCRIPTION: String = "description"
    }
}

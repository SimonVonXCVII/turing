package com.simonvonxcvii.turing.entity

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * 用户表
 * Transient 属性的字段，建议添加到圆括号 () 中，而不是花括号 {} 中，因为可以存在于生成的 toString() 等方法中
 *
 * @author Simon Von
 * @since 2022-12-19 15:58:28
 */
@Entity
@Table(
    schema = "public",
    name = "turing_user",
    uniqueConstraints = [
        UniqueConstraint(name = "con_public_turing_user_constraint_1", columnNames = arrayOf("id")),
        UniqueConstraint(columnNames = arrayOf("username"))
    ]
)
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_user SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
//@SQLDeleteAll()
@SQLRestriction("deleted = FALSE")
//@RedisHash
//@Document(indexName = "turing_user")
data class User(
    /**
     * 用户姓名
     */
    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR", length = 64)
    @Comment("用户姓名")
    var name: String = "",

    /**
     * 用户电话
     */
    @Column(name = "mobile", nullable = false, columnDefinition = "BIGINT")
    @Comment("用户电话")
    var mobile: Long = 0,

    /**
     * 用户性别
     */
    @Column(name = "gender", nullable = false, columnDefinition = "VARCHAR", length = 1)
    @Comment("用户性别")
    var gender: String = "",

    /**
     * 组织机构 id
     */
    @Column(name = "org_id", nullable = false, columnDefinition = "INTEGER")
    @Comment("组织机构 id")
    var orgId: Int = 0,

    /**
     * 组织机构名称
     */
    @Column(name = "org_name", nullable = false, columnDefinition = "VARCHAR", length = 128)
    @Comment("组织机构名称")
    var orgName: String = "",

    /**
     * 部门
     */
    @Column(name = "department", columnDefinition = "VARCHAR", length = 128)
    @Comment("部门")
    var department: String? = null,

    /**
     * 登录账号
     */
    @Column(name = "username", nullable = false, columnDefinition = "VARCHAR", length = 64)
    @Comment("登录账号")
    @get:JvmName("getUsernameValue")
    var username: String = "",

    /**
     * 用户密码
     */
    @Column(name = "password", nullable = false, columnDefinition = "VARCHAR", length = 128)
    @Comment("用户密码")
    @get:JvmName("getPasswordValue")
    var password: String = "",

    /**
     * 是否账号没有过期
     */
    @Column(name = "account_non_expired", nullable = false, columnDefinition = "BOOLEAN")
    @Comment("是否账号没有过期")
    var accountNonExpired: Boolean = true,

    /**
     * 是否账号没有锁定
     */
    @Column(name = "account_non_locked", nullable = false, columnDefinition = "BOOLEAN")
    @Comment("是否账号没有锁定")
    var accountNonLocked: Boolean = true,

    /**
     * 是否凭证没有过期
     */
    @Column(name = "credentials_non_expired", nullable = false, columnDefinition = "BOOLEAN")
    @Comment("是否凭证没有过期")
    var credentialsNonExpired: Boolean = true,

    /**
     * 是否启用
     */
    @Column(name = "enabled", nullable = false, columnDefinition = "BOOLEAN")
    @Comment("是否启用")
    var enabled: Boolean = true,

    /**
     * 是否单位管理员
     */
    @Column(name = "manager", nullable = false, columnDefinition = "BOOLEAN")
    @Comment("是否单位管理员")
    @get:JvmName("isManager")
    var manager: Boolean = false,

    /**
     * 是否需要重置密码
     */
    @Column(name = "need_reset_password", nullable = false, columnDefinition = "BOOLEAN")
    @Comment("是否需要重置密码")
    @get:JvmName("isNeedResetPassword")
    var needResetPassword: Boolean = true,

    /**
     * 用户角色
     */
    @Transient
    @get:JvmName("getAuthoritiesValue")
    var authorities: Collection<Role> = mutableListOf(),

    /**
     * 是否是超级管理员
     */
    @Transient
    @get:JvmName("isAdmin")
    var admin: Boolean = false,

    /**
     * 当前用户的 token
     *
     * @since 2023/4/11 18:07
     */
    @Transient
    var token: String? = null,

    /**
     * 用户所处的单位级别
     *
     * @since 2023/7/1 18:53
     */
    @Transient
    var orgLevel: String? = null,

    /**
     * 省（市、区）编码
     *
     * @since 2023/4/11 18:07
     */
    @Transient
    var provinceCode: Int? = null,

    /**
     * 市（州、盟）编码
     *
     * @since 2023/7/1 18:53
     */
    @Transient
    var cityCode: Int? = null,

    /**
     * 县（市、旗）编码
     *
     * @since 2023/7/1 18:53
     */
    @Transient
    var districtCode: Int? = null,

    /**
     * 省（市、区）名称
     *
     * @since 2023/7/1 18:53
     */
    @Transient
    var provinceName: String? = null,

    /**
     * 市（州、盟）名称
     *
     * @since 2023/7/1 18:53
     */
    @Transient
    var cityName: String? = null,

    /**
     * 县（市、旗）名称
     *
     * @since 2023/7/1 18:53
     */
    @Transient
    var districtName: String? = null
) : AbstractAuditable(), UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        return accountNonExpired
    }

    override fun isAccountNonLocked(): Boolean {
        return accountNonLocked
    }

    override fun isCredentialsNonExpired(): Boolean {
        return credentialsNonExpired
    }

    override fun isEnabled(): Boolean {
        return enabled
    }

    companion object {
        /**
         * ES 索引名称
         */
        const val ES_INDEX = "turing_user"

        /**
         * Redis key 前缀
         */
        const val REDIS_KEY_PREFIX = "$ES_INDEX:"

        const val NAME = "name"
        const val MOBILE = "mobile"
        const val GENDER = "gender"
        const val ORG_ID = "orgId"
        const val ORG_NAME = "orgName"
        const val DEPARTMENT = "department"
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val ACCOUNT_NON_EXPIRED = "accountNonExpired"
        const val ACCOUNT_NON_LOCKED = "accountNonLocked"
        const val CREDENTIALS_NON_EXPIRED = "credentialsNonExpired"
        const val ENABLED = "enabled"
        const val MANAGER = "manager"
        const val NEED_RESET_PASSWORD = "needResetPassword"
    }
}

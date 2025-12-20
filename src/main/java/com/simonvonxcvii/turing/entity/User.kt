package com.simonvonxcvii.turing.entity

import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * 用户表
 * Transient 属性的字段，建议添加到圆括号 () 中，而不是花括号 {} 中，因为可以存在于生成的 toString() 等方法中
 * @jakarta.persistence.Transient 是最佳选择：
 * 作用：
 * 告诉 JPA 提供者（Hibernate/EclipseLink 等）：这个属性不是持久化字段，不要映射到数据库表，也不要生成列。
 * 在 Spring Boot JPA 项目中这是标准的做法。
 * 适用场景：
 * JPA 实体类中某个属性只是运行期的临时值，或者是计算值，不需要入库。
 *
 * @author Simon Von
 * @since 2022-12-19 15:58:28
 */
@Entity
@Table(
    schema = "public",
    name = "turing_user",
    uniqueConstraints = [
        UniqueConstraint(name = "con_public_turing_user_constraint_1", columnNames = arrayOf("id"))
    ],
    comment = "用户表"
)
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_user SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
//@SQLDeleteAll()
@SQLRestriction("deleted = FALSE")
//@RedisHash
//@Document(indexName = "turing_user")
class User(
    /**
     * 用户姓名
     */
    @Column(nullable = false, columnDefinition = "VARCHAR(64)", comment = "用户姓名")
    var realName: String = "",

    /**
     * 用户电话
     */
    @Column(nullable = false, columnDefinition = "BIGINT", comment = "用户电话")
    var mobile: Long = 0,

    /**
     * 用户性别
     */
    @Column(nullable = false, columnDefinition = "VARCHAR(1)", comment = "用户性别")
    var gender: String = "",

    /**
     * 组织机构 id
     */
    @Column(nullable = false, columnDefinition = "INTEGER", comment = "组织机构 id")
    var orgId: Int = 0,

    /**
     * 组织机构名称
     */
    @Column(nullable = false, columnDefinition = "VARCHAR(128)", comment = "组织机构名称")
    var orgName: String = "",

    /**
     * 部门
     */
    @Column(columnDefinition = "VARCHAR(128)", comment = "部门")
    var department: String? = null,

    /**
     * 登录账号
     */
    @Column(unique = true, nullable = false, columnDefinition = "VARCHAR(64)", comment = "登录账号")
    @get:JvmName("getUsernameValue")
    var username: String = "",

    /**
     * 用户密码
     */
    @Column(nullable = false, columnDefinition = "VARCHAR(128)", comment = "用户密码")
    @get:JvmName("getPasswordValue")
    var password: String = "",

    /**
     * 是否账号没有过期
     */
    @Column(nullable = false, columnDefinition = "BOOLEAN", comment = "是否账号没有过期")
    var accountNonExpired: Boolean = true,

    /**
     * 是否账号没有锁定
     */
    @Column(nullable = false, columnDefinition = "BOOLEAN", comment = "是否账号没有锁定")
    var accountNonLocked: Boolean = true,

    /**
     * 是否凭证没有过期
     */
    @Column(nullable = false, columnDefinition = "BOOLEAN", comment = "是否凭证没有过期")
    var credentialsNonExpired: Boolean = true,

    /**
     * 是否启用
     */
    @Column(nullable = false, columnDefinition = "BOOLEAN", comment = "是否启用")
    var enabled: Boolean = true,

    /**
     * 是否单位管理员
     */
    @Column(nullable = false, columnDefinition = "BOOLEAN", comment = "是否单位管理员")
    @get:JvmName("isManager")
    var manager: Boolean = false,

    /**
     * 是否需要重置密码
     */
    @Column(nullable = false, columnDefinition = "BOOLEAN", comment = "是否需要重置密码")
    @get:JvmName("isNeedResetPassword")
    var needResetPassword: Boolean = true
) : AbstractAuditable(), UserDetails {
    /**
     * 用户角色关联表
     */
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "user", orphanRemoval = true)
    var userRoles: MutableList<UserRole> = mutableListOf()

    /**
     * 用户角色
     */
    @Transient
    @get:JvmName("getAuthoritiesValue")
    var authorities: Collection<Role> = mutableListOf()

    /**
     * 用户角色编码
     */
    @Transient
    var roles: Set<String> = mutableSetOf()

    /**
     * 用户权限码
     */
    @Transient
    var codes: Set<String> = mutableSetOf()

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

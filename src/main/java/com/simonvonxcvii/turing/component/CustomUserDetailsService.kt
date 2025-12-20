package com.simonvonxcvii.turing.component

import com.simonvonxcvii.turing.entity.*
import com.simonvonxcvii.turing.repository.jpa.UserJpaRepository
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.StringUtils

/**
 * 该类用于验证在有账号登录时是否与数据库账号匹配
 * todo 该类的最关键或者唯一的作用，应该只是根据传来的 username 去数据库查询 user 数据，并包装成 UserDetails 返回
 *  以前，是自己签 token + 自己校验，所以需要在该类中使用 user.id, username 去创建 token
 *  现在，因为使用 OIDC，所以不再需要自己创建 token 了
 *
 * @author Simon Von
 * @since 2022/12/19 15:31
 */
@Component
class CustomUserDetailsService(
    private val redisTemplate: RedisTemplate<Any, Any>,
    private val userJpaRepository: UserJpaRepository,
) : UserDetailsService {
    /**
     * 根据用户名定位用户。在实际实现中，搜索可能区分大小写，也可能不区分大小写，具体取决于实现实例的配置方式。
     * 在这种情况下，返回的 UserDetails 对象的用户名可能与实际请求的用户名大小写不同。
     *
     * 注意：流程是 AbstractUserDetailsAuthenticationProvider
     *  1. 调用其 authenticate(Authentication authentication)
     *   A. 调用其 retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
     *    a. 该方法的实现会调用 UserDetailsService.loadUserByUsername(String username) 返回 UserDetails
     *   B. 调用其 additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
     *    b. 该方法的实现会调用 PasswordEncoder.matches(CharSequence rawPassword, String encodedPassword) 对比密码是否匹配
     *  所以是先验证 username 后验证 password
     *
     * @param username 用于标识所需数据的用户的用户名。
     * @return 完整填充的用户记录（永不为 null）
     * @throws UsernameNotFoundException 如果找不到用户或用户没有授权权限
     * @see org.springframework.security.authentication.dao.DaoAuthenticationProvider.additionalAuthenticationChecks
     */
    @Transactional(readOnly = true)
    override fun loadUserByUsername(username: String): UserDetails {
        // todo 可以学习源码的其他实现中，先从缓存中获取。其他 Custom 类也可以尝试
        //  在 CustomLogoutSuccessHandler 中可以尝试不删除 redis 中的 user，删除规则可以尝试向 keycloak 颁发的 token 有效期看齐
        if (!StringUtils.hasText(username)) {
            throw UsernameNotFoundException("用户账号不能为空")
        }
        // 获取用户数据
        val user = userJpaRepository.findOneByUsername(username)
            .orElse(null) ?: throw UsernameNotFoundException("该用户账号不存在：$username")

        if (!user.isAccountNonExpired) throw AccountExpiredException("账号已过期")
        if (!user.isAccountNonLocked) throw LockedException("账号已锁定")
        if (!user.isCredentialsNonExpired) throw CredentialsExpiredException("凭证已过期")
        if (!user.isEnabled) throw DisabledException("账号已禁用")

        // 获取用户角色数据
        val roleList = user.userRoles.map(UserRole::role).toList()

        // 缓存当前用户的角色集合
        user.authorities = roleList
        // 缓存当前用户的角色名称集合
        user.roles = roleList.map(Role::name).toSet()
        // 缓存当前用户的权限编码集合
        user.codes = roleList.asSequence()
            .mapNotNull(Role::rolePermissions)
            .flatten()
            .mapNotNull(RolePermission::permission)
            .mapNotNull(Permission::code)
            .toSet()

        // 缓存用户信息 TODO 是否该用 putIfAbsent
        redisTemplate.opsForHash<String, User>().put(User.REDIS_KEY_PREFIX, username, user)
        return user
    }
}

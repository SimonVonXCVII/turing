package com.simonvonxcvii.turing.component

import com.simonvonxcvii.turing.entity.User
import com.simonvonxcvii.turing.entity.UserRole
import com.simonvonxcvii.turing.repository.jpa.OrganizationJpaRepository
import com.simonvonxcvii.turing.repository.jpa.RoleJpaRepository
import com.simonvonxcvii.turing.repository.jpa.UserJpaRepository
import com.simonvonxcvii.turing.repository.jpa.UserRoleJpaRepository
import com.simonvonxcvii.turing.service.NimbusJwtService
import com.simonvonxcvii.turing.utils.Constants
import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.security.authentication.*
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils

/**
 * 该类用于验证在有账号登录时是否与数据库账号匹配
 *
 * @author Simon Von
 * @since 2022/12/19 15:31
 */
@Component
class CustomUserDetailsService(
    private val nimbusJwtService: NimbusJwtService,
    private val httpServletRequest: HttpServletRequest,
    private val redisTemplate: RedisTemplate<Any, Any>,
    private val stringRedisTemplate: StringRedisTemplate,
    private val userJpaRepository: UserJpaRepository,
    private val roleJpaRepository: RoleJpaRepository,
    private val userRoleJpaRepository: UserRoleJpaRepository,
    private val organizationJpaRepository: OrganizationJpaRepository,
) : UserDetailsService {
    /**
     * 根据用户名定位用户。在实际实现中，搜索可能区分大小写或不区分大小写，具体取决于实现实例的配置方式。
     * 在这种情况下，返回的 UserDetails 对象的用户名可能与实际请求的用户名不同。
     * 根据用户账号获取用户信息，并将账号、密码、角色、是否锁定、是否禁用赋予 UserDetails
     * 只校验账号是否存在，不校验密码是否正确，并将认证信息保存到安全上下文中。
     *
     * @param username 标识需要其数据的用户的用户名。
     * @return 完全填充的用户记录 (never `null`)
     * @throws UsernameNotFoundException 如果找不到用户或用户没有授权权限
     * @see org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider
     */
    override fun loadUserByUsername(username: String): UserDetails {
        if (!StringUtils.hasText(username)) {
            throw UsernameNotFoundException("用户账号不能为空")
        }
        // 获取用户数据
        val spec = Specification<User> { root, query, builder ->
            val usernamePredicate = builder.equal(root.get<String>(User.USERNAME), username)
            query?.where(usernamePredicate)?.restriction
        }
        val user = userJpaRepository.findOne(spec)
            .orElse(null) ?: throw UsernameNotFoundException("该用户账号不存在：$username")

        if (!user.isAccountNonExpired) throw AccountExpiredException("账号已过期")
        if (!user.isAccountNonLocked) throw LockedException("账号已锁定")
        if (!user.isCredentialsNonExpired) throw CredentialsExpiredException("凭证已过期")
        if (!user.isEnabled) throw DisabledException("账号已禁用")

        user.admin = "admin" == user.username
        // 超级管理员拥有所有角色和权限
        val roleList = if (user.admin) {
            roleJpaRepository.findAll().filterNotNull()
        } else {
            // 获取用户角色与用户关联记录表
            val spec = Specification<UserRole> { root, query, builder ->
                val userId = builder.equal(root.get<String>(UserRole.USER_ID), user.id)
                query?.where(userId)?.restriction
            }
            val userRoleList = userRoleJpaRepository.findAll(spec).filterNotNull()
                .apply {
                    if (this.isEmpty()) throw BadCredentialsException("非法账号，该账号没有角色：$username")
                }

            // 获取用户角色
            val userRoleIdList = userRoleList.stream().map { userRole -> userRole.roleId }.toList()
            roleJpaRepository.findAllById(userRoleIdList)
                .filterNotNull()
                .apply {
                    if (this.isEmpty()) throw BadCredentialsException("非法账号，该账号没有角色：$username")
                }
        }

        // 缓存用户其他信息到实体类
        // 缓存当前用户的角色集合
        user.authorities = roleList
        // 缓存当前用户的 token
        user.token = nimbusJwtService.encode(user.id, username).tokenValue
        // 将 token 保存到 request 中，便于在 AuthenticationSuccessHandlerImpl#onAuthenticationSuccess 方法中获取
        httpServletRequest.setAttribute(OAuth2ParameterNames.TOKEN, user.token)
        val organization = organizationJpaRepository.findById(user.orgId)
            .orElse(null) ?: throw BadCredentialsException("无法找到当前用户的单位信息")
        // 用户所处的单位级别
//         user.orgLevel = organization.orgLevel
        // 区域编码
        user.provinceCode = organization.provinceCode
        user.cityCode = organization.cityCode
        user.districtCode = organization.districtCode
        // 区域名称
        user.provinceName = organization.provinceName
        user.cityName = organization.cityName
        user.districtName = organization.districtName

        // 获取 md5DigestAsHex
        val md5DigestAsHex: String = httpServletRequest.getAttribute(Constants.HEX_DIGEST) as String
        // 删除属性（摘要字符串）
        httpServletRequest.removeAttribute(md5DigestAsHex)
        // 验证成功，删除 Redis 中的验证码
        stringRedisTemplate.opsForValue().getAndDelete(Constants.REDIS_CAPTCHA + md5DigestAsHex)
        // 缓存用户信息 TODO 是否该用 putIfAbsent
        redisTemplate.opsForHash<String, User>().put(User.REDIS_KEY_PREFIX, username, user)
        return user
    }
}

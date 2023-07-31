package com.shiminfxcvii.turing.component

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.shiminfxcvii.turing.entity.Role
import com.shiminfxcvii.turing.entity.User
import com.shiminfxcvii.turing.entity.UserRole
import com.shiminfxcvii.turing.mapper.RoleMapper
import com.shiminfxcvii.turing.mapper.UserMapper
import com.shiminfxcvii.turing.mapper.UserRoleMapper
import com.shiminfxcvii.turing.service.NimbusJwtService
import com.shiminfxcvii.turing.utils.Constants
import jakarta.servlet.http.HttpServletRequest
import org.apache.tomcat.util.net.IPv6Utils
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.*
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.stereotype.Component
import org.springframework.util.DigestUtils
import org.springframework.util.StringUtils
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * 该类用于验证在有账号登录时是否与数据库账号匹配
 *
 * @author ShiminFXCVII
 * @since 2022/12/19 15:31
 */
@Component
class UserDetailsServiceImpl(
    private val userMapper: UserMapper,
    private val userRoleMapper: UserRoleMapper,
    private val roleMapper: RoleMapper,
    private val nimbusJwtService: NimbusJwtService,
    private val httpServletRequest: HttpServletRequest,
    private val redisTemplate: RedisTemplate<String, Any>
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
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        if (!StringUtils.hasText(username)) {
            throw UsernameNotFoundException("用户账号不能为空")
        }
        // 获取用户数据
        val user = userMapper.selectOne(KtQueryWrapper(User()).eq(User::username, username))
            ?: throw UsernameNotFoundException("该用户账号不存在：$username")
        if (user.disabled) {
            throw DisabledException("账号已禁用")
        }
        if (user.locked) {
            throw LockedException("账号已锁定")
        }
        val roleList: List<Role>
        // 超级管理员拥有所有角色和权限
        if (user.admin) {
            roleList = roleMapper.selectList(null)
        } else {
            // 获取用户角色与用户关联记录表
            val userRoleList = userRoleMapper.selectList(KtQueryWrapper(UserRole()).eq(UserRole::userId, user.id))
            if (userRoleList.isEmpty()) {
                throw AuthenticationCredentialsNotFoundException("非法账号，该账号没有角色：$username")
            }

            // 获取用户角色
            roleList =
                roleMapper.selectBatchIds(userRoleList.stream().map { userRole: UserRole -> userRole.roleId }.toList())
            if (roleList.isEmpty()) {
                throw AuthenticationCredentialsNotFoundException("非法账号，该账号没有角色：$username")
            }
        }

        // 校验验证码
        // 使用 md5 这种方式作为 key 的原因是 session id 总是会改变，同一个客户端的浏览器发送的请求的 session id 无法保持一致
        val ipAddr = IPv6Utils.canonize(httpServletRequest.remoteAddr)
        val userAgent = httpServletRequest.getHeader(HttpHeaders.USER_AGENT)
        val md5DigestAsHex = DigestUtils.md5DigestAsHex((ipAddr + userAgent).toByteArray(StandardCharsets.UTF_8))
        // 服务端验证码
        val serverCaptcha = redisTemplate.opsForValue()[Constants.REDIS_CAPTCHA + md5DigestAsHex] as String?
        // 客户端验证码
        val clientCaptcha = httpServletRequest.getParameter("captcha")
        if (!StringUtils.hasText(serverCaptcha)) {
            throw AuthenticationServiceException("验证码已过期")
        }
        if (!StringUtils.hasText(clientCaptcha)) {
            throw AuthenticationServiceException("请输入验证码")
        }
        if (!serverCaptcha.equals(clientCaptcha, ignoreCase = true)) {
            throw BadCredentialsException("验证码错误，请重新输入")
        }
        // 如果验证成功，则删除验证码
        redisTemplate.opsForValue().getAndDelete(Constants.REDIS_CAPTCHA + md5DigestAsHex)

        // 缓存用户其他信息到实体类
        // 缓存当前用户的 token
        user.token = nimbusJwtService.encode(user.id, username).tokenValue
        // 将 token 保存到 request 中，便于在 AuthenticationSuccessHandlerImpl#onAuthenticationSuccess 方法中获取
        httpServletRequest.setAttribute(OAuth2ParameterNames.TOKEN, user.token)
        // 缓存当前用户的角色集合
        user.roleList = roleList
        // 如果当前登录用户是行政管理员则缓存所在单位对应的省份编码
        roleList.stream()
            .filter { obj: Role? -> Objects.nonNull(obj) }
            .map { obj: Role -> obj.code }
            .filter { obj: String? -> Objects.nonNull(obj) }
            .filter { code: String -> code.endsWith("_GOV") }
            .findAny()
            .ifPresent {
//                val businessManageOrganization = businessManageOrganizationMapper.selectById(user.orgId)
//                if (businessManageOrganization != null) {
//                    // TODO: 2023/7/1 业务单位人员有单位名称？
//                    user.orgName = businessManageOrganization.name
//                    // 用户所处的单位级别
//                    user.orgLevel = businessManageOrganization.orgLevel
//                    // 区域编码
//                    user.provinceCode = businessManageOrganization.provinceCode.toInt()
//                    if (businessManageOrganization.cityCode != null) {
//                        user.cityCode = businessManageOrganization.cityCode.toInt()
//                    }
//                    if (businessManageOrganization.districtCode != null) {
//                        user.districtCode = businessManageOrganization.districtCode.toInt()
//                    }
//                    // 区域名称
//                    user.provinceName = businessManageOrganization.provinceName
//                    user.cityName = businessManageOrganization.cityName
//                    user.districtName = businessManageOrganization.districtName
//                }
            }

        // 缓存用户信息。因为序列化问题，没有保存 userDetails
        redisTemplate.opsForValue()[User.REDIS_KEY_PREFIX + username] = user
        return org.springframework.security.core.userdetails.User
            // 账号
            .withUsername(user.username)
            // 密码
            .password(user.password)
            // 用户角色
            .roles(*roleList.stream().map { role: Role -> role.code }
                .toList()
                .toTypedArray())
            // 是否锁定
            .accountLocked(user.locked)
            // 是否禁用
            .disabled(user.disabled)
            .build()
    }
}
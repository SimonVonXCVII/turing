package com.simonvonxcvii.turing.filter

import com.simonvonxcvii.turing.component.CustomNimbusJwtProvider
import com.simonvonxcvii.turing.entity.User
import com.simonvonxcvii.turing.properties.SecurityProperties
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Jwt 请求调度的一次执行认证过滤器
 *
 * @author Simon Von
 * @since 11/22/2022 2:19 PM
 */
@Component
class CustomJwtOncePerRequestFilter(
    private val customNimbusJwtProvider: CustomNimbusJwtProvider,
    private val securityProperties: SecurityProperties,
    private val redisTemplate: RedisTemplate<Any, Any>
) : OncePerRequestFilter() {
    /**
     * 与 doFilter 的契约相同，但保证在单个请求线程中每个请求仅调用一次。详情请参阅 [shouldNotFilterAsyncDispatch]。
     * 提供 HttpServletRequest 和 HttpServletResponse 参数，而不是默认的 ServletRequest 和 ServletResponse 参数。
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // TODO AntPathMatcher 到底是否线程安全？如果是就恢复成常量
        val antPathMatcher = AntPathMatcher()
        // 判断本次请求是否需要拦截
        val matched = securityProperties.whitelist.none { antPathMatcher.match(it, request.requestURI) }
        // 如果不在白名单则拦截
        if (matched) {
            // 从请求中解析 username
            val username = customNimbusJwtProvider.getUsername(request)
            // 根据 username 从 redis 获取 user
            val user = redisTemplate.opsForHash<String, User>().get(User.REDIS_KEY_PREFIX, username)
                ?: throw AuthenticationServiceException("无法获取到用户信息")
            // 缓存用户信息到 SecurityContext
            // TODO 考虑是否仅当 SecurityContextHolder.getContext().authentication 为 null 时才赋值
            // TODO 2023/8/31 在两个地方都设置了 user，如何才能只需要设置一次
            val token = UsernamePasswordAuthenticationToken.authenticated(
                user, user.password, user.authorities
            )
//        token.details = user
            SecurityContextHolder.getContext().authentication = token
        }
        // 执行下一个 filter
        filterChain.doFilter(request, response)
    }
}
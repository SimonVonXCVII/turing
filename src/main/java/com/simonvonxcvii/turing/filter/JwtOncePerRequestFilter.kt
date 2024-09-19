package com.simonvonxcvii.turing.filter

import com.simonvonxcvii.turing.entity.User
import com.simonvonxcvii.turing.properties.SecurityProperties
import com.simonvonxcvii.turing.service.NimbusJwtService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Jwt 请求调度的一次执行认证过滤器
 *
 * @author SimonVonXCVII
 * @since 11/22/2022 2:19 PM
 */
@Component
class JwtOncePerRequestFilter(
    private val nimbusJwtService: NimbusJwtService,
    private val securityProperties: SecurityProperties,
    private val redisTemplate: RedisTemplate<String, Any>
) : OncePerRequestFilter() {
    /**
     * 与 doFilter 的契约相同，但保证在单个请求线程中每个请求只调用一次。有关详细信息，请参阅 [shouldNotFilterAsyncDispatch]。
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 判断本次请求是否需要拦截
        for (path in securityProperties.whitelist) {
            if (ANT_PATH_MATCHER.match(path, request.requestURI)) {
                // 执行下一个 filter
                filterChain.doFilter(request, response)
                // return 是必须的
                return
            }
        }
        // 校验请求是否正确携带 token
        val jwt = nimbusJwtService.resolve(request)
        // 缓存用户信息到 SecurityContext
        val username = jwt.getClaim<String>(OAuth2ParameterNames.USERNAME)
        val user = redisTemplate.opsForValue()[User.REDIS_KEY_PREFIX + username] as User?
            ?: throw AuthenticationServiceException("无法获取到用户信息")
        // TODO: 2023/8/31 在两个地方都设置了 user，如何才能只需要设置一次
        val token = UsernamePasswordAuthenticationToken.authenticated(user, user.password, user.authorities)
//        token.details = user
        SecurityContextHolder.getContext().authentication = token
        // 执行下一个 filter
        filterChain.doFilter(request, response)
    }

    companion object {
        private val ANT_PATH_MATCHER = AntPathMatcher()
    }
}

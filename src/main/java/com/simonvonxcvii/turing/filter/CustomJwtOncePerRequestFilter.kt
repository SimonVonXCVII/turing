package com.simonvonxcvii.turing.filter

import com.simonvonxcvii.turing.component.CustomNimbusJwtProvider
import com.simonvonxcvii.turing.properties.CustomSecurityProperties
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Jwt 请求调度的一次执行认证过滤器
 * 因为 Spring security 内部并没有做校验 token 合法性的操作，所以必须自己校验
 *
 * @author Simon Von
 * @since 11/22/2022 2:19 PM
 */
@Component
class CustomJwtOncePerRequestFilter(
    private val customSecurityProperties: CustomSecurityProperties,
    private val customNimbusJwtProvider: CustomNimbusJwtProvider
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
        val matched = customSecurityProperties.whitelist.none {
            antPathMatcher.match(it, request.requestURI)
        }
        // 如果不在白名单则拦截
        if (matched) {
            // 从请求中解析校验 token 合法性
            customNimbusJwtProvider.resolve(request)
        }
        // 执行下一个 filter
        filterChain.doFilter(request, response)
    }
}
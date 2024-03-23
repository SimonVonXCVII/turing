package com.shiminfxcvii.turing.component

import com.shiminfxcvii.turing.entity.User
import com.shiminfxcvii.turing.service.NimbusJwtService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Component

/**
 * 在 LogoutFilter 成功注销后调用的策略，用于处理重定向或转发到相应目标。
 * 请注意，该接口与 LogoutHandler 几乎相同，但可能会引发异常。LogoutHandler 实现希望被调用以执行必要地清理，因此不应引发异常。
 *
 * @author ShiminFXCVII
 * @since 12/27/2022 4:20 PM
 */
@Component
class LogoutSuccessHandlerImpl(
    private val nimbusJwtService: NimbusJwtService,
    private val redisTemplate: RedisTemplate<String, Any>
) : LogoutSuccessHandler {
    /**
     * 自定义用户退出登录后的一些操作
     *
     * @param request        the request.
     * @param response       the response.
     * @param authentication 认证信息，始终为 null
     */
    override fun onLogoutSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ) {
        val jwt = nimbusJwtService.resolve(request)
        val username = jwt.getClaim<String>(OAuth2ParameterNames.USERNAME)
        // 清除缓存的用户信息
        redisTemplate.opsForValue().getAndDelete(User.REDIS_KEY_PREFIX + username)
    }
}

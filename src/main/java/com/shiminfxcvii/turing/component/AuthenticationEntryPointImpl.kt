package com.shiminfxcvii.turing.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.shiminfxcvii.turing.common.exception.BizRuntimeException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.*
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException
import org.springframework.security.web.authentication.rememberme.CookieTheftException
import org.springframework.security.web.authentication.rememberme.InvalidCookieException
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException
import org.springframework.security.web.authentication.session.SessionAuthenticationException
import org.springframework.security.web.authentication.www.NonceExpiredException
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * 由 ExceptionTranslationFilter 用于启动身份验证方案。
 *
 * @author ShiminFXCVII
 * @since 3/9/2023 5:13 PM
 */
@Component
class AuthenticationEntryPointImpl(private val objectMapper: ObjectMapper) : AuthenticationEntryPoint {
    /**
     * 启动身份验证方案。
     * 在调用此方法之前，ExceptionTranslationFilter 将使用请求的目标 URL 填充名为
     * AbstractAuthenticationProcessingFilter.SPRING_SECURITY_SAVED_REQUEST_KEY 的 HttpSession 属性。
     * 实现应根据需要修改 ServletResponse 上的标头以开始身份验证过程。
     *
     * @param request       that resulted in an `AuthenticationException`
     * @param response      so that the user agent can begin authentication
     * @param authException that caused the invocation
     */
    @Throws(IOException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        var massage: String? = null
        if (StringUtils.hasText(authException.message)) {
            massage = authException.message
        } else if (authException is AccountExpiredException) {
            massage = "账户已过期"
        } else if (authException is CredentialsExpiredException) {
            massage = "凭据已过期"
        } else if (authException is DisabledException) {
            massage = "账户已禁用"
        } else if (authException is LockedException) {
            massage = "账户已锁定"
        } else if (authException is AccountStatusException) {
            massage = "账号状态身份验证异常"
        } else if (authException is AuthenticationCredentialsNotFoundException) {
            massage = "未找到身份验证凭据"
        } else if (authException is InternalAuthenticationServiceException) {
            massage = "内部身份验证服务异常"
        } else if (authException is AuthenticationServiceException) {
            massage = "身份验证服务异常"
        } else if (authException is BadCredentialsException) {
            massage = "凭据无效，用户名或密码有误"
        } else if (authException is CookieTheftException) {
            massage = "Cookie 盗窃异常"
        } else if (authException is InvalidCookieException) {
            massage = "无效的 Cookie 异常"
        } else if (authException is RememberMeAuthenticationException) {
            massage = "记住我身份验证异常"
        } else if (authException is InsufficientAuthenticationException) {
            massage = "凭据不够可信"
        } else if (authException is OAuth2AuthenticationException) {
            massage = "OAuth 2.0 相关的身份验证错误"
        } else if (authException is NonceExpiredException) {
            massage = "摘要随机数已过期"
        } else if (authException is PreAuthenticatedCredentialsNotFoundException) {
            massage = "未找到预验证凭据异常"
        } else if (authException is ProviderNotFoundException) {
            massage = "未找到身份验证提供者"
        } else if (authException is SessionAuthenticationException) {
            massage = "未找到身份验证提供者"
        } else if (authException is UsernameNotFoundException) {
            massage = "无法通过用户名找到用户"
        }
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.writer.write(objectMapper.writeValueAsString(BizRuntimeException(massage)))
    }
}
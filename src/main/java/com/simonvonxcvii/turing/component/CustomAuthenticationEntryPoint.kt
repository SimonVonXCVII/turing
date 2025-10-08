package com.simonvonxcvii.turing.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.simonvonxcvii.turing.common.exception.BizRuntimeException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.*
import org.springframework.security.authentication.ott.InvalidOneTimeTokenException
import org.springframework.security.authentication.password.CompromisedPasswordException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException
import org.springframework.security.web.authentication.rememberme.CookieTheftException
import org.springframework.security.web.authentication.rememberme.InvalidCookieException
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException
import org.springframework.security.web.authentication.session.SessionAuthenticationException
import org.springframework.security.web.authentication.www.NonceExpiredException
import org.springframework.stereotype.Component

/**
 * 由 ExceptionTranslationFilter 用于启动身份验证方案。
 *
 * @author Simon Von
 * @since 3/9/2023 5:13 PM
 */
@Component
class CustomAuthenticationEntryPoint(private val objectMapper: ObjectMapper) : AuthenticationEntryPoint {
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
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val massage = when (authException) {
            is AccountExpiredException -> "账户已过期"
            is CredentialsExpiredException -> "凭据已过期"
            is DisabledException -> "帐户被禁用"
            is LockedException -> "帐户被锁定"
            is AccountStatusException -> "帐户状态锁定或禁用"
            is AuthenticationCredentialsNotFoundException -> "未找到身份验证凭据"
            is InternalAuthenticationServiceException -> "内部身份验证服务异常"
            is AuthenticationServiceException -> "身份验证服务异常"
            is BadCredentialsException -> "凭据无效，用户名或密码有误"
            is CompromisedPasswordException -> "提供的密码已受损"
            is CookieTheftException -> "Cookie 盗窃异常"
            is InsufficientAuthenticationException -> "凭据不够可信"
            is InvalidBearerTokenException -> "无效的承载令牌"
            is InvalidCookieException -> "无效的 Cookie 异常"
            is InvalidOneTimeTokenException -> "一次性令牌无效"
            is NonceExpiredException -> "摘要随机数已过期"
            is OAuth2AuthorizationCodeRequestAuthenticationException -> "尝试对 OAuth 2.0 授权请求（或同意）失败"
            is OAuth2AuthenticationException -> "OAuth 2.0 相关的身份验证错误"
            is PreAuthenticatedCredentialsNotFoundException -> "未找到预验证凭据异常"
            is ProviderNotFoundException -> "未找到身份验证提供者"
            is RememberMeAuthenticationException -> "记住我身份验证异常"
            is SessionAuthenticationException -> "未找到身份验证提供者"
            is UsernameNotFoundException -> "无法通过用户名找到用户"
            else -> authException.message
        }
        println(authException.message)
        println(authException.localizedMessage)
        println(response.characterEncoding)
        println(response.contentType)
        println(response.status)
//        response.characterEncoding = StandardCharsets.UTF_8.name()
//        response.contentType = MediaType.APPLICATION_JSON_VALUE
//        response.status = HttpStatus.UNAUTHORIZED.value()
        val string = objectMapper.writeValueAsString(BizRuntimeException(massage))
        response.writer.write(string)
    }
}

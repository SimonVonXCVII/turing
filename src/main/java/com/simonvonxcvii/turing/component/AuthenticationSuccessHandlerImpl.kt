package com.simonvonxcvii.turing.component

import com.fasterxml.jackson.databind.ObjectMapper
import com.simonvonxcvii.turing.common.result.Result
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

/**
 * 用于处理成功的用户身份验证的策略。
 * 实现可以做任何他们想做的事情，但典型的行为是控制到后续目的地的导航（使用重定向或转发）。
 * 例如，在用户通过提交登录表单登录后，应用程序需要决定之后应将其重定向到何处（请参阅 [org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter] 和子类）。
 * 如果需要，还可以包括其他逻辑。
 *
 * @author Simon Von
 * @since 12/22/2022 8:41 PM
 */
@Component
class AuthenticationSuccessHandlerImpl(private val objectMapper: ObjectMapper) : AuthenticationSuccessHandler {
    /**
     * Called when a user has been successfully authenticated.
     *
     * @param request        the request which caused the successful authentication
     * @param response       the response
     * @param authentication the <tt>Authentication</tt> object which was created during
     * the authentication process.
     */
    override fun onAuthenticationSuccess(
        request: HttpServletRequest, response: HttpServletResponse,
        authentication: Authentication
    ) {
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpStatus.OK.value()
        response.writer
            .write(
                objectMapper.writeValueAsString(
                    Result.ok(mapOf(OAuth2ParameterNames.TOKEN to request.getAttribute(OAuth2ParameterNames.TOKEN)))
                )
            )
        request.removeAttribute(OAuth2ParameterNames.TOKEN)
    }
}

package com.simonvonxcvii.turing.component

import com.simonvonxcvii.turing.common.exception.BizRuntimeException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

/**
 * 用于处理失败的身份验证尝试的策略。
 * 典型的行为可能是将用户重定向到身份验证页面（在表单登录的情况下）以允许他们重试。根据异常的类型，可能会实现更复杂的逻辑。
 * 例如，CredentialsExpireException 可能会导致重定向到 Web 控制器，从而允许用户更改其密码。
 * TODO 有问题，账号或者密码错了会到这里，并且没有给前端正确提示：Bad credentials
 *
 * @author Simon Von
 * @since 12/22/2022 8:46 PM
 */
@Component
class CustomAuthenticationFailureHandler(private val objectMapper: ObjectMapper) : AuthenticationFailureHandler {
    /**
     * 尝试身份验证失败时调用
     *
     * @param request   发生于尝试身份验证的请求
     * @param response  the response.
     * @param exception 为拒绝身份验证请求而引发的异常
     */
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        println(exception.message)
        println(exception.localizedMessage)
        println(response.characterEncoding)
        println(response.contentType)
        println(response.status)
//        response.characterEncoding = StandardCharsets.UTF_8.name()
//        response.contentType = MediaType.APPLICATION_JSON_VALUE
//        response.status = HttpStatus.OK.value()
        val string = objectMapper.writeValueAsString(BizRuntimeException(exception.message))
        response.writer.write(string)
    }
}

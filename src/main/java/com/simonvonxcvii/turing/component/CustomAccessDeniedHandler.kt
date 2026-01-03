package com.simonvonxcvii.turing.component

import com.simonvonxcvii.turing.common.exception.BizRuntimeException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.logging.LogFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

/**
 * 由 ExceptionTranslationFilter 用于处理 AccessDeniedException。
 * AccessDeniedHandler 的基本实现。
 * 此实现发送 403 （SC_FORBIDDEN） HTTP 错误代码。
 * 此外，如果定义了 errorPage，则实现将执行请求分派器“转发”到指定的错误页面视图。作为“转发”，SecurityContextHolder 将保持填充状态。
 * 如果视图（或标记库或宏）希望访问 SecurityContextHolder，这是有益的。
 * 请求范围也将填充异常本身，可从密钥 WebAttributes.ACCESS_DENIED_403 获得。
 *
 * @author Simon Von
 * @since 2023/3/9 16:21
 */
@Component
class CustomAccessDeniedHandler(private val objectMapper: ObjectMapper) : AccessDeniedHandler {
    /**
     * 处理拒绝访问失败。
     */
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        println(accessDeniedException.message)
        println(accessDeniedException.localizedMessage)
        println(response.characterEncoding)
        println(response.contentType)
        println(response.status)
//        response.characterEncoding = StandardCharsets.UTF_8.name()
//        response.contentType = MediaType.APPLICATION_JSON_VALUE
//        response.status = HttpStatus.UNAUTHORIZED.value()
        val string = objectMapper.writeValueAsString(BizRuntimeException(accessDeniedException.message))
        response.writer.write(string)
    }

    companion object {
        private val logger = LogFactory.getLog(CustomAccessDeniedHandler::class.java)
    }
}

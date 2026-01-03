//package com.simonvonxcvii.turing.component
//
//import com.simonvonxcvii.turing.common.result.Result
//import com.simonvonxcvii.turing.resource.server.User
//import jakarta.servlet.http.HttpServletRequest
//import jakarta.servlet.http.HttpServletResponse
//import org.springframework.http.HttpEntity
//import org.springframework.http.HttpHeaders
//import org.springframework.http.HttpStatus
//import org.springframework.http.MediaType
//import org.springframework.security.core.Authentication
//import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler
//import org.springframework.stereotype.Component
//import org.springframework.util.LinkedMultiValueMap
//import org.springframework.web.client.RestTemplate
//import tools.jackson.databind.ObjectMapper
//import java.nio.charset.StandardCharsets
//
///**
// * 用于处理成功的用户身份验证的策略。
// * 实现可以做任何他们想做的事情，但典型的行为是控制到后续目的地的导航（使用重定向或转发）。
// * 例如，在用户通过提交登录表单登录后，应用程序需要决定之后应将其重定向到何处
// * （请参阅 [org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter] 和子类）。
// * 如果需要，还可以包括其他逻辑。
// *
// * @author Simon Von
// * @since 12/22/2022 8:41 PM
// */
//@Component
//class CustomAuthenticationSuccessHandler(
//    private val objectMapper: ObjectMapper
//) : AuthenticationSuccessHandler {
//    /**
//     * 当用户成功身份验证时调用。
//     *
//     * @param request        导致成功身份验证的请求
//     * @param response       the response
//     * @param authentication the <tt>Authentication</tt> object which was created during
//     * the authentication process.
//     */
//    override fun onAuthenticationSuccess(
//        request: HttpServletRequest,
//        response: HttpServletResponse,
//        authentication: Authentication
//    ) {
//        val body = LinkedMultiValueMap<String, String>()
//        // todo client_id 居然可以是其他的，至少可以同一个 realm 下，并且是可以没有 credentials 的
//        body.add("client_id", "confidential-client")
//        body.add("client_secret", "xcDFONfvwYqJSotUDFjsg1ajF7mzkdsE")
//        // todo username 与 password 居然是 oidc 中创建的 user，
//        //  难道不应该填前端登录用户的信息吗？这样的话前端不是每个用户登录获取的 token 都相同？
//        body.add("username", "turing-user")
//        body.add("password", "password")
//        body.add("grant_type", "password")
//        val httpHeaders = HttpHeaders().apply {
//            contentType = MediaType.APPLICATION_FORM_URLENCODED
//        }
//        val httpEntity = HttpEntity(body, httpHeaders)
//        val responseEntity = RestTemplate()
//            .postForEntity(
//                "http://localhost:8081/realms/turing-realm/protocol/openid-connect/token",
//                httpEntity,
//                Map::class.java
//            )
//        val accessToken = responseEntity.body?.get(OAuth2ParameterNames.ACCESS_TOKEN)
//        // todo 改成 OAuth2ParameterNames.ACCESS_TOKEN
//        //  后期可以将整个 body 返回
//        //  再后期可以不在后端这里获取 token 了，直接将以上代码由前端发送
//        val principal = authentication.principal
//        if (principal is User) {
//            val map = mapOf(
//                "accessToken" to accessToken,
//                "id" to principal.id,
//                "realName" to principal.realName,
//                "username" to principal.username,
//                "password" to principal.password,
//                "roles" to principal.roles,
//            )
//            val result = objectMapper.writeValueAsString(Result.ok(map))
//            response.writer.write(result)
//        }
//        response.characterEncoding = StandardCharsets.UTF_8.name()
//        response.contentType = MediaType.APPLICATION_JSON_VALUE
//        response.status = HttpStatus.OK.value()
//    }
//}

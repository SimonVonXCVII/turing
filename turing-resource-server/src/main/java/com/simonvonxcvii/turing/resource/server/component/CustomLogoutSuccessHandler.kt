//package com.simonvonxcvii.turing.component
//
//import jakarta.servlet.http.HttpServletRequest
//import jakarta.servlet.http.HttpServletResponse
//import org.springframework.data.redis.core.RedisTemplate
//import org.springframework.security.core.Authentication
//import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
//import org.springframework.stereotype.Component
//
///**
// * 在 LogoutFilter 成功注销后调用的策略，用于处理重定向或转发到相应目标。
// * 请注意，该接口与 LogoutHandler 几乎相同，但可能会引发异常。LogoutHandler 实现希望被调用以执行必要地清理，因此不应引发异常。
// *
// * @author Simon Von
// * @since 12/27/2022 4:20 PM
// */
//@Component
//class CustomLogoutSuccessHandler(
////    private val customNimbusJwtProvider: CustomNimbusJwtProvider,
//    private val redisTemplate: RedisTemplate<Any, Any>
//) : LogoutSuccessHandler {
//    /**
//     * 自定义用户退出登录后的一些操作
//     *
//     * @param request        the request.
//     * @param response       the response.
//     * @param authentication 认证信息，始终为 null
//     */
//    override fun onLogoutSuccess(
//        request: HttpServletRequest,
//        response: HttpServletResponse,
//        authentication: Authentication?
//    ) {
//        // 从请求中解析 username todo 前端需要在退出请求中加上参数 username
////        val username = customNimbusJwtProvider.getUsername(request)
//        // 清除缓存的用户信息
////        redisTemplate.opsForHash<String, User>().delete(User.REDIS_KEY_PREFIX, username)
//    }
//}

//package com.simonvonxcvii.turing.filter
//
//import com.simonvonxcvii.turing.component.CustomAuthenticationEntryPoint
//import com.simonvonxcvii.turing.utils.Constants
//import jakarta.servlet.FilterChain
//import jakarta.servlet.http.HttpServletRequest
//import jakarta.servlet.http.HttpServletResponse
//import org.springframework.http.HttpMethod
//import org.springframework.security.authentication.BadCredentialsException
//import org.springframework.stereotype.Component
//import org.springframework.web.filter.OncePerRequestFilter
//
///**
// * Captcha 请求调度的一次执行认证过滤器
// *
// * @author Simon Von
// * @since 9/28/25 12:35 AM
// */
//@Component
//class CustomCaptchaOncePerRequestFilter(
//    private val customAuthenticationEntryPoint: CustomAuthenticationEntryPoint
//) : OncePerRequestFilter() {
//    /**
//     * 与 doFilter 的契约相同，但保证在单个请求线程中每个请求仅调用一次。详情请参阅 [shouldNotFilterAsyncDispatch]。
//     * 提供 HttpServletRequest 和 HttpServletResponse 参数，而不是默认的 ServletRequest 和 ServletResponse 参数。
//     */
//    override fun doFilterInternal(
//        request: HttpServletRequest,
//        response: HttpServletResponse,
//        filterChain: FilterChain
//    ) {
//        // 只拦截登录请求
//        if (request.requestURI == Constants.LOGIN_URL && request.method == HttpMethod.POST.name()) {
//            val captcha = request.getParameter("captcha")
//            if (captcha != "true") {
//                customAuthenticationEntryPoint.commence(
//                    request,
//                    response,
//                    BadCredentialsException("请先完成验证")
//                )
//                return
//            }
//        }
//
//        // 执行下一个 filter
//        filterChain.doFilter(request, response)
//    }
//}
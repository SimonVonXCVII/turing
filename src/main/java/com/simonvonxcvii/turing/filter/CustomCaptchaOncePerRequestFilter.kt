package com.simonvonxcvii.turing.filter

import com.simonvonxcvii.turing.utils.Constants
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Component
import org.springframework.util.DigestUtils
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.net.InetAddress
import java.nio.charset.StandardCharsets

/**
 * Captcha 请求调度的一次执行认证过滤器
 *
 * @author Simon Von
 * @since 9/28/25 12:35 AM
 */
@Component
class CustomCaptchaOncePerRequestFilter(
    private val stringRedisTemplate: StringRedisTemplate
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
        // 只拦截登录请求
        if (!Constants.ANT_PATH_MATCHER.match(Constants.LOGIN_REQUEST, request.requestURI)) {
            // 执行下一个 filter
            filterChain.doFilter(request, response)
            // return 是必须的，不然会执行下面的代码
            return
        }
        // 使用 md5 这种方式作为 key 的原因是 session id 总是会改变，同一个客户端的浏览器发送的请求的 session id 无法保持一致
        val ipAddr = InetAddress.getByName(request.remoteAddr).hostAddress
        val userAgent = request.getHeader(HttpHeaders.USER_AGENT)
        val ipAddrUserAgentByte = (ipAddr + userAgent).toByteArray(StandardCharsets.UTF_8)
        val md5DigestAsHex = DigestUtils.md5DigestAsHex(ipAddrUserAgentByte)
        // 服务端验证码
        val serverCaptcha = stringRedisTemplate.opsForValue().get(Constants.REDIS_CAPTCHA + md5DigestAsHex)
        // 客户端验证码
        val clientCaptcha = request.getParameter(Constants.CAPTCHA)

        // TODO: 验证码有时候会出现过期提示，但实则并没有过期
        println("serverCaptcha: $serverCaptcha")
        println("clientCaptcha: $clientCaptcha")
        if (!StringUtils.hasText(serverCaptcha)) throw BadCredentialsException("验证码已过期")
        if (!StringUtils.hasText(clientCaptcha)) throw BadCredentialsException("请输入验证码")
        if (!serverCaptcha.equals(clientCaptcha, true))
            throw BadCredentialsException("验证码错误，请重新输入")

        // 将 md5DigestAsHex 保存到 request 中，便于在 UserDetailsServiceImpl#loadUserByUsername 方法中获取
        request.setAttribute(Constants.HEX_DIGEST, md5DigestAsHex)

        // 执行下一个 filter
        filterChain.doFilter(request, response)
    }
}
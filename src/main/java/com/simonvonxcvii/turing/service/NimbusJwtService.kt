package com.simonvonxcvii.turing.service

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.oauth2.jwt.Jwt

/**
 * NimbusJwt 服务接口
 *
 * @author Simon Von
 * @since 2/20/2023 11:19 PM
 */
interface NimbusJwtService {
    /**
     * 根据用户 id 和用户名进行编码并返回生成的 JWT
     *
     * @param userId   用户 id，不能为空
     * @param username 用户名，不能为空
     * @return 生成的 JWT
     * @author Simon Von
     * @since 2/21/2023 12:06 PM
     */
    fun encode(userId: String, username: String): Jwt

    /**
     * 从其紧凑的声明表示格式解码和验证 JWT
     *
     * @param token JWT 值，不能为空
     * @return 经过验证的 JWT
     * @author Simon Von
     * @since 2/21/2023 12:54 PM
     */
    fun decode(token: String): Jwt

    /**
     * 从请求中解析 JWT
     *
     * @param request the request
     * @return 解析后的 JWT
     * @author Simon Von
     * @since 2023/6/17 20:23
     */
    fun resolve(request: HttpServletRequest): Jwt
}

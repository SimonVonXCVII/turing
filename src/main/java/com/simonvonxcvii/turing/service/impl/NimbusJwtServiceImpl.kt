package com.simonvonxcvii.turing.service.impl

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import com.simonvonxcvii.turing.properties.SecurityProperties
import com.simonvonxcvii.turing.service.NimbusJwtService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.ssl.SslBundles
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.oauth2.jwt.*
import org.springframework.stereotype.Service
import org.springframework.util.Assert
import org.springframework.util.StringUtils
import java.time.Instant

/**
 * NimbusJwt 服务实现
 *
 * @author Simon Von
 * @since 2/20/2023 11:21 PM
 */
@Service
class NimbusJwtServiceImpl(
    sslBundles: SslBundles,
    private val securityProperties: SecurityProperties
) : NimbusJwtService {
    /**
     * JwtEncoder：spring security jose 基于 Nimbus 的 JWT 加密类
     */
    private final val nimbusJwtEncoder: NimbusJwtEncoder

    /**
     * JwtDecoder 的低级 Nimbus 实现，采用原始 Nimbus 配置。
     */
    private final val nimbusJwtDecoder: NimbusJwtDecoder

    init {
        val bundle = sslBundles.getBundle("turing")
        val stores = bundle.stores
        val keyStore = stores.keyStore
        val rsaKey = JWK.load(keyStore, keyStore.aliases().nextElement(), null)
        val jwkSet = JWKSet(rsaKey)
        val securityContextJWKSource: JWKSource<SecurityContext> = ImmutableJWKSet(jwkSet)

        // encode
        nimbusJwtEncoder = NimbusJwtEncoder(securityContextJWKSource)

        // decode
        // 不安全（纯）、签名和加密的 JSON Web 令牌 （JWT） 的默认处理器。
        val defaultJWTProcessor = DefaultJWTProcessor<SecurityContext>()
        // 创建新的 JWS 验证密钥选择器。
        // 形参: jwsAlg – 允许要验证的对象 JWS 算法。不得为空。
        //      jwkSource – JWK source。不得为空。
        val selector = JWSVerificationKeySelector(JWSAlgorithm.RS256, securityContextJWKSource)
        defaultJWTProcessor.jwsKeySelector = selector
        // 使用给定参数配置 NimbusJwtDecoder
        nimbusJwtDecoder = NimbusJwtDecoder(defaultJWTProcessor)
    }

    /**
     * 根据用户 id 和用户名进行编码并返回生成的 JWT
     *
     * @param userId   用户 id，不能为空
     * @param username 用户名，不能为空
     * @return 生成的 JWT
     * @author Simon Von
     * @since 2/21/2023 12:06 PM
     */
    override fun encode(userId: Int, username: String): Jwt {
        Assert.notNull(userId, "userId cannot be bull")
        Assert.hasText(username, "username cannot be blank")
        // 重要：虽然 Instant.now() 获取的时间比我们本地时间少了八个小时，但是这不影响后续的解码操作。
        // 反而，如果是 .plus(8L, ChronoUnit.HOURS) 则会报错，提示使用的 JWT 在 notBefore 之前
        val now = Instant.now()
        return nimbusJwtEncoder.encode(
            // 返回一个新的 JwtEncoderParameters，使用提供的 JwtClaimsSet 进行初始化。
            JwtEncoderParameters.from(
                JwtClaimsSet.builder()
                    // 设置颁发者 （iss） 声明，该声明标识颁发 JWT 的主体。
                    // 形参: 颁发者 – 颁发者标识符
                    .issuer("https://" + securityProperties.address)
                    // 设置主题（子）声明，该声明标识作为 JWT 主题的主体。
                    // 形参: 主题 – 主题标识符
                    .subject(securityProperties.address)
                    // 设置受众 （aud） 声明，该声明标识 JWT 所针对的收件人。
                    // 形参: 受众 – 此 JWT 所针对的受众
                    .audience(listOf(username))
                    // 设置过期时间 （exp） 声明，该声明标识不得接受 JWT 进行处理的时间或之后的时间。
                    // 形参: expiresAt – 不得接受 JWT 进行处理的时间或之后的时间
                    .expiresAt(now.plusSeconds(securityProperties.expires))
                    // 设置不早于 （nbf） 声明，该声明标识不得接受 JWT 进行处理的时间。
                    // 形参: notBefore——不得接受 JWT 进行处理的时间
                    .notBefore(now)
                    // 设置颁发时间 （iat） 声明，该声明标识颁发 JWT 的时间。
                    // 形参: 发布时间 – JWT 发布的时间
                    .issuedAt(now)
                    // 设置 JWT ID （jti） 声明，该声明为 JWT 提供唯一标识符。
                    // 形参: JTI – JWT 的唯一标识符
                    .id(userId.toString())
                    .claim(OAuth2ParameterNames.USERNAME, username)
                    .build()
            )
        )
    }

    /**
     * 从其紧凑的声明表示格式解码和验证 JWT
     *
     * @param token JWT 值，不能为空
     * @return 经过验证的 JWT
     * @author Simon Von
     * @since 2/21/2023 12:54 PM
     */
    override fun decode(token: String): Jwt {
        Assert.hasText(token, "token cannot be blank")
        return nimbusJwtDecoder.decode(token)
    }

    /**
     * 从请求中解析 JWT
     *
     * @param request the request
     * @return 解析后的 JWT
     * @author Simon Von
     * @since 2023/6/17 20:23
     */
    override fun resolve(request: HttpServletRequest): Jwt {
        Assert.notNull(request, "request cannot be null")
        val authorization = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!StringUtils.hasText(authorization)) {
            throw AuthenticationServiceException("令牌缺失")
        }
        val split =
            authorization.split(org.apache.commons.lang3.StringUtils.SPACE.toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        if (split.size != 2) {
            throw AuthenticationServiceException("非法令牌，令牌必须以 Bearer 为前缀并以一个空格分开")
        }
        if (!OAuth2AccessToken.TokenType.BEARER.value.equals(split[0], true)) {
            throw AuthenticationServiceException("非法令牌，令牌必须以 Bearer 为前缀并以一个空格分开")
        }
        // 目前认为没有必要实现 token 刷新的功能，因为有效期设置的一周，一周后重新登录就好，中间不需要做刷新，因为一直都是有效的，每次请求都会携带 token
        try {
            return decode(split[1])
        } catch (_: JwtException) {
            throw AuthenticationServiceException("令牌已过期或验证不正确")
        }
    }
}

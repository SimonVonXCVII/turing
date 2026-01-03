package com.simonvonxcvii.turing.component

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import com.simonvonxcvii.turing.properties.CustomSecurityProperties
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ssl.SslBundles
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.jwt.*
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import java.time.Instant
import java.util.*

/**
 * NimbusJwt 服务实现
 * TODO 尝试基于 JwtAuthenticationProvider 实现
 *
 * @author Simon Von
 * @since 2/20/2023 11:21 PM
 */
@Component
class CustomNimbusJwtProvider(
    sslBundles: SslBundles,
    private val customSecurityProperties: CustomSecurityProperties,
    @param:Value($$"${spring.application.name}") private val applicationName: String
) : JwtEncoder, JwtDecoder {
    /**
     * JwtEncoder：spring security jose 基于 Nimbus 的 JWT 加密类
     */
    private final val nimbusJwtEncoder: NimbusJwtEncoder

    /**
     * JwtDecoder 的低级 Nimbus 实现，采用原始 Nimbus 配置。
     */
    private final val nimbusJwtDecoder: NimbusJwtDecoder

    // todo 尝试优化，使用 NimbusJwtDecoder.withIssuerLocation(String issuer) 等
    init {
        // 返回具有所提供名称的 SslBundle。
        val bundle = sslBundles.getBundle(applicationName)
        // 返回可用于访问此 bundle 的密钥和信任存储的 SslStoreBundle。
        val stores = bundle.stores
        // 返回由信任材料生成的密钥库或 null。
        val keyStore = stores.keyStore
        // 从指定的 JCE 密钥库加载 JWK。JWK 可以是 RSA 公钥/私钥、EC 公钥/私钥或密钥。需要 BouncyCastle。
        // 重要提示：X.509 证书未经验证！
        val rsaKey = JWK.load(keyStore, keyStore?.aliases()?.nextElement(), null)
        // 使用单个键创建一个新的 JWK 集。
        val jwkSet = JWKSet(rsaKey)
        // 创建由不可变的 JWK 集支持的新 JWK 源。
        val securityContextJWKSource: JWKSource<SecurityContext> = ImmutableJWKSet(jwkSet)

        // encode
        nimbusJwtEncoder = NimbusJwtEncoder(securityContextJWKSource)

        // decode
        // 创建新的 JWS 验证密钥选择器。
        // 形参: jwsAlg – 允许要验证的对象 JWS 算法。不得为空。
        //      jwkSource – JWK source。不得为空。
        val selector = JWSVerificationKeySelector(JWSAlgorithm.RS256, securityContextJWKSource)
        // 不安全（纯）、签名和加密的 JSON Web 令牌 （JWT） 的默认处理器。
        val defaultJWTProcessor = DefaultJWTProcessor<SecurityContext>()
        defaultJWTProcessor.jwsKeySelector = selector
        // 使用给定参数配置 NimbusJwtDecoder
        nimbusJwtDecoder = NimbusJwtDecoder(defaultJWTProcessor)
    }

    /**
     * 将 JWT 编码为其紧凑的声明表示格式。
     */
    override fun encode(parameters: JwtEncoderParameters): Jwt {
        return nimbusJwtEncoder.encode(parameters)
    }

    /**
     * 从其紧凑声明表示格式解码 JWT 并返回 Jwt。
     */
    override fun decode(token: String): Jwt {
        return nimbusJwtDecoder.decode(token)
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
    fun encode(userId: Int, username: String): Jwt {
        // 重要：虽然 Instant.now() 获取的时间比我们本地时间少了八个小时，但是这不影响后续的解码操作。
        // 反而，如果是 .plus(8L, ChronoUnit.HOURS) 则会报错，提示使用的 JWT 在 notBefore 之前
        val now = Instant.now()
        val jwtClaimsSet = JwtClaimsSet.builder()
            // 设置颁发者 （iss） 声明，该声明标识颁发 JWT 的主体。
            // 形参: 颁发者 – 颁发者标识符
            .issuer(customSecurityProperties.host)
            // 设置主题（子）声明，该声明标识作为 JWT 主题的主体。
            // 形参: 主题 – 主题标识符
            .subject(userId.toString())
            // 设置受众 （aud） 声明，该声明标识 JWT 所针对的收件人。
            // 形参: 受众 – 此 JWT 所针对的受众
            .audience(listOf(applicationName))
            // 设置过期时间 （exp） 声明，该声明标识不得接受 JWT 进行处理的时间或之后的时间。
            // 形参: expiresAt – 不得接受 JWT 进行处理的时间或之后的时间
            .expiresAt(now.plusSeconds(customSecurityProperties.expires.toLong()))
            // 设置不早于 （nbf） 声明，该声明标识不得接受 JWT 进行处理的时间。
            // 形参: notBefore——不得接受 JWT 进行处理的时间
            .notBefore(now.plusNanos(1))
            // 设置颁发时间 （iat） 声明，该声明标识颁发 JWT 的时间。
            // 形参: 发布时间 – JWT 发布的时间
            .issuedAt(now)
            // 设置 JWT ID （jti） 声明，该声明为 JWT 提供唯一标识符。
            // 形参: JTI – JWT 的唯一标识符
            .id(UUID.randomUUID().toString())
            // 设置声明。
            .claim("username", username)
            .build()
        // 返回一个新的 JwtEncoderParameters，使用提供的 JwtClaimsSet 进行初始化。
        val jwtEncoderParameters = JwtEncoderParameters.from(jwtClaimsSet)
        return encode(jwtEncoderParameters)
    }

    /**
     * 从请求中解析 JWT
     *
     * @param request the request
     * @return 解析后的 JWT
     * @author Simon Von
     * @since 2023/6/17 20:23
     */
    fun resolve(request: HttpServletRequest): Jwt {
        // todo 更合理更准确的情况下，HttpHeaders.AUTHORIZATION 是否应该为
        //  org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver.ACCESS_TOKEN_PARAMETER_NAME
        val authorization = request.getHeader(HttpHeaders.AUTHORIZATION)
        if (!StringUtils.hasText(authorization)) {
            throw AuthenticationServiceException("令牌缺失")
        }
        val split = authorization.split(org.apache.commons.lang3.StringUtils.SPACE.toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()
        if (split.size != 2) {
            throw AuthenticationServiceException("非法令牌，令牌必须以 Bearer 为前缀并以一个空格分开")
        }
        if (!OAuth2AccessToken.TokenType.BEARER.value.equals(split[0], true)) {
            throw AuthenticationServiceException("非法令牌，令牌必须以 Bearer 为前缀并以一个空格分开")
        }
        // 目前认为没有必要实现 token 刷新的功能，因为有效期设置的一周，一周后重新登录就好，中间不需要做刷新，
        // 因为一直都是有效的，每次请求都会携带 token
        try {
            return decode(split[1])
        } catch (_: JwtException) {
            throw AuthenticationServiceException("令牌已过期或验证不正确")
        }
    }

    /**
     * 从请求中解析 username
     *
     * @param request the request
     * @return username
     * @author Simon Von
     * @since 9/29/25 1:20 AM
     */
    fun getUsername(request: HttpServletRequest): String {
        // 校验请求是否正确携带 token
        val jwt = resolve(request)
        return jwt.getClaim("username")
    }
}
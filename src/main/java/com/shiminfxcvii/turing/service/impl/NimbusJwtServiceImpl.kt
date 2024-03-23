package com.shiminfxcvii.turing.service.impl;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.shiminfxcvii.turing.properties.SecurityProperties;
import com.shiminfxcvii.turing.service.NimbusJwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Collections;

/**
 * NimbusJwt 服务实现
 *
 * @author ShiminFXCVII
 * @since 2/20/2023 11:21 PM
 */
@Service
public class NimbusJwtServiceImpl implements NimbusJwtService {

    /**
     * JwtEncoder：spring security jose 基于 Nimbus 的 JWT 加密类
     */
    private final NimbusJwtEncoder nimbusJwtEncoder;
    /**
     * JwtDecoder 的低级 Nimbus 实现，采用原始 Nimbus 配置。
     */
    private final NimbusJwtDecoder nimbusJwtDecoder;
    /**
     * 项目安全参数配置类
     */
    private final SecurityProperties securityProperties;

    public NimbusJwtServiceImpl(JWKSource<SecurityContext> securityContextJWKSource, SecurityProperties securityProperties) {
        // encode
        nimbusJwtEncoder = new NimbusJwtEncoder(securityContextJWKSource);
        // decode
        // 不安全（纯）、签名和加密的 JSON Web 令牌 （JWT） 的默认处理器。
        DefaultJWTProcessor<SecurityContext> defaultJWTProcessor = new DefaultJWTProcessor<>();
        // 创建新的 JWS 验证密钥选择器。
        // 形参: jwsAlg – 允许要验证的对象 JWS 算法。不得为空。
        //      jwkSource – JWK source。不得为空。
        JWSKeySelector<SecurityContext> selector = new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, securityContextJWKSource);
        defaultJWTProcessor.setJWSKeySelector(selector);
        // 使用给定参数配置 NimbusJwtDecoder
        nimbusJwtDecoder = new NimbusJwtDecoder(defaultJWTProcessor);
        this.securityProperties = securityProperties;
    }

    /**
     * 根据用户 id 和用户名进行编码并返回生成的 JWT
     *
     * @param userId   用户 id，不能为空
     * @param username 用户名，不能为空
     * @return 生成的 JWT
     * @author ShiminFXCVII
     * @since 2/21/2023 12:06 PM
     */
    @Override
    public Jwt encode(String userId, String username) {
        Assert.notNull(userId, "userId cannot be null");
        Assert.notNull(username, "username cannot be null");
        Instant now = Instant.now();
        return nimbusJwtEncoder.encode(
                // 返回一个新的 JwtEncoderParameters，使用提供的 JwtClaimsSet 进行初始化。
                JwtEncoderParameters.from(
                        JwtClaimsSet.builder()
                                // 设置颁发者 （iss） 声明，该声明标识颁发 JWT 的主体。
                                // 形参: 颁发者 – 颁发者标识符
                                .issuer("https://" + securityProperties.getAddress())
                                // 设置主题（子）声明，该声明标识作为 JWT 主题的主体。
                                // 形参: 主题 – 主题标识符
                                .subject(securityProperties.getAddress())
                                // 设置受众 （aud） 声明，该声明标识 JWT 所针对的收件人。
                                // 形参: 受众 – 此 JWT 所针对的受众
                                .audience(Collections.singletonList(username))
                                // 设置过期时间 （exp） 声明，该声明标识不得接受 JWT 进行处理的时间或之后的时间。
                                // 形参: expiresAt – 不得接受 JWT 进行处理的时间或之后的时间
                                .expiresAt(now.plusSeconds(securityProperties.getExpires()))
                                // 设置不早于 （nbf） 声明，该声明标识不得接受 JWT 进行处理的时间。
                                // 形参: notBefore——不得接受 JWT 进行处理的时间
                                .notBefore(now)
                                // 设置颁发时间 （iat） 声明，该声明标识颁发 JWT 的时间。
                                // 形参: 发布时间 – JWT 发布的时间
                                .issuedAt(now)
                                // 设置 JWT ID （jti） 声明，该声明为 JWT 提供唯一标识符。
                                // 形参: JTI – JWT 的唯一标识符
                                .id(userId)
                                .claim(OAuth2ParameterNames.USERNAME, username)
                                .build()));
    }

    /**
     * 从其紧凑的声明表示格式解码和验证 JWT
     *
     * @param token JWT 值，不能为空
     * @return 经过验证的 JWT
     * @author ShiminFXCVII
     * @since 2/21/2023 12:54 PM
     */
    @Override
    public Jwt decode(String token) {
        Assert.notNull(token, "token cannot be null");
        return nimbusJwtDecoder.decode(token);
    }

    /**
     * 从请求中解析 JWT
     *
     * @param request the request
     * @return 解析后的 JWT
     * @author ShiminFXCVII
     * @since 2023/6/17 20:23
     */
    @Override
    public Jwt resolve(HttpServletRequest request) {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authorization)) {
            throw new AuthenticationServiceException("令牌缺失");
        }
        String[] split = authorization.split(org.apache.commons.lang3.StringUtils.SPACE);
        if (split.length != 2) {
            throw new AuthenticationServiceException("非法令牌，令牌必须以 Bearer 为前缀并以一个空格分开");
        }
        if (!OAuth2AccessToken.TokenType.BEARER.getValue().equalsIgnoreCase(split[0])) {
            throw new AuthenticationServiceException("非法令牌，令牌必须以 Bearer 为前缀并以一个空格分开");
        }
        Jwt jwt = decode(split[1]);
        if (jwt == null) {
            throw new AuthenticationServiceException("令牌已过期或验证不正确");
        }
        return jwt;
    }

}
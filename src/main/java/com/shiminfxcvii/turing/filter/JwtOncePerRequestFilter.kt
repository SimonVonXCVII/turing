package com.shiminfxcvii.turing.filter;

import com.shiminfxcvii.turing.entity.Role;
import com.shiminfxcvii.turing.entity.User;
import com.shiminfxcvii.turing.properties.SecurityProperties;
import com.shiminfxcvii.turing.service.NimbusJwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Jwt 请求调度的一次执行认证过滤器
 *
 * @author zhq
 * @author ShiminFXCVII
 * @since 11/22/2022 2:19 PM
 */
@Component
public class JwtOncePerRequestFilter extends OncePerRequestFilter {

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    private final NimbusJwtService nimbusJwtService;
    private final SecurityProperties securityProperties;
    private final RedisTemplate<String, Object> redisTemplate;

    public JwtOncePerRequestFilter(
            NimbusJwtService nimbusJwtService,
            SecurityProperties securityProperties,
            RedisTemplate<String, Object> redisTemplate
    ) {
        this.nimbusJwtService = nimbusJwtService;
        this.securityProperties = securityProperties;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 与 doFilter 的契约相同，但保证在单个请求线程中每个请求只调用一次。有关详细信息，请参阅 {@link #shouldNotFilterAsyncDispatch()}。
     */
    @Override
    @SuppressWarnings("all")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // 判断本次请求是否需要拦截
        for (String path : securityProperties.getWhitelist()) {
            if (ANT_PATH_MATCHER.match(path, request.getRequestURI())) {
                // 执行下一个 filter
                filterChain.doFilter(request, response);
                // return 是必须的
                return;
            }
        }
        // 校验请求是否正确携带 token
        Jwt jwt = nimbusJwtService.resolve(request);
        // 缓存用户信息到 SecurityContext
        String username = jwt.getClaim(OAuth2ParameterNames.USERNAME);
        User user = (User) redisTemplate.opsForValue().get(User.REDIS_KEY_PREFIX + username);
        if (user == null) {
            throw new AuthenticationServiceException("无法获取到用户信息");
        }
        var authorityList = user.getRoleList().stream().map(Role::getCode).map(code -> new SimpleGrantedAuthority("ROLE_" + code)).toList();
        var token = UsernamePasswordAuthenticationToken.authenticated(user, user.getPassword(), authorityList);
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                // 账号
                .withUsername(user.getUsername())
                // 密码
                .password(user.getPassword())
                // 用户角色
                .roles(user.getRoleList().stream().map(Role::getCode).toArray(String[]::new))
                // 是否锁定
                .accountLocked(user.getLocked())
                // 是否禁用
                .disabled(user.getDisabled())
                .build();
        token.setDetails(userDetails);
        SecurityContextHolder.getContext().setAuthentication(token);
        // 执行下一个 filter
        filterChain.doFilter(request, response);
    }

}
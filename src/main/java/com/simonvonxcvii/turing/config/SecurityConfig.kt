package com.simonvonxcvii.turing.config

import com.simonvonxcvii.turing.filter.CustomCaptchaOncePerRequestFilter
import com.simonvonxcvii.turing.filter.CustomJwtOncePerRequestFilter
import com.simonvonxcvii.turing.properties.SecurityProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.*
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * Spring Boot Security 用于用户操作验证
 *
 * @author Simon Von
 * @since 2022/5/1 14:45
 */
@Configuration
//@EnableWebSecurity // TODO 该注解在 Spring boot 项目中可以省略？
@EnableMethodSecurity
class SecurityConfig {
    /**
     * 密码明文加密方式配置
     *
     * @return 要使用的密码编码器
     * @author Simon Von
     * @since 2022/10/4 17:14
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        // 使用默认映射创建一个 DelegatingPasswordEncoder。 可能会添加其他映射，并且会更新编码以符合最佳实践。
        // 但是，由于 DelegatingPasswordEncoder 的性质，更新不应影响用户。 当前的映射是：BCryptPasswordEncoder
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    /**
     * 由基于提供的反应性请求提供 CorsConfiguration 实例的类（通常是 HTTP 请求处理程序）实现的接口。
     *
     * @return 接受过滤器使用的 CorsConfigurationSource 的构造函数，以查找要用于每个传入请求的 CorsConfiguration。
     * @author Simon Von
     * @since 2022/10/4 19:46
     */
    @Bean
    fun corsConfigurationSource(securityProperties: SecurityProperties): CorsConfigurationSource {
        // 用于 CORS 配置的容器以及用于检查给定请求的实际来源、HTTP 方法和标头的方法。
        // 默认情况下，新创建的 CorsConfiguration 不允许任何跨源请求，必须明确配置以指示应允许的内容。
        // 使用 applyPermitDefaultValues() 翻转初始化模型，以开放默认值开始，这些默认值允许 GET、HEAD 和 POST 请求的所有跨源请求。
        val config = CorsConfiguration().applyPermitDefaultValues()
        // 设置允许的来源
        // 允许跨源请求的源列表。默认情况下未设置，这意味着不允许任何来源
        config.allowedOrigins = listOf(securityProperties.host)
        // TODO 设置允许的原点模式
        // setAllowedOrigins 的替代方案，它支持更灵活的来源模式，除了端口列表之外，主机名中的任何位置都带有“*”。 例子：
        // https://*.domain1.com -- 以 domain1.com 结尾的域
        // https://*.domain1.com:[8080,8081] -- 在端口 8080 或端口 8081 上以 domain1.com 结尾的域
        // https://*.domain1.com:[*] -- 在任何端口上以 domain1.com 结尾的域，包括默认端口
        // 逗号分隔的模式列表，例如 "https://*.a1.com,https://*.a2.com";
        // 当通过属性占位符解析值时，这很方便，例如 "${原点}"; 请注意，此类占位符必须在外部解析。
        // 与仅支持“*”且不能与 allowCredentials 一起使用的 allowedOrigins 相比，当匹配 allowedOriginPattern 时，
        // Access-Control-Allow-Origin 响应标头将设置为匹配的来源，而不是“*”或模式。
        // 因此，allowedOriginPatterns 可以与设置为 true 的 setAllowCredentials 结合使用。
        // 默认情况下未设置。
//        config.setAllowedOriginPatterns(Collections.singletonList("http://localhost:552*"));
        // 设置允许的方法
        // 将 HTTP 方法设置为允许，例如 “GET”、“POST”、“PUT”等。
        // 特殊值“*”允许所有方法。
        // 如果未设置，则只允许使用“GET”和“HEAD”。
        // 默认情况下未设置。
        config.allowedMethods = listOf(CorsConfiguration.ALL)
        // 设置允许的请求头
        // 将飞行前请求可以列出的标头列表设置为允许在实际请求期间使用。
        // 特殊值“*”允许实际请求发送任何标头。
        // 如果标头名称是以下之一，则不需要列出：Cache-Control、Content-Language、Expires、Last-Modified 或 Pragma。
        // 默认情况下未设置。
//        config.allowedHeaders = listOf(CorsConfiguration.ALL)
        // TODO 设置暴露的请求头
        // 设置响应标头列表，而不是简单标头（即 Cache-Control、Content-Language、Content-Type、Expires、Last-Modified 或 Pragma）
        // 实际响应可能具有并且可以公开。
        // 特殊值“*”允许为非凭证请求公开所有标头。
        // 默认情况下未设置。
//        config.setExposedHeaders(Collections.singletonList(CorsConfiguration.ALL));
        // 设置允许凭据
        // 是否支持用户凭据。
        // 默认情况下未设置（即不支持用户凭据）。
        config.allowCredentials = true
        // 设置最大有效期
        // 配置客户端可以缓存飞行前请求的响应多长时间（以秒为单位）。
        // 默认情况下未设置。
//        config.maxAge = 1800
        // CorsConfigurationSource 使用 URL 模式为请求选择 CorsConfiguration。
        val source = UrlBasedCorsConfigurationSource()
        // 注册 Cors 配置
        // 为指定的路径模式注册一个 CorsConfiguration。
        // 添加映射路径，拦截一切请求
        source.registerCorsConfiguration("/**", config)
        // 接受过滤器使用的 CorsConfigurationSource 的构造函数，以查找要用于每个传入请求的 CorsConfiguration。
        return source
    }

    /**
     * 对登录、退出、页面的访问权限、静态资源的管理
     *
     * @param http HttpSecurity
     * @throws Exception 未知异常
     * @author Simon Von
     * @since 2022/5/1 14:50
     */
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        corsConfigurationSource: CorsConfigurationSource,
        authenticationSuccessHandler: AuthenticationSuccessHandler,
        authenticationFailureHandler: AuthenticationFailureHandler,
        customJwtOncePerRequestFilter: CustomJwtOncePerRequestFilter,
        customCaptchaOncePerRequestFilter: CustomCaptchaOncePerRequestFilter,
        securityProperties: SecurityProperties,
        accessDeniedHandler: AccessDeniedHandler,
        authenticationEntryPoint: AuthenticationEntryPoint,
        logoutSuccessHandler: LogoutSuccessHandler
    ): SecurityFilterChain {
        // 将安全标头添加到响应中。使用 EnableWebSecurity 时默认激活。接受 EnableWebSecurity 提供的默认值或仅调用 headers() 而不对其调用其他方法
//        http.headers { configurer: HeadersConfigurer<HttpSecurity> ->
//            configurer
//                // Adds a HeaderWriter instance
//                .addHeaderWriter(null)
//                // 配置插入 X-Content-Type-Options 的 XContentTypeOptionsHeaderWriter:
//                // X-Content-Type-Options: nosniff
//                .contentTypeOptions(null)
//                // 请注意，这不是全面的 XSS 保护！
//                // 允许自定义 XXssProtectionHeaderWriter 添加 X-XSS-Protection 标头
//                .xssProtection(null)
//                // 允许自定义 CacheControlHeadersWriter。 具体来说，它添加了以下标头：
//                // Cache-Control: no-cache, no-store, max-age=0, must-revalidate
//                // Pragma: no-cache
//                // Expires: 0
//                .cacheControl(null)
//                // 允许自定义为 HTTP 严格传输安全 (HSTS) 提供支持的 HstsHeaderWriter
//                .httpStrictTransportSecurity(null)
//                // 允许自定义 XFrameOptionsHeaderWriter。
//                .frameOptions(null)
//                // 允许自定义 HpkpHeaderWriter，它提供对 HTTP 公钥固定 (HPKP) 的支持
//                .httpPublicKeyPinning(null)
//                // 允许配置内容安全策略 (CSP) 级别 2。
//                // 调用此方法会使用提供的安全策略指令在响应中自动启用（包括）Content-Security-Policy 标头。
//                // 向 ContentSecurityPolicyHeaderWriter 提供了配置，它支持编写 W3C 候选建议中详述的两个标头：
//                // Content-Security-Policy
//                // Content-Security-Policy-Report-Only
//                .contentSecurityPolicy()
//                // 从响应中清除所有默认标头。 这样做之后，可以添加标头。 例如，如果你只想使用 Spring Security 的缓存控制，你可以使用以下内容：
//                // http.headers().defaultsDisabled().cacheControl();
//                .defaultsDisabled()
//                // 允许配置 Referrer Policy。
//                // 向 ReferrerPolicyHeaderWriter 提供了配置，它支持写入标头，如 W3C 技术报告中所述：
//                // Referrer-Policy
//                .referrerPolicy()
//                .permissionsPolicy()
//                .and()
//                // 允许配置 Cross-Origin-Opener-Policy 标头。
//                // 调用此方法会使用提供的策略在响应中自动启用（包括）Cross-Origin-Opener-Policy 标头。
//                // 配置提供给负责编写标头的 CrossOriginOpenerPolicyHeaderWriter。
//                .crossOriginOpenerPolicy(Customizer.withDefaults())
//                // 允许配置 Cross-Origin-Embedder-Policy 标头。
//                // 调用此方法会使用提供的策略在响应中自动启用（包括）Cross-Origin-Embedder-Policy 标头。
//                // 配置提供给负责编写标头的 CrossOriginEmbedderPolicyHeaderWriter。
//                .crossOriginEmbedderPolicy()
//                // 允许配置 Cross-Origin-Resource-Policy 标头。
//                // 调用此方法会使用提供的策略在响应中自动启用（包括）Cross-Origin-Resource-Policy 标头。
//                // 配置提供给负责编写标头的 CrossOriginResourcePolicyHeaderWriter：
//                .crossOriginResourcePolicy()
//                .disable()
//        }

        // 添加要使用的 CorsFilter。 如果提供了名为 corsFilter 的 bean，则使用该 CorsFilter。
        // 否则，如果定义了 corsConfigurationSource，则使用该 CorsConfiguration。
        // 否则，如果 Spring MVC 在类路径上，则使用 HandlerMappingIntrospector。
        http.cors { corsConfigurer: CorsConfigurer<HttpSecurity> ->
            corsConfigurer.configurationSource(corsConfigurationSource)
        }

        // 允许配置会话管理。todo 尝试启用，看有哪些好处
        http.sessionManagement { configurer: SessionManagementConfigurer<HttpSecurity> ->
            configurer
                // 设置此属性将向 SessionManagementFilter 注入配置有属性值的 SimpleRedirectInvalidSessionStrategy。
                // 当提交无效的会话 ID 时，将调用该策略，重定向到配置的 URL。
//                .invalidSessionUrl(null)
//                // 设置这意味着需要显式调用 SessionAuthenticationStrategy。
//                .requireExplicitAuthenticationStrategy(false)
//                // 设置此属性会将提供的 invalidSessionStrategy 注入到 SessionManagementFilter 中。 当提交无效的会话 ID 时，将调用该策略，重定向到配置的 URL。
//                .invalidSessionStrategy(null)
//                // 定义在 SessionAuthenticationStrategy 引发异常时应显示的错误页面的 URL。 如果未设置，将向客户端返回未授权 (402) 错误代码。
//                // 请注意，如果在基于表单的登录期间发生错误，则此属性不适用，其中身份验证失败的 URL 将优先。
//                .sessionAuthenticationErrorUrl(null)
//                // 定义将在 SessionAuthenticationStrategy 引发异常时使用的 AuthenticationFailureHandler。
//                // 如果未设置，将向客户端返回未授权 (402) 错误代码。 请注意，如果在基于表单的登录期间发生错误，则此属性不适用，其中身份验证失败的 URL 将优先。
//                .sessionAuthenticationFailureHandler(null)
//                // 如果设置为 true，则允许在使用 HttpServletResponse.encodeRedirectURL(String) 或 HttpServletResponse.encodeURL(String) 时
//                // 在 URL 中重写 HTTP 会话，否则不允许在 URL 中包含 HTTP 会话。 这可以防止信息泄露到外部域。
//                // 这是通过保护 HttpServletResponse.encodeURL 和 HttpServletResponse.encodeRedirectURL 调用来实现的。
//                // 任何同时重写这两种方法中的任何一种的代码，如 org.springframework.web.servlet.resource.ResourceUrlEncodingFilter，
//                // 都需要位于安全过滤器链之后，否则就有被跳过的风险。
//                .enableSessionUrlRewriting(false)
                // 允许指定 SessionCreationPolicy
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            // 允许明确指定 SessionAuthenticationStrategy。 默认是使用 ChangeSessionIdAuthenticationStrategy。
            // 如果配置了限制最大会话数，则 CompositeSessionAuthenticationStrategy 委托给 ConcurrentSessionControlAuthenticationStrategy，
            // 默认或提供的 SessionAuthenticationStrategy 和 RegisterSessionAuthenticationStrategy。
            // 注意：提供自定义 SessionAuthenticationStrategy 将覆盖默认会话固定策略。
//                .sessionAuthenticationStrategy(ConcurrentSessionControlAuthenticationStrategy(SessionRegistryImpl()))
//                // 允许配置会话固定保护。
//                .sessionFixation(Customizer.withDefaults())
//                // 控制用户的最大会话数。 默认是允许任意数量的用户。
//                .sessionConcurrency(null)
//                .disable()
        }

        // 允许配置可从 getSharedObject(Class) 获得的 PortMapper。
        // 当从 HTTP 重定向到 HTTPS 或从 HTTPS 重定向到 HTTP 时（例如，当与 requiresChannel() 结合使用时），
        // 其他提供的 SecurityConfigurer 对象使用此配置的 PortMapper 作为默认 PortMapper。
        // 默认情况下，Spring Security 使用 PortMapperImpl 将 HTTP 端口 8080 映射到 HTTPS 端口 8443 和 HTTP 端口 80 到 HTTPS 端口 443。
//        http.portMapper(httpSecurityPortMapperConfigurer -> httpSecurityPortMapperConfigurer
//                // 允许指定 PortMapper 实例。
//                .portMapper(new PortMapperImpl())
//                .disable()
//        );

        // 配置基于容器的预认证。 在这种情况下，身份验证由 Servlet 容器管理。
//        http.jee(httpSecurityJeeConfigurer -> httpSecurityJeeConfigurer
//                // 指定角色以使用从 HttpServletRequest 到 UserDetails 的映射。
//                // 如果 HttpServletRequest.isUserInRole(String) 返回 true，角色将添加到 UserDetails。
//                // 此方法等效于调用 mappableAuthorities(Set)。 mappableAuthorities(String...) 的多次调用将覆盖之前的调用。
//                // 没有映射的默认角色。
//                .mappableAuthorities()
//                // 指定角色以使用从 HttpServletRequest 到 UserDetails 的映射，并自动为其添加前缀“ROLE_”。
//                // 如果 HttpServletRequest.isUserInRole(String) 返回 true，角色将添加到 UserDetails。
//                // 此方法等效于调用 mappableAuthorities(Set)。 mappableRoles(String...) 的多次调用将覆盖之前的调用。
//                // 没有映射的默认角色。
//                .mappableRoles()
//                // 指定角色以使用从 HttpServletRequest 到 UserDetails 的映射。
//                // 如果 HttpServletRequest.isUserInRole(String) 返回 true，角色将添加到 UserDetails。
//                // 这相当于 mappableRoles(String...)。 mappableAuthorities(Set) 的多次调用将覆盖之前的调用。
//                // 没有映射的默认角色。
//                .mappableAuthorities((String) null)
//                // 指定与 PreAuthenticatedAuthenticationProvider 一起使用的 AuthenticationUserDetailsService。
//                // 默认是 PreAuthenticatedGrantedAuthoritiesUserDetailsService。
//                .authenticatedUserDetailsService(null)
//                // 允许指定要使用的 J2eePreAuthenticatedProcessingFilter。
//                // 如果提供了 J2eePreAuthenticatedProcessingFilter，则还必须手动配置其所有属性（即不使用 JeeConfigurer 中填充的所有属性）。
//                .j2eePreAuthenticatedProcessingFilter(null)
//                .disable()
//        );

        // 配置基于 X509 的预身份验证。
//        http.x509(httpSecurityX509Configurer -> httpSecurityX509Configurer
//                // 允许指定整个 X509AuthenticationFilter。 如果已指定，则不会在 X509AuthenticationFilter 上填充 X509Configurer 上的属性。
//                .x509AuthenticationFilter(null)
//                // 指定 X509PrincipalExtractor
//                .x509PrincipalExtractor(null)
//                // 指定 AuthenticationDetailsSource
//                .authenticationDetailsSource(null)
//                // 使用 UserDetailsByNameServiceWrapper 调用 authenticationUserDetailsService(AuthenticationUserDetailsService) 的快捷方式。
//                .userDetailsService(null)
//                // 指定要使用的 AuthenticationUserDetailsService。 如果未指定，则默认使用 UserDetailsService bean。
//                .authenticationUserDetailsService(null)
//                // 指定用于从证书中提取委托人的正则表达式。 如果未指定，则使用 SubjectDnX509PrincipalExtractor 中的默认表达式。
//                .subjectPrincipalRegex(null)
//                .disable()
//        );

        // 允许配置记住我身份验证。
//        http.rememberMe(httpSecurityRememberMeConfigurer -> httpSecurityRememberMeConfigurer
//                // 允许指定令牌的有效期（以秒为单位）
//                .tokenValiditySeconds(0)
//                // cookie 是否应标记为安全。 安全 cookie 只能通过 HTTPS 连接发送，因此不会意外地通过 HTTP 提交，以免被拦截。
//                // 默认情况下，如果请求是安全的，cookie 将是安全的。 如果您只想通过 HTTPS（推荐）使用 remember-me，您应该将此属性设置为 true。
//                .useSecureCookie(false)
//                // 指定用于在记住我令牌有效时查找 UserDetails 的 UserDetailsService。
//                // 使用 org.springframework.security.web.SecurityFilterChain bean 时，默认是查找 UserDetailsService bean。
//                // 或者，可以填充 rememberMeServices(RememberMeServices)。
//                .userDetailsService(null)
//                // 指定要使用的 PersistentTokenRepository。 默认是使用 TokenBasedRememberMeServices 代替。
//                .tokenRepository(null)
//                // 设置密钥以识别为记住我身份验证而创建的令牌。 默认是一个安全的随机生成的密钥。
//                // 如果指定了 rememberMeServices(RememberMeServices) 并且是 AbstractRememberMeServices 类型，则默认为 AbstractRememberMeServices 中设置的键。
//                .key(null)
//                // 用于指示在登录时记住用户的 HTTP 参数。
//                .rememberMeParameter(null)
//                // 存储用于记住我身份验证的令牌的 cookie 的名称。 默认为“记住我”。
//                .rememberMeCookieName(null)
//                // 记住我的 cookie 在其中可见的域名。
//                .rememberMeCookieDomain(null)
//                // 允许控制记住的用户在成功通过身份验证后发送到的目的地。
//                // 默认情况下，过滤器将只允许当前请求继续，但如果设置了 AuthenticationSuccessHandler，
//                // 它将被调用并且 doFilter() 方法将立即返回，从而允许应用程序将用户重定向到特定的 URL，而不管 最初的要求是什么。
//                .authenticationSuccessHandler(null)
//                // 指定要使用的 RememberMeServices。
//                .rememberMeServices(null)
//                // 即使未设置 remember-me 参数，是否始终创建 cookie。
//                // 默认情况下，这将设置为 false。
//                .alwaysRemember(false)
//                .disable()
//        );

        // TODO: 2023/6/21 白名单里的请求路径居然直接跳过了跨域，不受跨域的限制！？
        // 允许使用 RequestMatcher 实现（即通过 URL 模式）基于 HttpServletRequest 限制访问。
        http.authorizeHttpRequests { registry: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry ->
            registry
                // 如果 HandlerMappingIntrospector 在类路径中可用，则映射到不关心使用哪个 HttpMethod 的 MvcRequestMatcher。
                // 这个匹配器将使用 Spring MVC 用于匹配的相同规则。 例如，路径“/path”的映射通常会匹配“/path”、“/path/”、“/path.html”等。
                // 如果 HandlerMappingIntrospector 不可用，则映射到 AntPathRequestMatcher。
                .requestMatchers(*securityProperties.whitelist.toTypedArray())
                // 指定任何人都允许使用 URL。
                .permitAll()
                // 如果 HandlerMappingIntrospector 在类路径中可用，则映射到与特定 HttpMethod 匹配的 MvcRequestMatcher。
                // 这个匹配器将使用 Spring MVC 用于匹配的相同规则。 例如，路径“/path”的映射通常会匹配“/path”、“/path/”、“/path.html”等。
                // 如果 HandlerMappingIntrospector 不可用，则映射到 AntPathRequestMatcher。
                // 如果必须指定特定的 RequestMatcher，请改用 requestMatchers(RequestMatcher...)
                .requestMatchers(HttpMethod.OPTIONS)
                // 指定任何人都允许使用 URL。
                .permitAll()
                // 映射任何请求。
                .anyRequest()
                // 指定任何经过身份验证的用户都允许使用 URL。
                .authenticated()
            // 允许指定自定义 AuthorizationManager。
//                .access(AuthenticatedAuthorizationManager.authenticated())
        }

        // 允许配置请求缓存。 例如，在身份验证之前可能会请求受保护的页面 (/protected)。 该应用程序会将用户重定向到登录页面。
        // 身份验证后，Spring Security 会将用户重定向到最初请求的受保护页面（/protected）。 这在使用 EnableWebSecurity 时会自动应用。
//        http.requestCache(httpSecurityRequestCacheConfigurer -> httpSecurityRequestCacheConfigurer
//                // 允许显式配置要使用的 RequestCache。 默认尝试将 RequestCache 查找为共享对象。 然后退回到 HttpSessionRequestCache。
//                .requestCache(null)
//                .disable()
//        );

        // 允许配置异常处理。 这在使用 EnableWebSecurity 时会自动应用。
        http.exceptionHandling { configurer: ExceptionHandlingConfigurer<HttpSecurity> ->
            configurer
                // 指定要使用的 AccessDeniedHandler 的快捷方式是特定的错误页面
                //                .accessDeniedPage(null)
                // 指定要使用的 AccessDeniedHandler
                .accessDeniedHandler(accessDeniedHandler)
                // 设置要使用的默认 AccessDeniedHandler，它更喜欢为提供的 RequestMatcher 调用。
                // 如果仅指定了一个默认的 AccessDeniedHandler，它将用于默认的 AccessDeniedHandler。
                // 如果配置了多个默认的 AccessDeniedHandler 实例，则将使用 RequestMatcherDelegatingAccessDeniedHandler。
                //                .defaultAccessDeniedHandlerFor(accessDeniedHandler, null)
                // 设置要使用的 AuthenticationEntryPoint。
                // 如果未指定 authenticationEntryPoint(AuthenticationEntryPoint)，
                // 则将使用 defaultAuthenticationEntryPointFor(AuthenticationEntryPoint, RequestMatcher)。
                // 如果未找到匹配项，则第一个 AuthenticationEntryPoint 将用作默认值。
                // 如果未提供默认为 Http403ForbiddenEntryPoint。
                .authenticationEntryPoint(authenticationEntryPoint)
            // 设置要使用的默认 AuthenticationEntryPoint，它更喜欢为提供的 RequestMatcher 调用。
            // 如果仅指定了一个默认的 AuthenticationEntryPoint，它将用于默认的 AuthenticationEntryPoint。
            // 如果配置了多个默认 AuthenticationEntryPoint 实例，则将使用 DelegatingAuthenticationEntryPoint。
            //                .defaultAuthenticationEntryPointFor(authenticationEntryPoint, null)
        }

        // TODO: 2023/4/15 研究这里的作用
        // 在 HttpServletRequest 之间的 SecurityContextHolder 上设置 SecurityContext 的管理。 这在使用 EnableWebSecurity 时会自动应用。
//        http.securityContext(httpSecuritySecurityContextConfigurer -> httpSecuritySecurityContextConfigurer
//                // 指定要使用的共享 SecurityContextRepository
//                .securityContextRepository(null)
//                // 是否需要显式保存
//                .requireExplicitSave(false)
//                .disable()
//        );

        // 将 HttpServletRequest 方法与在 SecurityContext 上找到的值集成。 这在使用 EnableWebSecurity 时会自动应用。
//        http.servletApi(httpSecurityServletApiConfigurer -> httpSecurityServletApiConfigurer
//                .rolePrefix(null)
//                .disable()
//        );

        // TODO: 2023/4/9 研究是否值得启用，可能是要前端配合的
        // 启用 CSRF 保护。 在使用 EnableWebSecurity 的默认构造函数时默认激活。
        http.csrf { configurer: CsrfConfigurer<HttpSecurity> ->
            configurer
                // 指定要使用的 CsrfTokenRepository。 默认是由 LazyCsrfTokenRepository 包装的 HttpSessionCsrfTokenRepository。
                //                        .csrfTokenRepository(new HttpSessionCsrfTokenRepository())
                // 指定 RequestMatcher 用于确定何时应应用 CSRF。 默认是忽略 GET、HEAD、TRACE、OPTIONS 并处理所有其他请求。
                //                        .requireCsrfProtectionMatcher(CsrfFilter.DEFAULT_CSRF_MATCHER)
                // 指定 CsrfTokenRequestHandler 以用于使 CsrfToken 可用作请求属性。
                //                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                // 允许指定不应使用 CSRF 保护的 HttpServletRequests，即使它们匹配 requireCsrfProtectionMatcher(RequestMatcher)。
                .ignoringRequestMatchers("/getCaptcha")
                // 指定要使用的 SessionAuthenticationStrategy。 默认是 CsrfAuthenticationStrategy。
                //                        .sessionAuthenticationStrategy(new ConcurrentSessionControlAuthenticationStrategy(new SessionRegistryImpl()))
                .disable()
        }

        // 提供注销支持。 这在使用 EnableWebSecurity 时会自动应用。
        // 默认情况下，访问 URL“/logout”将通过使 HTTP 会话无效、清除配置的任何 rememberMe() 身份验证、清除 SecurityContextHolder，
        // 然后重定向到“/login?success”来注销用户。
        http.logout { configurer: LogoutConfigurer<HttpSecurity> ->
            configurer
                // 添加一个注销处理程序。
                // SecurityContextLogoutHandler 和 LogoutSuccessEventPublishingLogoutHandler 默认添加为最后一个 LogoutHandler 实例。
                //                .addLogoutHandler(logoutHandler)
                // 指定 SecurityContextLogoutHandler 是否应在注销时清除身份验证。
                // 默认 true
                //                .clearAuthentication(true)
                // 配置 SecurityContextLogoutHandler 以在注销时使 HttpSession 无效。
                // 默认 true
                //                .invalidateHttpSession(true)
                // 触发注销的 URL（默认为“/logout”）。
                // 如果启用 CSRF 保护（默认），则请求也必须是 POST。 这意味着默认情况下需要 POST "/logout" 来触发注销。
                // 如果禁用 CSRF 保护，则允许使用任何 HTTP 方法。
                // 对任何更改状态（即注销）的操作使用 HTTP POST 以防止 CSRF 攻击被认为是最佳实践。
                // 如果你真的想使用 HTTP GET，你可以使用 logoutRequestMatcher(new AntPathRequestMatcher(logoutUrl, "GET"));
                //                .logoutUrl("/api/logout")
                // 触发注销发生的 RequestMatcher。 在大多数情况下，用户将使用 logoutUrl(String) 这有助于实施良好做法。
                //                .logoutRequestMatcher(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/logout"))
                // 注销后重定向到的 URL。 默认值为“/login?logout”。
                // 这是使用 SimpleUrlLogoutSuccessHandler 调用 logoutSuccessHandler(LogoutSuccessHandler) 的快捷方式。
                //                .logoutSuccessUrl(null)
                // 以 true 作为参数的 permitAll(boolean) 的快捷方式。
                //                .permitAll()
                // 允许指定在注销成功时要删除的 cookie 的名称。
                // 这是使用 CookieClearingLogoutHandler 轻松调用 addLogoutHandler(LogoutHandler) 的快捷方式。
                .deleteCookies(
                    "Idea-2a3d4c",
                    "JSESSIONID",
                    "XSRF-TOKEN"
                )
                // 设置要使用的 LogoutSuccessHandler。 如果指定，则忽略 logoutSuccessUrl(String)。
                .logoutSuccessHandler(logoutSuccessHandler)
            // 设置要使用的默认 LogoutSuccessHandler，它更喜欢为提供的 RequestMatcher 调用。
            // 如果未指定 LogoutSuccessHandler，将使用 SimpleUrlLogoutSuccessHandler。
            // 如果配置了任何默认的 LogoutSuccessHandler 实例，则将使用默认为 SimpleUrlLogoutSuccessHandler 的 DelegatingLogoutSuccessHandler。
            //                .defaultLogoutSuccessHandlerFor(logoutSuccessHandler, null)
            // 授予每个用户对 logoutSuccessUrl(String) 和 logoutUrl(String) 的访问权限。
            //                .permitAll(false)
        }

        // 允许配置匿名用户的表示方式。 这在与 EnableWebSecurity 结合使用时会自动应用。
        // 默认情况下，匿名用户将使用 org.springframework.security.authentication.AnonymousAuthenticationToken 表示并包含角色“ROLE_ANONYMOUS”。
//        http.anonymous(httpSecurityAnonymousConfigurer -> httpSecurityAnonymousConfigurer
//                // 设置密钥以识别为匿名身份验证创建的令牌。 默认是一个安全的随机生成的密钥。
//                .key(null)
//                // 设置匿名用户身份验证对象的主体
//                .principal(null)
//                // 为匿名用户设置 Authentication.getAuthorities()
//                .authorities((List<GrantedAuthority>) null)
//                // 设置用于验证匿名用户的 AuthenticationProvider。
//                // 如果设置了此项，则不会在 AuthenticationProvider 上设置 AnonymousConfigurer 上的任何属性。
//                .authenticationProvider(null)
//                // 设置用于填充匿名用户的 AnonymousAuthenticationFilter。
//                // 如果设置了此项，则将不会在 AnonymousAuthenticationFilter 上设置 AnonymousConfigurer 上的任何属性。
//                .authenticationFilter(null)
//                .disable()
//        );

        // 指定支持基于表单的身份验证。 如果未指定 FormLoginConfigurer.loginPage(String)，将生成默认登录页面。
        http.formLogin { configurer: FormLoginConfigurer<HttpSecurity> ->
            configurer
                // 指定如果用户在身份验证之前未访问安全页面，则在成功进行身份验证后将重定向到何处。这是调用 defaultSuccessUrl(String, boolean) 的快捷方式。
                //                .defaultSuccessUrl(null)
                //                // 指定如果用户在身份验证之前未访问安全页面或始终使用 true，则在成功进行身份验证后将重定向到何处。这是调用 successHandler(AuthenticationSuccessHandler) 的快捷方式。
                //                .defaultSuccessUrl(null, true)
                //                // 指定用于验证凭据的 URL。
                //                .loginProcessingUrl(null)
                //                // 指定用于在请求之间持久化安全上下文的策略。
                //                .securityContextRepository(null)
                //                // 指定自定义身份验证详细信息源。默认值为 WebAuthenticationDetailsSource。
                //                .authenticationDetailsSource(null)
                // 指定要使用的身份验证成功处理程序。默认值为 SavedRequestAwareAuthenticationSuccessHandler，未设置其他属性。
                .successHandler(authenticationSuccessHandler)
                // 身份验证失败时向用户发送的 URL。这是调用 failureHandler（AuthenticationFailureHandler）的快捷方式。默认值为“/login？error”。
                //                .failureUrl(null)
                // 指定身份验证失败时要使用的 AuthenticationFailureHandler。
                // 默认使用 SimpleUrlAuthenticationFailureHandler 重定向到“/login?error”
                .failureHandler(authenticationFailureHandler)
        }

        // 使用 SAML 2.0 服务提供程序配置身份验证支持。 “身份验证流程”是使用 Web 浏览器 SSO 配置文件、POST 和重定向绑定实现的，
        // 如 SAML V2.0 核心、配置文件和绑定规范中所述。 使用此功能的先决条件是，您具有 SAML v2.0 身份提供程序来提供断言。
        // 服务提供商、信赖方和远程身份提供程序（断言方）的表示形式包含在信赖方注册中。
        // RelyingPartyRegistration（s）由RelyingPartyRegistrationRepository组成，该存储库是必需的，
        // 必须向ApplicationContext注册或通过saml2Login（）.relyingPartyRegistrationRepository（..）进行配置。
        // 默认配置在“/login”处提供自动生成的登录页面，并在发生身份验证错误时重定向到“/login？error”。
        // 登录页面将显示每个身份提供程序，其中包含一个能够启动“身份验证流”的链接。
//        http.saml2Login(httpSecuritySaml2LoginConfigurer -> httpSecuritySaml2LoginConfigurer
//                // 在将传入请求转换为身份验证时使用此身份验证转换器。默认情况下，使用 Saml2AuthenticationTokenConverter。
//                .authenticationConverter(null)
//                // 提供要在 SAML 2 身份验证期间使用的身份验证管理器的配置。如果未指定任何内容，系统将创建一个将其注入 Saml2WebSsoAuthenticationFilter
//                .authenticationManager(null)
//                // 设置信赖方、代表服务提供商、SP 和此主机的每个参与方以及相互通信的标识提供者、IDP 对的依赖方注册存储库。
//                .relyingPartyRegistrationRepository(null)
//                // 登录页面
//                .loginPage(null)
//                // 使用此 Saml2AuthenticationRequestResolver 生成 SAML 2.0 身份验证请求。
//                .authenticationRequestResolver(null)
//                // 自定义 SAML 身份验证请求将发送到的 URL。
//                .authenticationRequestUri(null)
//                // 指定用于验证凭据的 URL。如果指定了自定义 URL，请考虑通过 authenticationConverter（AuthenticationConverter）指定自定义 AuthenticationConverter，
//                // 因为默认的 AuthenticationConverter 实现依赖于 URL 中存在的 {registrationId} 路径变量
//                .loginProcessingUrl(null)
//                // 通过删除 AbstractHttpConfigurer 来禁用它。执行此操作后，可以应用新版本的配置。
//                .disable()
//        );

        // 图 SAML 2.0 信赖方的注销支持。 使用 POST 和重定向绑定实现单注销配置文件，如 SAML V2.0 核心、配置文件和绑定规范中所述。
        // 作为使用此功能的先决条件，您必须有一个 SAML v2.0 断言方向其发送注销请求。信赖方和主张方的表示包含在信赖方注册中。
        // RelyingPartyRegistration（s）由RelyingPartyRegistrationRepository组成，该存储库是必需的，
        // 必须向ApplicationContext注册或通过saml2Login（Customizer）进行配置。
        // 默认配置在“/logout”处提供自动生成的注销端点，并在注销完成后重定向到 /login？logout。
//        http.saml2Logout(httpSecuritySaml2LogoutConfigurer -> httpSecuritySaml2LogoutConfigurer
//                // 信赖方或断言方可通过其触发注销的 URL。
//                // 信赖方通过发布到终结点来触发注销。
//                // 断言方根据 RelyingPartyRegistration.getSingleLogoutServiceBindings（） 指定的内容触发注销。
//                .logoutUrl(null)
//                // 设置信赖方、代表服务提供商、SP 和此主机的每个参与方以及相互通信的标识提供者、IDP 对的依赖方注册存储库。
//                .relyingPartyRegistrationRepository(null)
//                // 配置 SAML 2.0 注销请求组件
//                .logoutRequest(null)
//                // 配置 SAML 2.0 注销响应组件
//                .logoutResponse(null)
//                // 通过删除 AbstractHttpConfigurer 来禁用它。执行此操作后，可以应用新版本的配置。
//                .disable()
//        );

        // 配置 SAML 2.0 元数据终结点，该终结点在 <md：EntityDescriptor> 有效负载中显示信赖方配置。
        // 默认情况下，端点是 /saml2/metadata 和 /saml2/metadata/{registrationId}，
        // 但请注意，出于向后兼容性目的，还会识别 /saml2/service-provider-metadata/{registrationId}。
//        http.saml2Metadata(httpSecuritySaml2MetadataConfigurer -> httpSecuritySaml2MetadataConfigurer
//                // 使用此终结点请求信赖方元数据。
//                // 如果在 URL 中指定 registrationId 占位符，则筛选器将使用该占位符查找 RelyingPartyRegistration。
//                // 如果没有 registrationId，并且您的 RelyingPartyRegistrationRepository 是 {code Iterable}，
//                // 则元数据终结点将尝试在单个 <md：EntitiesDecriptor 元素中显示所有信赖方的元数据。
//                // 如果需要比这些更复杂的查找策略，请改用元数据响应解析程序。
//                .metadataUrl(null)
//                // Use this Saml2MetadataResponseResolver to parse the request and respond with SAML 2.0 metadata.
//                // 使用此 Saml2MetadataResponseResolver 解析请求并使用 SAML 2.0 元数据进行响应。
//                .metadataResponseResolver(null)
//                // 通过删除 AbstractHttpConfigurer 来禁用它。执行此操作后，可以应用新版本的配置。
//                .disable()
//        );

        // 使用 OAuth 2.0 和/或 OpenID Connect 1.0 提供程序配置身份验证支持。
        // 正如 OAuth 2.0 授权框架和 OpenID Connect Core 1.0 规范中所指定的那样，“身份验证流程”是使用授权码授予来实现的。
        // 作为使用此功能的先决条件，您必须向提供商注册客户端。
        // 客户端注册信息然后可以用于使用 org.springframework.security.oauth2.client.registration.ClientRegistration.Builder
        // 配置 org.springframework.security.oauth2.client.registration.ClientRegistration。
        // org.springframework.security.oauth2.client.registration.ClientRegistration(s)
        // 由 org.springframework.security.oauth2.client.registration.ClientRegistrationRepository 组成，
        // 它是必需的并且必须在 ApplicationContext 中注册或通过 oauth2Login() 配置 .clientRegistrationRepository(..)。
        // 默认配置在“/login”提供一个自动生成的登录页面，并在发生身份验证错误时重定向到“/login?error”。
        // 登录页面将向每个客户端显示一个能够启动“身份验证流程”的链接。
//        http.oauth2Login(httpSecurityOAuth2LoginConfigurer -> httpSecurityOAuth2LoginConfigurer
//                // 设置客户端注册的存储库。
//                .clientRegistrationRepository(null)
//                // 设置授权客户端的存储库。
//                .authorizedClientRepository(null)
//                // 为授权客户端设置服务。
//                .authorizedClientService(null)
//                // 登陆页面
//                .loginPage(null)
//                // 登陆处理页面
//                .loginProcessingUrl(null)
//                // 配置授权服务器的授权端点。
//                .authorizationEndpoint(null)
//                // 配置授权服务器的令牌端点。
//                .tokenEndpoint(null)
//                // 配置客户端的重定向端点。
//                .redirectionEndpoint(null)
//                // 配置授权服务器用户信息端点
//                .userInfoEndpoint(null)
//                // 通过删除 AbstractHttpConfigurer 来禁用它。执行此操作后，可以应用新版本的配置。
//                .disable()
//        );

        // 配置 OAuth 2.0 客服端支持
//        http.oauth2Client(httpSecurityOAuth2ClientConfigurer -> httpSecurityOAuth2ClientConfigurer
//                // 设置客服端注册仓库
//                .clientRegistrationRepository(null)
//                // 设置授权客户端的存储库。
//                .authorizedClientRepository(null)
//                // 设置授权客服端的服务
//                .authorizedClientService(null)
//                // 配置 OAuth 2.0 授权代码授予。
//                .authorizationCodeGrant(null)
//                // 通过删除 AbstractHttpConfigurer 来禁用它。执行此操作后，可以应用新版本的配置。
//                .disable()
//        );

        // 配置 OAuth 2.0 资源服务器支持
//        http.oauth2ResourceServer { httpSecurityOAuth2ResourceServerConfigurer ->
//            httpSecurityOAuth2ResourceServerConfigurer
//                .accessDeniedHandler(accessDeniedHandler)
//                .authenticationEntryPoint(authenticationEntryPoint)
//                .authenticationManagerResolver(null)
//                .bearerTokenResolver(null)
//                // 启用 JWT 编码的持有者令牌支持。
//                .jwt(null)
//                // 启用不透明持有者令牌支持。
//                .opaqueToken(null)
//                // 通过删除 AbstractHttpConfigurer 来禁用它。执行此操作后，可以应用新版本的配置。
//                .disable()
//        };

        // 配置通道安全性。为了使此配置有用，必须至少提供一个到所需通道的映射。
//        http.requiresChannel(channelRequestMatcherRegistry -> channelRequestMatcherRegistry
//                // 为此类添加一个 ObjectPostProcessor
//                .withObjectPostProcessor(null)
//                // 设置要在 ChannelDecisionManagerImpl 中使用的 ChannelProcessor 实例
//                .channelProcessors(null)
//                // 设置要在 RetryWithHttpEntryPoint 和 RetryWithHttpsEntryPoint 中使用的 RedirectStrategy 实例
//                .redirectStrategy(null)
//        );

        // 配置 HTTP Basic 身份验证
//        http.httpBasic(httpSecurityHttpBasicConfigurer -> httpSecurityHttpBasicConfigurer
//                // 允许轻松更改领域，但保留剩余的默认值。如果已调用 authenticationEntryPoint（AuthenticationEntryPoint），则调用此方法将导致错误。
//                .realmName(null)
//                // 身份验证入口点在身份验证失败时填充在 BasicAuthenticationFilter 上。默认将 BasicAuthenticationEntryPoint 与领域“Realm”一起使用。
//                .authenticationEntryPoint(authenticationEntryPoint)
//                // 指定用于基本身份验证的自定义身份验证详细信息源。默认值为 WebAuthenticationDetailsSource。
//                .authenticationDetailsSource(null)
//                // 指定用于基本身份验证的自定义 SecurityContextRepository。默认值为 RequestAttributeSecurityContextRepository。
//                .securityContextRepository(null)
//                // 通过删除 AbstractHttpConfigurer 来禁用它。执行此操作后，可以应用新版本的配置。
//                .disable()
//        );

        // 添加对密码管理的支持。
//        http.passwordManagement(httpSecurityPasswordManagementConfigurer -> httpSecurityPasswordManagementConfigurer
//                // 设置更改密码页面。默认为 DEFAULT_CHANGE_PASSWORD_PAGE。
//                .changePasswordPage("/change-password")
//                // 通过删除 AbstractHttpConfigurer 来禁用它。执行此操作后，可以应用新版本的配置。
//                .disable()
//        );

        // 配置默认的 AuthenticationManager.
//        http.authenticationManager(null);

        // 设置由多个 SecurityConfigurer 共享的对象。
//        http.setSharedObject(null, null);

        // 允许添加额外的 AuthenticationProvider 以供使用
//        http.authenticationProvider(null);

        // 允许添加额外的 UserDetailsService 以供使用
//        http.userDetailsService(null);

        // 将过滤器实例添加到指定过滤器类之后
        // 允许在已知筛选器类之一之后添加筛选器。
        // 已知的筛选器实例是 addFilter(Filter) 中列出的筛选器，或者是已使用 addFilterAfter(Filter, Class) 或 addFilterBefore(Filter, Class) 添加的筛选器。
        http.addFilterAfter(customJwtOncePerRequestFilter, UsernamePasswordAuthenticationFilter::class.java)

        // 允许在已知筛选器类之一之前添加筛选器。
        // 已知的筛选器实例是 addFilter（Filter） 中列出的筛选器，或者是已使用 addFilterAfter（Filter， Class） 或 addFilterBefore（Filter， Class） 添加的筛选器。
        http.addFilterBefore(customCaptchaOncePerRequestFilter, UsernamePasswordAuthenticationFilter::class.java)

        // 添加一个筛选器，该筛选器必须是安全框架中提供的筛选器的实例或扩展其中一个筛选器。该方法可确保自动处理筛选器的顺序。
//        http.addFilter();

        // 在指定筛选器类的位置添加筛选器。例如，如果您希望筛选器 CustomFilter 注册到与 UsernamePasswordAuthenticationFilter 相同的位置，则可以调用：
        // addFilterAt（new CustomFilter（）， UsernamePasswordAuthenticationFilter.class）
        // 在同一位置注册多个筛选器意味着它们的排序不是确定的。更具体地说，在同一位置注册多个筛选器不会覆盖现有筛选器。相反，不要注册您不想使用的筛选器。
//        http.addFilterAt();

        // 允许指定将在哪些 HttpServletRequest 实例上调用此 HttpSecurity。
        // 此方法允许为多个不同的 RequestMatcher 实例轻松调用 HttpSecurity。
        // 如果只需要一个 RequestMatcher，请考虑使用 securityMatcher（String...） 或 securityMatcher（RequestMatcher）。
        // 调用 securityMatchers（Customizer） 不会覆盖以前对 securityMatchers（）、
        // securityMatchers（Customizer） securityMatcher（String...） 和 securityMatcher（RequestMatcher） 的调用
//        http.securityMatchers(AbstractRequestMatcherRegistry::dispatcherTypeMatchers);

        // 允许将 HttpSecurity 配置为仅在匹配提供的 RequestMatch 时调用。如果需要更高级的配置，请考虑使用 securityMatchers（Customizer） （）。
        // 调用 securityMatcher（RequestMatcher） 将覆盖之前对 securityMatcher（RequestMatcher）、securityMatcher（String...）、
        // securityMatchers（Customizer） 和 securityMatchers（） 的调用
//        http.securityMatcher((RequestMatcher) null);

        // 允许将 HttpSecurity 配置为仅在匹配提供的模式时调用。如果 Spring MVC 在类路径中，则此方法创建一个 MvcRequestMatcher，如果没有，
        // 则创建 AntPathRequestMatcher。如果需要更高级的配置，请考虑使用 securityMatchers（Customizer） 或 securityMatcher（RequestMatcher）。
        // 调用 securityMatcher（String...） 将覆盖之前对 securityMatcher（String...） 的调用（String）}}，
        // securityMatcher（RequestMatcher） （）}， securityMatchers（Customizer） （String）} and securityMatchers（） （String）}.
//        http.securityMatcher((String) null);
        return http.build()
    }
}

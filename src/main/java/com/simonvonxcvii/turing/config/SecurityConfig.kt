package com.simonvonxcvii.turing.config

import com.simonvonxcvii.turing.filter.CustomCaptchaOncePerRequestFilter
import com.simonvonxcvii.turing.properties.CustomSecurityProperties
import com.simonvonxcvii.turing.utils.Constants
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy
import org.springframework.security.web.csrf.CsrfFilter
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher
import org.springframework.security.web.util.matcher.AnyRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.web.cors.CorsConfigurationSource

/**
 * Spring Boot Security 用于用户操作验证
 * todo 考虑将项目的安全、认证、授权等服务集成到另外一个模块（或者子项目）
 *
 * @author Simon Von
 * @since 2022/5/1 14:45
 */
@Configuration(proxyBeanMethods = false)
@EnableWebSecurity // TODO 该注解在 Spring boot 项目中可以省略？
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

//    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
//        val rolesConverter = JwtGrantedAuthoritiesConverter().apply {
//            setAuthoritiesClaimName("realm_access.roles")
//            setAuthorityPrefix("ROLE_")
//        }
//
//        return JwtAuthenticationConverter().apply {
//            setJwtGrantedAuthoritiesConverter(rolesConverter)
//        }
//    }

    /**
     * 对登录、退出、页面的访问权限、静态资源的管理
     *
     * @author Simon Von
     * @since 2022/5/1 14:50
     */
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        corsConfigurationSource: CorsConfigurationSource,
        authenticationSuccessHandler: AuthenticationSuccessHandler,
        authenticationFailureHandler: AuthenticationFailureHandler,
        customCaptchaOncePerRequestFilter: CustomCaptchaOncePerRequestFilter,
        customSecurityProperties: CustomSecurityProperties,
        accessDeniedHandler: AccessDeniedHandler,
        authenticationEntryPoint: AuthenticationEntryPoint,
        logoutSuccessHandler: LogoutSuccessHandler,
    ): SecurityFilterChain {
        http {
            // 允许配置 HttpSecurity 仅在匹配提供的模式时调用。
            // 如果 Spring MVC 位于 Classpath 中，它将使用 MVC 匹配器。如果 Spring MVC 不在 Classpath 中，它将使用 Ant 匹配器。
//            securityMatcher("")

            // 允许配置 HttpSecurity 仅在匹配提供的 RequestMatcher 时调用。
//            securityMatcher(pathPattern("/private/&ast;&ast;"))

            // 启用基于表单的身份验证。
            formLogin {
                // 如果需要身份验证则重定向到登录页面（即“/login”）
//                loginPage = "/api/auth/login"
                // 身份验证成功后使用的 AuthenticationSuccessHandler
                this.authenticationSuccessHandler = authenticationSuccessHandler
                // 身份验证失败后使用的 AuthenticationFailureHandler
                this.authenticationFailureHandler = authenticationFailureHandler
                // 身份验证失败时发送给用户的 URL
//                failureUrl = null
//                // 用于验证凭证的 URL
                loginProcessingUrl = Constants.LOGIN_URL
//                // 是否授予每个用户对 failureUrl 以及 HttpSecurityBuilder、loginPage 和 loginProcessingUrl 的访问权限
//                permitAll = true
//                // 为给定的 Web 请求提供 org.springframework.security.core.Authentication.getDetails() 对象。
//                authenticationDetailsSource = null
//                // 执行身份验证时查找用户名的 HTTP 参数
//                usernameParameter = null
//                // 执行身份验证时查找密码的 HTTP 参数
//                passwordParameter = null
//                // 禁用 FormLogin。
//                disable()
//                // 授予每个用户的失败 URL 以及 HttpSecurityBuilder、loginPage 和 loginProcessingUrl 的访问权限。
//                permitAll()
//                // 如果用户在身份验证之前没有访问过安全页面或者 alwaysUse 为真，则指定用户身份验证成功后将被重定向到哪里。
//                defaultSuccessUrl("", true)
            }

            // TODO: 2023/6/21 白名单里的请求路径居然直接跳过了跨域，不受跨域的限制！？
            // 允许根据 HttpServletRequest 限制访问
            authorizeHttpRequests {
                // 如果传入的任何 RequestMatcher 实例匹配，则 RequestMatcher 将返回 true。
                val matches = OrRequestMatcher(customSecurityProperties.whitelist.map {
                    PathPatternRequestMatcher.withDefaults().matcher(it)
                })
                // 为与指定模式匹配的端点添加请求授权规则。如果 Spring MVC 位于 Classpath 中，它将使用 MVC 匹配器。
                // 如果 Spring MVC 不在 Classpath 中，它将使用 Ant 匹配器。MVC 将使用与 Spring MVC 相同的匹配规则。
                // 例如，路径“/path”的映射通常会匹配“/path”、“/path/”、“/path.html”等。
                // 如果 Spring MVC 不处理当前请求，则将使用合理的默认值，即使用 Ant 模式。
                authorize(matches, permitAll)
                authorize(HttpMethod.OPTIONS, "/**", permitAll)
                authorize(anyRequest, authenticated)
                // 为与指定模式匹配的端点添加请求授权规则。如果 Spring MVC 位于 Classpath 中，它将使用 MVC 匹配器。
                // 如果 Spring MVC 不在 Classpath 中，它将使用 Ant 匹配器。MVC 将使用与 Spring MVC 相同的匹配规则。
                // 例如，路径“/path”的映射通常会匹配“/path”、“/path/”、“/path.html”等。
                // 如果 Spring MVC 不处理当前请求，则将使用合理的默认值，即使用 Ant 模式。
//                authorize(OrRequestMatcher(), authenticated)
//                authorize("", authenticated)
//                authorize(HttpMethod.OPTIONS, "", authenticated)
//                authorize("", "", authenticated)
//                authorize(HttpMethod.OPTIONS, "", "", authenticated)
//                hasAuthority("ROLE_ADMIN")
//                hasAnyAuthority("ROLE_ADMIN")
//                hasAllAuthorities("ROLE_ADMIN")
//                hasRole("ROLE_ADMIN")
//                hasAnyRole("ROLE_ADMIN")
//                hasAllRoles("ROLE_ADMIN")
//                hasIpAddress("ROLE_ADMIN")
            }

            // 启用 HTTP 基本身份验证。 todo 尝试启用
//            httpBasic {
//                // 要使用的 HTTP 基本领域。如果已调用 authenticationEntryPoint，则调用此方法将导致错误。
//                realmName = null
//                // 如果身份验证失败，则在 BasicAuthenticationFilter 上填充 AuthenticationEntryPoint。
//                this.authenticationEntryPoint = authenticationEntryPoint
//                // 用于基本身份验证的自定义 AuthenticationDetails 源。
//                this.authenticationDetailsSource = null
//                // 禁用 HTTP 基本身份验证
//                disable()
//            }

            // 启用密码管理。
//            passwordManagement {
//                // 更改密码页面。
//                changePasswordPage = ("/change-password")
//            }

            // 允许配置响应标头。
//            headers {
//                defaultsDisabled = true
//                // 配置 XContentTypeOptionsHeaderWriter 插入 <a href= "https://msdn.microsoft.com/en-us/library/ie/gg622941(v=vs.85).aspx"
//                contentTypeOptions {
//                    // 禁用 X-Content-Type-Options 标头。
//                    disable()
//                }
//                // 请注意，这不是全面的 XSS 保护！
//                xssProtection {
//                    // X-XSS-Protection 标头的值。OWASP 建议使用 HeaderValue.DISABLED。
//                    headerValue = XXssProtectionHeaderWriter.HeaderValue.DISABLED
//                    // 不要在响应中包含 X-XSS-Protection 标头。
//                    disable()
//                }
//                // 允许自定义 CacheControlHeadersWriter。具体来说，它添加了以下标头：
//                cacheControl {
//                    // 禁用缓存控制标头。
//                    disable()
//                }
//                // 允许自定义 HstsHeaderWriter，它为 <a href="https://tools.ietf.org/html/rfc6797">HTTP 严格传输安全 (HSTS) 提供支持。
//                httpStrictTransportSecurity {
//                    // Strict-Transport-Security 标头的 max-age 指令的值（以秒为单位）。
//                    maxAgeInSeconds = null
//                    // RequestMatcher 用于确定是否应添加“Strict-Transport-Security”标头。如果为 true，则添加标头，否则不添加标头。
//                    requestMatcher = null
//                    // 如果为真，子域也应被视为 HSTS 主机。
//                    includeSubDomains = null
//                    // 如果为真，预加载将包含在 HSTS 标头中。
//                    preload = null
//                    // 禁用 HTTP 严格传输安全标头。
//                    disable()
//                }
//                // 允许自定义 XFrameOptionsHeaderWriter 来添加 X-Frame-Options 标头。
//                frameOptions {
//                    // 允许来自同一来源的任何请求构建该应用程序。
//                    sameOrigin = true
//                    // 拒绝从该应用程序构建任何内容。
//                    deny = true
//                    // 禁用 X-Frame-Options 标头。
//                    disable()
//                }
//                // 允许自定义 HpkpHeaderWriter，它为 <a href="https://tools.ietf.org/html/rfc7469">HTTP 公钥固定 (HPKP) 提供支持。
//                httpPublicKeyPinning {
//                    // Public-Key-Pins 标头的 pin- 指令的值。
//                    pins = null
//                    // Public-Key-Pins 标头的 max-age 指令的值（以秒为单位）。
//                    maxAgeInSeconds = null
//                    // 如果为真，则固定策略适用于此固定主机以及主机域名的任何子域。
//                    includeSubDomains = null
//                    // 如果为真，则浏览器不应终止与服务器的连接。
//                    reportOnly = null
//                    // 浏览器应向其报告 PIN 验证失败的 URI。
//                    reportUri = null
//                    // 禁用 HTTP 公钥固定标头。
//                    disable()
//                }
//                // 允许配置内容安全策略 (CSP) 级别 2。
//                contentSecurityPolicy {
//                    // 响应头中要使用的安全策略指令。
//                    policyDirectives = null
//                    // 在响应中包含 Content-Security-Policy-Report-Only 标头。
//                    reportOnly = null
//                }
//                // 允许配置引荐来源策略。
//                referrerPolicy {
//                    // 响应头中要使用的策略。
//                    policy = null
//                }
//                // 允许配置权限策略。
//                permissionsPolicy {
//                    // 响应头中要使用的策略。
//                    policy = null
//                }
//                // 允许配置 Cross-Origin-Opener-Policy 标头。
//                crossOriginOpenerPolicy {
//                    // 响应头中要使用的策略。
//                    policy = null
//                }
//                // 允许配置 Cross-Origin-Embedder-Policy 标头。
//                crossOriginEmbedderPolicy {
//                    // 响应头中要使用的策略。
//                    policy = null
//                }
//                // 配置 Cross-Origin-Resource-Policy 标头。
//                crossOriginResourcePolicy {
//                    // 响应头中要使用的策略。
//                    policy = null
//                }
//                // 添加 HeaderWriter 实例。
//                addHeaderWriter(XXssProtectionHeaderWriter())
//                // 禁用所有 HTTP 安全标头。
//                disable()
//            }

            // 启用 CORS。
            cors {
                // 要使用的 CorsConfigurationSource。
                configurationSource = corsConfigurationSource
                // 禁用 CORS。
//                disable()
            }

            // 允许配置会话管理。
//            sessionManagement {
//                invalidSessionUrl = null
//                invalidSessionStrategy = null
//                sessionAuthenticationErrorUrl = null
//                sessionAuthenticationFailureHandler = null
//                enableSessionUrlRewriting = null
//                requireExplicitAuthenticationStrategy = null
//                sessionCreationPolicy = null
//                sessionAuthenticationStrategy = null
//                // 启用会话固定保护。
//                sessionFixation {
//                    // 指定应创建一个新会话，但不应保留原始 HttpSession 的会话属性。
//                    newSession()
//                    // 指定应创建一个新会话，并保留原始 HttpSession 的会话属性。
//                    migrateSession()
//                    // 指定应使用 Servlet 容器提供的会话固定保护。
//                    // 会话身份验证时，将调用 Servlet 方法 HttpServletRequest.changeSessionId 来更改会话 ID 并保留所有会话属性。
//                    changeSessionId()
//                    // 指定不应启用会话固定保护。
//                    none()
//                }
//                // 控制用户的多个会话的行为。
//                sessionConcurrency {
//                    // 控制用户的最大会话数。
//                    maximumSessions = null
//                    // 如果用户尝试访问资源并且由于当前用户的会话过多而导致其会话已过期，则重定向到的 URL。
//                    expiredUrl = null
//                    // 确定检测到过期会话时的行为。
//                    expiredSessionStrategy = null
//                    // 如果设置为 true，则在达到 maximumSessions 限制时阻止用户进行身份验证。
//                    // 否则（默认），已进行身份验证的用户将被允许访问，并且现有用户的会话将过期。
//                    maxSessionsPreventsLogin = null
//                    // 使用的 SessionRegistry 实现。
//                    sessionRegistry = null
//                    maximumSessions(SessionLimit.UNLIMITED)
//                }
//            }

            // 允许配置端口映射器。
//            portMapper {
//                // 允许指定 PortMapper 实例。
//                portMapper = null
//                // 向端口映射器添加映射。
//                map(80, 443)
//            }

            // 配置通道安全性。为了使此配置有用，必须提供至少一个到所需通道的映射。
//            redirectToHttps {
//                // 指定要重定向到的自定义 HTTPS 端口的 PortMapper。
//                requestMatchers = null
//            }

            // 配置基于 X509 的预身份验证。
//            x509 {
//                // 整个 X509AuthenticationFilter。如果指定了此项，则 X509Configurer 上的属性将不会填充到 {@link X509AuthenticationFilter} 上。
//                x509AuthenticationFilter
//                x509PrincipalExtractor
//                authenticationDetailsSource
//                // 使用 UserDetailsByNameServiceWrapper 调用 authenticationUserDetailsService 的快捷方式
//                userDetailsService
//                // 要使用的 AuthenticationUserDetailsService
//                authenticationUserDetailsService
//            }

            // 启用请求缓存。具体来说，这可以确保已保存的请求（即在需要身份验证之后）稍后能够重放。
//            requestCache {
//                // 允许明确配置要使用的 RequestCache
//                requestCache = null
//            }

            // 允许配置异常处理。
            exceptionHandling {
                // 拒绝访问页面的 URL
//                accessDeniedPage = "/access-denied"
                // 要使用的 AccessDeniedHandler
                this.accessDeniedHandler = accessDeniedHandler
                // 要使用的 AuthenticationEntryPoint
                this.authenticationEntryPoint = authenticationEntryPoint
                // 设置要使用的默认 AccessDeniedHandler，该默认 AccessDeniedHandler 优先为提供的 RequestMatcher 调用。
//                defaultAccessDeniedHandlerFor(accessDeniedHandler, AnyRequestMatcher.INSTANCE)
//                // 设置要使用的默认 AuthenticationEntryPoint，该默认 AuthenticationEntryPoint 优先为提供的 RequestMatcher 调用。
//                defaultAuthenticationEntryPointFor(authenticationEntryPoint, AnyRequestMatcher.INSTANCE)
//                // 禁用异常处理。
//                disable()
            }

            // TODO: 2023/4/9 研究是否值得启用，可能是要前端配合的
            // 启用 CSRF 保护。
            csrf {
                // 要使用的 CsrfTokenRepository。
                csrfTokenRepository = HttpSessionCsrfTokenRepository()
                // 指定用于确定何时应用 CSRF 的 RequestMatcher。
                requireCsrfProtectionMatcher = CsrfFilter.DEFAULT_CSRF_MATCHER
                // 要使用的 SessionAuthenticationStrategy。
                sessionAuthenticationStrategy = ConcurrentSessionControlAuthenticationStrategy(SessionRegistryImpl())
                // CsrfTokenRequestHandler 用于将 CSRF 令牌作为请求属性使用
                csrfTokenRequestHandler = CsrfTokenRequestAttributeHandler()
                // 允许指定不应使用 CSRF 保护的 HttpServletRequests，即使它们与 requireCsrfProtectionMatcher 匹配。
                ignoringRequestMatchers(AnyRequestMatcher.INSTANCE)
                ignoringRequestMatchers("/**")
                // 禁用 CSRF 保护
                disable()
            }

            // 提供注销支持。
            logout {
                // SecurityContextLogoutHandler 是否应该在注销时清除 Authentication。
//                clearAuthentication = true
                // 注销时是否使 HttpSession 无效。
//                invalidateHttpSession = true
                // 触发注销的 URL。
                logoutUrl = "/api/auth/logout"
                // 触发注销发生的 RequestMatcher。
//                logoutRequestMatcher = OrRequestMatcher()
                // 注销后重定向到的 URL。
//                logoutSuccessUrl = null
                // 注销后使用的 LogoutSuccessHandler。如果指定了此参数，则 logoutSuccessUrl 将被忽略。
                this.logoutSuccessHandler = logoutSuccessHandler
//                permitAll = false
                // 添加一个 LogoutHandler。默认情况下，SecurityContextLogoutHandler 会被添加为最后一个 LogoutHandler。
//                addLogoutHandler()
                // 允许指定注销成功时要删除的 cookie 的名称。
                deleteCookies("SESSION", "JSESSIONID", "XSRF-TOKEN", "remember-me", "idea-**")
                // 设置要使用的默认 LogoutSuccessHandler，该默认 LogoutSuccessHandler 优先为提供的 RequestMatcher 调用。
//                defaultLogoutSuccessHandlerFor()
                // 禁用注销
//                disable()
                // 授予每个用户访问 logoutSuccessUrl 和 logoutUrl 的权限。
//                permitAll()
            }

            // 使用 SAML 2.0 服务提供商配置身份验证支持。需要一个 RelyingPartyRegistrationRepository，
            // 并且必须在 ApplicationContext 中注册或通过 Saml2Dsl.relyingPartyRegistrationRepository 配置。
//            saml2Login {
//                // 依赖方的 RelyingPartyRegistrationRepository，每一方代表一个服务提供商、SP 和此主机，以及相互通信的身份提供商、IDP 对。
//                relyingPartyRegistrationRepository = null
//                // 如果需要身份验证则重定向到登录页面（即“/login”）
//                loginPage = null
//                authenticationRequestUriQuery = null
//                // 身份验证成功后使用的 AuthenticationSuccessHandler
//                this.authenticationSuccessHandler = null
//                // 身份验证成功后使用的 AuthenticationFailureHandler
//                this.authenticationFailureHandler = null
//                // 身份验证失败时发送给用户的 URL
//                failureUrl = null
//                // 用于验证凭证的 URL
//                loginProcessingUrl = null
//                // 是否授予每个用户对 failureUrl 以及 HttpSecurityBuilder、loginPage 和 loginProcessingUrl 的访问权限
//                permitAll = null
//                // SAML 2 身份验证期间使用的 AuthenticationManager。
//                authenticationManager = null
//                // 授予每个用户的 failureUrl 以及 HttpSecurityBuilder、loginPage 和 loginProcessingUrl 的 URL 访问权限。
//                permitAll()
//                // 如果用户在身份验证之前没有访问过安全页面或者 alwaysUse 为真，则指定用户身份验证成功后将被重定向到哪里。
//                defaultSuccessUrl("", false)
//            }

            // 为 SAML 2.0 服务提供商配置注销支持。
            // 使用 POST 和 REDIRECT 绑定实现单点注销配置文件，如 SAML V2.0 核心、配置文件和绑定规范中所述。
            // 使用此功能的先决条件是，您需要有一个 SAML v2.0 断言方来发送注销请求。
            // 依赖方和断言方的表示包含在 RelyingPartyRegistration 中。
            // RelyingPartyRegistration 由 RelyingPartyRegistrationRepository 组成，该存储库是必需的，
            // 并且必须在 ApplicationContext 中注册或通过 HttpSecurityDsl.saml2Login 配置。
            // 默认配置在 /logout 处提供自动生成的注销端点，并在注销完成后重定向到 /login?logout。
//            saml2Logout {
//                // 依赖方的 RelyingPartyRegistrationRepository，每一方代表一个服务提供商、SP 和此主机，以及相互通信的身份提供商、IDP 对。
//                relyingPartyRegistrationRepository = null
//                // 注销页面开始 SLO 重定向流程
//                logoutUrl = null
//                // 配置 SAML 2.0 注销请求组件
//                logoutRequest {
//                    // 断言方可以通过此 URL 发送 SAML 2.0 注销请求。
//                    // 断言方应使用 {@link RelyingPartyRegistration#getSingleLogoutServiceBindings()} 中指定的任何 HTTP 方法。
//                    logoutUrl = "/logout/saml2/slo"
//                    // Saml2LogoutRequestValidator 用于验证传入的 {@code LogoutRequest}。
//                    logoutRequestValidator = null
//                    // Saml2LogoutRequestResolver 用于生成传出的 {@code LogoutRequest}。
//                    logoutRequestResolver = null
//                    // Saml2LogoutRequestRepository 用于存储传出的 {@code LogoutRequest}，用于链接到断言方相应的 {@code LogoutResponse}
//                    logoutRequestRepository = HttpSessionLogoutRequestRepository()
//                }
//                // 配置 SAML 2.0 注销响应组件
//                logoutResponse {
//                    // 断言方可以通过此 URL 发送 SAML 2.0 注销响应。
//                    // 断言方应使用 {@link RelyingPartyRegistration#getSingleLogoutServiceBindings()} 中指定的任何 HTTP 方法。
//                    logoutUrl = "/logout/saml2/slo"
//                    // Saml2LogoutResponseValidator 用于验证传入的 {@code LogoutResponse}。
//                    logoutResponseValidator = null
//                    // Saml2LogoutResponseResolver 用于生成传出的 {@code LogoutResponse}。
//                    logoutResponseResolver = null
//                }
//            }

            // 配置 SAML 2.0 依赖方元数据端点。
            // RelyingPartyRegistrationRepository 是必需的，
            // 并且必须向 ApplicationContext 注册或通过 Saml2Dsl.relyingPartyRegistrationRepository 配置。
//            saml2Metadata {
//                // 依赖方元数据端点的名称；默认为 /saml2/metadata 和 /saml2/metadata/{registrationId}
//                metadataUrl = null
//                // Saml2MetadataResponseResolver 用于将元数据请求解析为元数据
//                metadataResponseResolver = null
//            }

            // 允许配置匿名用户的表示方式。
//            anonymous {
//                // 用于识别为匿名身份验证创建的令牌的密钥
//                key = null
//                // 匿名用户的身份验证对象的主体
//                principal = null
//                // 匿名用户的 Authentication.getAuthorities
//                authorities = null
//                // 用于验证匿名用户的 AuthenticationProvider
//                authenticationProvider = null
//                // 用于填充匿名用户的 AnonymousAuthenticationFilter。
//                authenticationFilter = null
//                // 禁用匿名身份验证
//                disable()
//            }

            // 使用 OAuth 2.0 和/或 OpenID Connect 1.0 提供程序配置身份验证支持。需要 ClientRegistrationRepository，
            // 并且必须将其注册为 Bean 或通过 OAuth2LoginDsl.clientRegistrationRepository 配置
//            oauth2Login {
//                // 客户注册存储库。
//                clientRegistrationRepository = null
//                // 授权客户端的存储库。
//                authorizedClientRepository = null
//                // 为授权客户提供服务。
//                authorizedClientService = null
//                // 如果需要身份验证则重定向到登录页面（即“/login”）
//                loginPage = null
//                // 身份验证成功后使用的 AuthenticationSuccessHandler
//                this.authenticationSuccessHandler = null
//                // 身份验证成功后使用的 AuthenticationFailureHandler
//                this.authenticationFailureHandler = null
//                // 身份验证失败时发送给用户的 URL
//                failureUrl = null
//                // 用于验证凭证的 URL
//                loginProcessingUrl = null
//                // 是否授予每个用户对 failureUrl 以及 HttpSecurityBuilder、loginPage 和 loginProcessingUrl 的访问权限
//                permitAll = null
//                authenticationDetailsSource = null
//                oidcSessionRegistry = null
//                // 授予每个用户的 failureUrl 以及 HttpSecurityBuilder、loginPage 和 loginProcessingUrl 的 URL 访问权限。
//                permitAll()
//                // 如果用户在身份验证之前没有访问过安全页面或者 alwaysUse 为真，则指定用户身份验证成功后将被重定向到哪里。
//                defaultSuccessUrl("", false)
//                // 配置授权服务器的授权端点。
//                authorizationEndpoint {
//                    // 用于授权请求的基本 URI。
//                    baseUri = null
//                    // 用于解析 OAuth2AuthorizationRequest 的解析器。
//                    authorizationRequestResolver = null
//                    // 用于存储 OAuth2AuthorizationRequest 的存储库。
//                    authorizationRequestRepository = null
//                    // 授权端点重定向 URI 的重定向策略。
//                    authorizationRedirectStrategy = null
//                }
//                // 配置授权服务器的令牌端点。
//                tokenEndpoint {
//                    // 客户端用于从 Token Endpoint 请求访问令牌凭证。
//                    accessTokenResponseClient = null
//                }
//                // 配置授权服务器的重定向端点。
//                redirectionEndpoint {
//                    // 将处理授权响应的 URI。
//                    baseUri = null
//                }
//                // 配置授权服务器的 UserInfo 端点。
//                userInfoEndpoint {
//                    // 用于从 UserInfo Endpoint 获取最终用户的用户属性的 OAuth 2.0 服务。
//                    userService = null
//                    // OpenID Connect 1.0 服务用于从 UserInfo Endpoint 获取最终用户的用户属性。
//                    oidcUserService = null
//                    // 用于映射 OAuth2User.getAuthorities 的 GrantedAuthoritiesMapper
//                    userAuthoritiesMapper = null
//                }
//            }

            // 配置 OAuth 2.0 客户端支持。
//            oauth2Client {
//                // 客户注册存储库。
//                clientRegistrationRepository = null
//                // 授权客户端的存储库。
//                authorizedClientRepository = null
//                // 为授权客户提供服务。
//                authorizedClientService = null
//                // 配置 OAuth 2.0 授权码授予。
//                authorizationCodeGrant {
//                    // 用于解析 OAuth2AuthorizationRequest 的解析器。
//                    authorizationRequestResolver = null
//                    // 用于存储 OAuth2AuthorizationRequest 的存储库。
//                    authorizationRequestRepository = null
//                    // 授权端点重定向 URI 的重定向策略。
//                    authorizationRedirectStrategy = null
//                    // 客户端用于从 Token Endpoint 请求访问令牌凭证。
//                    accessTokenResponseClient = null
//                }
//            }

            // 配置 OAuth 2.0 资源服务器支持。
//            oauth2ResourceServer {
//                // 用于使用 Bearer Tokens 进行身份验证的请求的 AccessDeniedHandler。
//                this.accessDeniedHandler = null
//                // 用于使用 Bearer Tokens 进行身份验证的请求的 AuthenticationEntryPoint。
//                this.authenticationEntryPoint = null
//                // BearerTokenResolver 用于通过 Bearer Tokens 进行身份验证的请求。
//                bearerTokenResolver = null
//                authenticationManagerResolver = null
//                // 启用 JWT 编码的承载令牌支持。
//                jwt {
//                    // AuthenticationManager 用于确定所提供的身份验证是否可以进行身份​​验证。
//                    authenticationManager = null
//                    // 用于将 Jwt 转换为 AbstractAuthenticationToken 的转换器。
//                    jwtAuthenticationConverter = null
//                    // 要使用的 JwtDecoder。
//                    jwtDecoder = null
//                    // 使用 JSON Web Key (JWK) URL 配置 JwtDecoder
//                    jwkSetUri = null
//                }
//                // 启用不透明令牌支持。
//                opaqueToken {
//                    // AuthenticationManager 用于确定所提供的身份验证是否可以进行身份​​验证。
//                    authenticationManager = null
//                    // Introspection 端点的 URI。
//                    introspectionUri = null
//                    // 要使用的 OpaqueTokenIntrospector。
//                    introspector = null
//                    authenticationConverter = null
//                    // 配置 Introspection 端点的凭据。
//                    introspectionClientCredentials("clientId", "clientSecret")
//                }
//            }

            // 配置 OIDC 1.0 注销支持。
//            oidcLogout {
//                clientRegistrationRepository = null
//                oidcSessionRegistry = null
//                backChannel {
//                    logoutHandler = null
//                    logoutUri = null
//                }
//            }

            // 配置一次性令牌登录支持。todo
//            oneTimeTokenLogin {
//                // 配置用于生成和使用的 OneTimeTokenService
//                tokenService = null
//                // 将传入请求转换为身份验证时使用此 AuthenticationConverter
//                authenticationConverter = null
//                // 身份验证时使用的 AuthenticationFailureHandler
//                this.authenticationFailureHandler = null
//                // 要使用的 AuthenticationSuccessHandler
//                this.authenticationSuccessHandler = null
//                // 要使用的 GenerateOneTimeTokenRequestResolver
//                generateRequestResolver = null
//                // 设置将生成的默认提交页面的 URL
//                defaultSubmitPageUrl = null
//                // 处理登录请求的 URL
//                loginProcessingUrl = null
//                // 一次性令牌生成请求将被处理的 URL
//                tokenGeneratingUrl = null
//                // 配置是否显示默认的一次性令牌提交页面
//                showDefaultSubmitPage = null
//                // 用于处理生成的一次性令牌的策略
//                oneTimeTokenGenerationSuccessHandler = null
//                // 验证用户身份时使用的 AuthenticationProvider
//                authenticationProvider = null
//            }

            // 配置“记住我”身份验证。
//            rememberMe {
//                // 身份验证成功后使用的 AuthenticationSuccessHandler
//                this.authenticationSuccessHandler = null
//                // 用于识别令牌的 key
//                key = null
//                // 要使用的 RememberMeServices
//                rememberMeServices = null
//                // 用于在登录时记住用户的 HTTP 参数。默认为“remember-me”
//                rememberMeParameter = null
//                // 用于存储记住我身份验证令牌的 cookie 名称。默认为“remember-me”
//                rememberMeCookieName = null
//                // 记住我 cookie 可见的域名
//                rememberMeCookieDomain = null
//                // 要使用的 PersistentTokenRepository。
//                // 默认为 org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices
//                tokenRepository = null
//                // 当记住我令牌有效时，用于查找 UserDetails 的 UserDetailsS​​ervice
//                userDetailsService = null
//                // 令牌的有效期（以秒为单位）。默认为 2 周
//                tokenValiditySeconds = null
//                // 该 cookie 是否应被标记为安全
//                useSecureCookie = null
//                // 即使未设置 remember-me 参数，是否也应始终创建 cookie。默认为 false
//                alwaysRemember = null
//            }

            // 启用 WebAuthn 配置。
//            webAuthn {
//                // 依赖方名称
//                rpName = null
//                // 依赖方 ID
//                rpId = null
//                // 允许的来源
//                allowedOrigins = null
//                // 禁用默认 webauthn 注册页面
//                disableDefaultRegistrationPage = null
//                creationOptionsRepository = null
//                messageConverter = null
//            }

            // 在指定 Filter 类的位置添加 Filter。此变体利用了 Kotlin 具体化的类型参数。
            addFilterAt<UsernamePasswordAuthenticationFilter>(customCaptchaOncePerRequestFilter)

            // 在指定 Filter 类的位置后添加 Filter。此变体利用了 Kotlin 具体化的类型参数。
//            addFilterAfter<UsernamePasswordAuthenticationFilter>(customCaptchaOncePerRequestFilter)

            // 在指定 Filter 类的位置之前添加 Filter。此变体利用了 Kotlin 具体化的类型参数。
//            addFilterBefore<UsernamePasswordAuthenticationFilter>(customCaptchaOncePerRequestFilter)

            // TODO: 2023/4/15 研究这里的作用
            // 启用安全上下文配置。
//            securityContext {
//                // SecurityContextRepository 用于在请求之间持久化 org.springframework.security.core.context.SecurityContext
//                securityContextRepository = null
//                requireExplicitSave = null
//            }
        }

        return http.build()
    }
}

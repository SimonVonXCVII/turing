package com.simonvonxcvii.turing.resource.server.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod.*
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * @author Simon Von
 * @since 12/23/25 7:35 PM
 */
@Configuration(proxyBeanMethods = false)
class CorsConfig {

    @Bean
    fun corsConfigurationSource(@Value($$"${app.base-uri}") appBaseUri: String): CorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowedOrigins = listOf(appBaseUri)
        config.allowedMethods =
            listOf(GET.name(), HEAD.name(), POST.name(), PUT.name(), PATCH.name(), DELETE.name(), OPTIONS.name())
        config.addAllowedHeader("X-XSRF-TOKEN")
        config.addAllowedHeader(HttpHeaders.CONTENT_TYPE)
        config.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }

}
package com.simonvonxcvii.turing.properties

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 自定义项目安全参数配置类
 *
 * @author Simon Von
 * @since 3/4/2023 12:57 AM
 */
@ConfigurationProperties(prefix = "custom.security")
data class CustomSecurityProperties(
    /**
     * 服务器网络地址
     */
    var host: String,

    /**
     * jwt 有效期时长，单位秒
     */
    var expires: Int,

    /**
     * 放行白名单配置，网关不校验此处的白名单
     */
    var whitelist: List<String>
)

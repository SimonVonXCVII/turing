package com.simonvonxcvii.turing.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * 项目安全参数配置类
 *
 * @author SimonVonXCVII
 * @since 3/4/2023 12:57 AM
 */
@Configuration
@ConfigurationProperties(prefix = "security")
class SecurityProperties {
    /**
     * 服务器网络地址
     */
    lateinit var address: String

    /**
     * jwt 有效期时长，单位秒
     */
    var expires: Long = 604800

    /**
     * 放行白名单配置，网关不校验此处的白名单
     */
    lateinit var whitelist: Array<String>
}

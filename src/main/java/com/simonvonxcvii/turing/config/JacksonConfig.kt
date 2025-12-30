package com.simonvonxcvii.turing.config

import org.springframework.boot.jackson.autoconfigure.JacksonProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer
import tools.jackson.databind.ext.javatime.deser.LocalDateTimeDeserializer
import tools.jackson.databind.ext.javatime.deser.LocalTimeDeserializer
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer
import tools.jackson.databind.ext.javatime.ser.LocalDateTimeSerializer
import tools.jackson.databind.ext.javatime.ser.LocalTimeSerializer
import tools.jackson.databind.module.SimpleModule
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * 全局日期时间格式的序列化与反序列化配置类 (LocalDateTime、LocalDate 和 LocalTime 类型)
 *
 * @author Simon Von
 * @since 2022/9/26 19:58 周一
 */
@Configuration(proxyBeanMethods = false)
class JacksonConfig {
    /**
     * 为给定类型配置自定义序列化器
     *
     * @author Simon Von
     * @since 2022/9/26 20:11
     */
    @Bean
    fun customSimpleModule(jacksonProperties: JacksonProperties): SimpleModule {
        // 格式
        val dateTimeFormatter = DateTimeFormatter.ofPattern(jacksonProperties.dateFormat ?: "yyyy-MM-dd HH:mm:ss")
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

        return SimpleModule().apply {
            // 序列化
            addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(dateTimeFormatter))
            addSerializer(LocalDate::class.java, LocalDateSerializer(dateFormatter))
            addSerializer(LocalTime::class.java, LocalTimeSerializer(timeFormatter))

            // 反序列化
            addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(dateTimeFormatter))
            addDeserializer(LocalDate::class.java, LocalDateDeserializer(dateFormatter))
            addDeserializer(LocalTime::class.java, LocalTimeDeserializer(timeFormatter))
        }
    }
}
package com.simonvonxcvii.turing.config

import org.springframework.boot.jackson.autoconfigure.JacksonProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.json.JsonMapper
import java.time.format.DateTimeFormatter

/**
 * 全局日期时间格式的序列化与反序列化配置类 (LocalDateTime、LocalDate 和 LocalTime 类型)
 * todo 适配 Spring Boot 4-M3 / Jackson 3.x
 *
 * @author Simon Von
 * @since 2022/9/26 19:58 周一
 */
@Configuration
class LocalDateTimeSerializerConfig {
    /**
     * 为给定类型配置自定义序列化器
     *
     * @author Simon Von
     * @since 2022/9/26 20:11
     */
    @Bean
    fun jackson2ObjectMapperBuilder(jacksonProperties: JacksonProperties): JsonMapper {
        val builder = JsonMapper.builder()

        // 格式
        val dateTimeFormatter = DateTimeFormatter.ofPattern(jacksonProperties.dateFormat ?: "yyyy-MM-dd HH:mm:ss")
        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

        // 序列化
//        builder.serializerByType(LocalDateTime::class.java, LocalDateTimeSerializer(dateTimeFormatter))
//        builder.serializerByType(LocalDate::class.java, LocalDateSerializer(dateFormatter))
//        builder.serializerByType(LocalTime::class.java, LocalTimeSerializer(timeFormatter))
//
//        // 反序列化
//        builder.deserializerByType(LocalDateTime::class.java, LocalDateTimeDeserializer(dateTimeFormatter))
//        builder.deserializerByType(LocalDate::class.java, LocalDateDeserializer(dateFormatter))
//        builder.deserializerByType(LocalTime::class.java, LocalTimeDeserializer(timeFormatter))

        return builder.build()
    }
}
package com.simonvonxcvii.turing.config

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.boot.autoconfigure.jackson.JacksonProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
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
@Configuration
class LocalDateTimeSerializerConfig {
    /**
     * 为给定类型配置自定义序列化器
     *
     * @author Simon Von
     * @since 2022/9/26 20:11
     */
    @Bean
    fun customize(jacksonProperties: JacksonProperties): Jackson2ObjectMapperBuilderCustomizer {
        return Jackson2ObjectMapperBuilderCustomizer { builder: Jackson2ObjectMapperBuilder ->
            // 格式
            val dateTimeFormatter = DateTimeFormatter.ofPattern(jacksonProperties.dateFormat)
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

            // 序列化
            builder.serializerByType(LocalDateTime::class.java, LocalDateTimeSerializer(dateTimeFormatter))
            builder.serializerByType(LocalDate::class.java, LocalDateSerializer(dateFormatter))
            builder.serializerByType(LocalTime::class.java, LocalTimeSerializer(timeFormatter))

            // 反序列化
            builder.deserializerByType(LocalDateTime::class.java, LocalDateTimeDeserializer(dateTimeFormatter))
            builder.deserializerByType(LocalDate::class.java, LocalDateDeserializer(dateFormatter))
            builder.deserializerByType(LocalTime::class.java, LocalTimeDeserializer(timeFormatter))
        }
    }
}

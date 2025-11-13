package com.simonvonxcvii.turing.config

import org.springframework.boot.jackson.autoconfigure.JacksonProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.json.JsonMapper
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * 全局日期时间格式的序列化与反序列化配置类 (LocalDateTime、LocalDate 和 LocalTime 类型)
 * todo 适配 Spring Boot 4 / Jackson 3.x
 *
 * @author Simon Von
 * @since 2022/9/26 19:58 周一
 */
@Configuration
class JacksonConfig {
    /**
     * 为给定类型配置自定义序列化器
     *
     * @author Simon Von
     * @since 2022/9/26 20:11
     */
    @Bean
    fun jackson2ObjectMapperBuilder(builder: JsonMapper.Builder, jacksonProperties: JacksonProperties): JsonMapper {
//        // 格式
//        val dateTimeFormatter = DateTimeFormatter.ofPattern(jacksonProperties.dateFormat ?: "yyyy-MM-dd HH:mm:ss")
//        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
//
//        // 序列化
//        builder.serializerByType(LocalDateTime::class.java, LocalDateTimeSerializer(dateTimeFormatter))
//        builder.serializerByType(LocalDate::class.java, LocalDateSerializer(dateFormatter))
//        builder.serializerByType(LocalTime::class.java, LocalTimeSerializer(timeFormatter))
//
//        // 反序列化
//        builder.deserializerByType(LocalDateTime::class.java, LocalDateTimeDeserializer(dateTimeFormatter))
//        builder.deserializerByType(LocalDate::class.java, LocalDateDeserializer(dateFormatter))
//        builder.deserializerByType(LocalTime::class.java, LocalTimeDeserializer(timeFormatter))

//        val ctxt: SerializationContext = SerializationContextExt()
//        val origType: JavaType = SimpleType.constructUnsafe(LocalDateTime::class.java)
//        val mapperConfig: MapperConfig<> = SerializationConfig()
//        val beanDescRef: BeanDescription.Supplier = BeanDescription.SupplierBase()
//        val supplier: BeanDescription.Supplier = BeanDescription.LazySupplier()
//        val supplier: BeanDescription.Supplier = BeanDescription.EagerSupplier()
//        val formatOverrides: JsonFormat.Value = JsonFormat.Value.forPattern(jacksonProperties.dateFormat)
//        val localDataTimeSerializer = BeanSerializerFactory.instance
//        localDataTimeSerializer.createSerializer(ctxt, origType, beanDescRef, formatOverrides)
//        builder.serializerFactory(localDataTimeSerializer)
//
//        builder.serializationContexts(SerializationContexts.DefaultImpl())
//        builder.configureForJackson2()

        val dateFormat = SimpleDateFormat()
        dateFormat.applyPattern(jacksonProperties.dateFormat ?: "yyyy-MM-dd HH:mm:ss")
//        val dateFormat2 = StdDateFormat()
        builder.defaultDateFormat(dateFormat)
        builder.registerSubtypes(LocalDateTime::class.java, LocalDate::class.java, LocalTime::class.java)

        return builder.build()


        // 格式
//        val dateTimeFormatter = DateTimeFormatter.ofPattern(jacksonProperties.dateFormat ?: "yyyy-MM-dd HH:mm:ss")
//        val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
//
//        // 序列化与反序列化配置
//        val javaTimeModule = JavaTimeModule().apply {
//            addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(dateTimeFormatter))
//            addSerializer(LocalDate::class.java, LocalDateSerializer(dateFormatter))
//            addSerializer(LocalTime::class.java, LocalTimeSerializer(timeFormatter))
//            addDeserializer(LocalDateTime::class.java, LocalDateTimeDeserializer(dateTimeFormatter))
//            addDeserializer(LocalDate::class.java, LocalDateDeserializer(dateFormatter))
//            addDeserializer(LocalTime::class.java, LocalTimeDeserializer(timeFormatter))
//        }
//
//        return com.fasterxml.jackson.databind.json.JsonMapper.builder()
//            .addModule(javaTimeModule)
//            .build()
    }
}
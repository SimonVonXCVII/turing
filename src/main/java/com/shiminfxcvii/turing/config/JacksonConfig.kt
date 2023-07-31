package com.shiminfxcvii.turing.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

/**
 * 全局处理返回数据中 Long 转 String 配置类。由于已将项目中实体类的主键改为了 String 类型，所有改配置类已没有用处
 *
 * @author ShiminFXCVII
 * @since 1/3/2023 2:12 PM
 */
@Configuration
class JacksonConfig {
    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper::class)
    fun objectMapper(builder: Jackson2ObjectMapperBuilder): ObjectMapper {
        val objectMapper = builder.createXmlMapper(false).build<ObjectMapper>()
        // 全局配置序列化返回 JSON 处理
        val simpleModule = SimpleModule()
        // JSON Long ==> String
        simpleModule.addSerializer(Long::class.java, ToStringSerializer.instance)
        objectMapper.registerModule(simpleModule)
        return objectMapper
    }
}
//package com.simonvonxcvii.turing.component
//
//import com.fasterxml.jackson.annotation.JsonAutoDetect
//import com.fasterxml.jackson.annotation.JsonTypeInfo
//import com.fasterxml.jackson.annotation.PropertyAccessor
//import com.fasterxml.jackson.databind.DeserializationFeature
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.data.redis.connection.RedisConnectionFactory
//import org.springframework.data.redis.core.RedisTemplate
//import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
//import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
//import org.springframework.data.redis.serializer.StringRedisSerializer
//
///**
// * redis 配置类，支持序列化与反序列化对象
// * 如果使用源码中默认的，不配置也可以，但是保存到 Redis 的数据有乱码
// *
// * @author Simon Von
// * @since 6/17/2023 5:20 PM
// */
//@Configuration
//class RedisConfig {
//    @Bean
//    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
//        val redisTemplate = RedisTemplate<String, Any>()
//        redisTemplate.connectionFactory = redisConnectionFactory
//
//        val objectMapper = ObjectMapper()
//        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
//
//        // 关键修复：明确指定类型信息包装方式为 PROPERTY 而非默认的 WRAPPER_ARRAY
//        objectMapper.activateDefaultTyping(
//            objectMapper.polymorphicTypeValidator,
//            ObjectMapper.DefaultTyping.NON_FINAL,
//            JsonTypeInfo.As.PROPERTY  // 这是关键修复
//        )
//
//        // 注册模块
//        objectMapper.registerModule(JavaTimeModule())
////        objectMapper.registerModule(KotlinModule.Builder().build()) // Kotlin支持
//
//        // 配置反序列化行为
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
//        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
//        objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false)
//
//        // 使用 Jackson2JsonRedisSerialize 替换默认序列化
//        val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(objectMapper, Any::class.java)
////        val genericJackson2JsonRedisSerializer = GenericJackson2JsonRedisSerializer()
////            .configure{ objectMapper: ObjectMapper ->
////                // 在这里配置 ObjectMapper
////                objectMapper.enable(SerializationFeature.INDENT_OUTPUT)
////                objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
////                // 其他配置...
////            }
//        val genericJackson2JsonRedisSerializer = GenericJackson2JsonRedisSerializer.builder()
//            .objectMapper(objectMapper)
//            .build()
//
//        // 设置 value 的序列化规则和 key 的序列化规则
//        val stringRedisSerializer = StringRedisSerializer()
//        redisTemplate.keySerializer = stringRedisSerializer
//        redisTemplate.valueSerializer = genericJackson2JsonRedisSerializer
//        redisTemplate.hashKeySerializer = stringRedisSerializer
//        redisTemplate.hashValueSerializer = genericJackson2JsonRedisSerializer
//        redisTemplate.afterPropertiesSet()
//        return redisTemplate
//    }
//}
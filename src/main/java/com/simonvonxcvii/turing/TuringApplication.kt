package com.simonvonxcvii.turing

import com.simonvonxcvii.turing.properties.SecurityProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.data.web.config.EnableSpringDataWebSupport

// TODO 将启动日志发给 AI 看哪些地方可以改进，当然，也不只启动日志
//  查看各 Kotlin 文件生成的对应的字节码文件，看哪些地方的写法可以改进
//  考虑解构项目
//  考虑写一个脚本还是什么，自动观测 Kotlin、Gradle、Nginx 等最新版本，and upgrade the local version to the latest version
//  可以尝试换成 latest，或者取消 version
@EnableConfigurationProperties(SecurityProperties::class)
// 清晰化 jpa 包路径
@EnableJpaRepositories("com.simonvonxcvii.turing.repository.jpa")
// 默认情况下不支持按原样序列化 PageImpl 实例，这意味着无法保证生成的 JSON 结构的稳定性
// 为了获得稳定的 JSON 结构，使用该方式来全局解决问题，让 Spring Data 自动处理分页对象的序列化，提供稳定的 JSON 结构
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@SpringBootApplication
class TuringApplication

fun main(args: Array<String>) {
    runApplication<TuringApplication>(*args)
}

package com.simonvonxcvii.turing

import com.simonvonxcvii.turing.properties.SecurityProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

// TODO 将启动日志发给 AI 看哪些地方可以改进，当然，也不只启动日志
@EnableConfigurationProperties(SecurityProperties::class)
@SpringBootApplication
class TuringApplication

fun main(args: Array<String>) {
    runApplication<TuringApplication>(*args)
}

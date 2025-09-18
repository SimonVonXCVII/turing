package com.simonvonxcvii.turing

import com.simonvonxcvii.turing.properties.SecurityProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@EnableConfigurationProperties(SecurityProperties::class)
@SpringBootApplication
class TuringApplication

fun main(args: Array<String>) {
    runApplication<TuringApplication>(*args)
}

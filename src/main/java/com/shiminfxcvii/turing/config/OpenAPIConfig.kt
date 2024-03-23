package com.shiminfxcvii.turing.config

import io.swagger.v3.oas.annotations.ExternalDocumentation
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.extensions.Extension
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.info.License
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.servers.Server
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders

/**
 * Open API 配置类
 *
 * @author ShiminFXCVII
 * @since 12/16/2022 17:47 PM
 */
// @OpenAPIDefinition 配置的内容优先级高于 new OpenAPI()
@OpenAPIDefinition(
    // 提供有关 API 的元数据。 元数据可以根据需要由工具使用。
    // 返回值：有关此 API 的元数据
    info = Info(
        title = "\${spring.application.name}",
        description = "OpenApi3.0.1",
        license = License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.html"),
        version = "0.0.1-SNAPSHOT"
    ),
    // 规范使用的带有附加元数据的标签列表。 标签的顺序可以用来反映解析工具的顺序。
    // 返回值：规范使用的标签以及任何其他元数据
    // 这个 tags 可以用来定义一些公共参数说明，比如 token 或者其他自定义 key
    tags = [Tag(
        name = "aaa-admin",
        description = "eyJ4NXQjUzI1NiI6InpkZGlWTVhuU3VOSHF6eGNEYlRxSWx3VWd5T2puMzBCSkRpWlhydU56aW8iLCJraWQiOiJzaGl0aW5nLXNvaWwta2V5c3RvcmUiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxNDgwNTM4MDg1Nzc4NzYzNzc3IiwiYXVkIjpbImh0dHA6Ly9sb2NhbGhvc3Q6NTUyMiIsImh0dHA6Ly8xMjcuMC4wLjE6NTUyMiJdLCJuYmYiOjE2Nzg2MzA4MDcsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6OTY5OSIsImV4cCI6MTY4NjQwNjgwNywiaWF0IjoxNjc4Njc4NjUzLCJqdGkiOiIxYWQ2ZTIxOS04YzMwLTQ1NjAtODI2NS01Mzg2OWI5ZDlmOTYiLCJ1c2VybmFtZSI6ImFkbWluIn0.LesmvflReIVMrCDgnputDiKhW8a_wZ_t0WTejVhHHz4_fz_TJaqaxRox3noiSosXeEcR306Vc0vertpf1KJjaVFj-8TBAZwGr_2UtNX6FiI3YK89y6Qz0-e4fhC-3i0P2QsKo3gci-zI9jwI8KzKcvEuME3IW72Ln6W-L8jwbOejlZ6yiIizIAwbAQ-rK5fPahmzeY_tYtj-L-GU5CsAHsXnO0cL-7Qacxkvs_uXPN8gA-LAZ0TC7lq6uZQls8dg4pu-eNuJAOL6Z9g7s4dtHHYi4c8nEqrYa7Tjsn73lwihFM57QBAu5SufUFUE6iFi1kHgJEI-RHAALF6G3sE-T0Bb4bG7YGWhcONL6Tdl6RHdHp9qhyluhrNxYVH42BMVsGG_AKVl_LNvoZhR7ZYCCVkzC21qVMyMg3Awc8vFI5cICC6cQV3C_ybqCPpTcztsbWCK3ZEYtMpNU7mX76tkIdgqWwoXnhHyWBO8dUgCT9GtwKFYTxoK4QEKiTo5Lz3V"
    )],
    // 一组服务器对象，它提供到目标服务器的连接信息。 如果未提供 servers 属性，或者是一个空数组，则默认值将是一个 url 值为 / 的服务器对象。
    // 返回值：这个 API 的服务器
    // 请求服务地址配置，可以按不同的环境配置
    servers = [Server(
        url = "http://localhost:8080",
        description = "开发环境地址"
    ), Server(url = "http://localhost:8080", description = "测试环境地址"), Server(
        url = "http://localhost:8080",
        description = "生产环境地址"
    )],
    // 可以在 API 中使用哪些安全机制的声明。
    // 返回值：用于此 API 的服务器数组
    security = [
        // 为什么没有 scopes ？
        // 因为如果安全方案的类型为“oauth2”或“openIdConnect”，则该值是执行所需的范围名称列表。对于其他安全方案类型，数组必须为空。
        SecurityRequirement(
            // 此名称必须对应于已声明的 SecurityRequirement。
            name = HttpHeaders.AUTHORIZATION
        )
    ],
    // API 的任何其他外部文档
    // 返回值：此 API 的外部文档。
    externalDocs = ExternalDocumentation(
        description = "开发环境",
        // 目标文档的 URL。 值必须采用 URL 格式。
        url = "/v3/api-docs",
        extensions = [Extension(
            name = HttpHeaders.AUTHORIZATION,
            properties = [ExtensionProperty(
                name = HttpHeaders.AUTHORIZATION,
                value = "eyJhbGciOiJIUzI1NiJ9.eyJwcmluY2lwYWwiOiIxNDgwNTM4MDg1Nzc4NzYzNzc3IiwiaXNzIjoiaHR0cHM6XC9cL3d3dy5jc2JhaWMuY29tXC8iLCJzdWIiOiJjc2JhaWMiLCJwcmluY2lwYWxfdHlwZSI6IiJ9.7QWvFbWBvl2LG8JStLCsCuT8spP5oFr75YeCN0Igabk"
            )]
        )]
    ),
    // 可选扩展列表
    // 返回值：可选的扩展数组
    extensions = []
)
// 注释可以在类级别（也可以在多个类上）使用，以将 securitySchemes 添加到规范组件部分。
// 安全配置：JWT Token。也可以配置其他类型的鉴权，比如：basic
@SecurityScheme(
    type = SecuritySchemeType.HTTP,
    name = HttpHeaders.AUTHORIZATION,
    description = "请输入 JWT token，不需要 Bearer 前缀",
    paramName = HttpHeaders.AUTHORIZATION,
    `in` = SecuritySchemeIn.HEADER,
    scheme = "Bearer",
    bearerFormat = "JWT",
    extensions = [Extension(
        properties = [ExtensionProperty(
            name = HttpHeaders.AUTHORIZATION,
            value = "eyJhbGciOiJIUzI1NiJ9.eyJwcmluY2lwYWwiOiIxNDgwNTM4MDg1Nzc4NzYzNzc3IiwiaXNzIjoiaHR0cHM6XC9cL3d3dy5jc2JhaWMuY29tXC8iLCJzdWIiOiJjc2JhaWMiLCJwcmluY2lwYWxfdHlwZSI6IiJ9.7QWvFbWBvl2LG8JStLCsCuT8spP5oFr75YeCN0Igabk"
        )]
    )]
)
@Configuration
class OpenAPIConfig {
//    @Bean
//    fun openAPI(): OpenAPI {
//        return OpenAPI()
//            .specVersion(SpecVersion.V31)// 55
//            .openapi("3.0.1")// 80
//            .paths(null)// 218
//            .path(null, null)// 246
//            .schema(null, null)// 255
//            .schemaRequirement(null, null)// 263
//            .webhooks(null)// 289
//            .jsonSchemaDialect(null)// 320
//    }
}

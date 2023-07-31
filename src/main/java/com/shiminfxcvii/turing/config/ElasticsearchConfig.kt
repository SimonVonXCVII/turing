package com.shiminfxcvii.turing.config

import lombok.SneakyThrows
import org.apache.http.conn.ssl.NoopHostnameVerifier
import org.apache.http.conn.ssl.TrustAllStrategy
import org.apache.http.ssl.SSLContexts
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration

/**
 * 用于使用 Elasticsearch 客户端设置 Elasticsearch 连接
 *
 * @author ShiminFXCVII
 * @since 2023/6/30 23:33
 */
@Configuration
class ElasticsearchConfig(private val elasticsearchProperties: ElasticsearchProperties) : ElasticsearchConfiguration() {
    /**
     * 重写以提供客户端配置
     *
     * @return configuration, must not be null
     */
    @SneakyThrows
    override fun clientConfiguration(): ClientConfiguration {
        return ClientConfiguration.builder()
            .connectedTo(*elasticsearchProperties.uris.toTypedArray())
            // 只需要设置 TrustAllStrategy.INSTANCE 和 NoopHostnameVerifier.INSTANCE，不需要加载证书文件
            .usingSsl(
                SSLContexts.custom().loadTrustMaterial(TrustAllStrategy.INSTANCE).build(),
                NoopHostnameVerifier.INSTANCE
            )
            .withBasicAuth(elasticsearchProperties.username, elasticsearchProperties.password)
            .build()
    }
}
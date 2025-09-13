package com.simonvonxcvii.turing.config

import com.simonvonxcvii.turing.utils.UserUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.*

/**
 * JPA 配置类
 *
 * @author Simon Von
 * @since 2023/8/18 14:48
 */
@Configuration
@EnableJpaAuditing
class JpaConfig {
    /**
     * 定义审核员信息
     *
     * @return 识别应用程序的当前审核员的组件的接口实现。这主要是某种用户。
     * @author Simon Von
     * @since 2023/8/18 14:49
     */
    @Bean
    fun auditorProvider(): AuditorAware<Int> {
        return AuditorAware { Optional.ofNullable(UserUtils.getId()) }
    }
}

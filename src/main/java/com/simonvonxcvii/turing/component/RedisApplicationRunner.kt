package com.simonvonxcvii.turing.component

import com.simonvonxcvii.turing.entity.Dict
import com.simonvonxcvii.turing.repository.DictRepository
import jakarta.persistence.criteria.Path
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.stream.Collectors

/**
 * Redis 数据初始化
 * 用于指示 bean 在包含在 SpringApplication 中时应运行的接口。
 * 可以在同一个应用程序上下文中定义多个 ApplicationRunner bean，并且可以使用 Ordered 接口或 @Order 注解进行排序。
 *
 * @author Simon Von
 * @since 2023/7/16 14:00
 */
@Component
class RedisApplicationRunner(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val dictRepository: DictRepository
) : ApplicationRunner {
    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     */
    override fun run(args: ApplicationArguments) {
        val dictList = dictRepository.findAll { root, query, criteriaBuilder ->
            val type: Path<String> = root.get("type")
            query?.where(criteriaBuilder.equal(type, "area"))?.restriction
        }
        if (dictList.isEmpty()) {
            return
        }
        redisTemplate.opsForValue().multiSet(
            dictList.stream().collect(
                Collectors.toMap(
                    { dict -> Dict.REDIS_KEY_PREFIX + dict.value }, Dict::name
                )
            )
        )
    }
}

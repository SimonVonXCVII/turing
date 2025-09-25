package com.simonvonxcvii.turing.component

import com.simonvonxcvii.turing.entity.Dict
import com.simonvonxcvii.turing.repository.jpa.DictJpaRepository
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.data.redis.core.StringRedisTemplate
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
    private val stringRedisTemplate: StringRedisTemplate,
    private val dictJpaRepository: DictJpaRepository
) : ApplicationRunner {
    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     */
    override fun run(args: ApplicationArguments) {
        val dictList = dictJpaRepository.findAll { root, query, builder ->
            val type = builder.equal(root.get<String>(Dict.TYPE), "area")
            query?.where(type)?.restriction
        }.filterNotNull()
        // TODO 考虑如果 Redis 中已经有上面这些数据了就不要执行下面的代码了，虽然数据只会覆盖掉，不会发生变化
        val toMap = Collectors.toMap({ dict -> Dict.REDIS_KEY_PREFIX + dict.value }, Dict::name)
        val collect = dictList.stream().collect(toMap)
        stringRedisTemplate.opsForValue().multiSetIfAbsent(collect)
    }
}

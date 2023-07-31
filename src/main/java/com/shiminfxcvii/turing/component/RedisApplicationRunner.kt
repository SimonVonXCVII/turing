package com.shiminfxcvii.turing.component

import com.baomidou.mybatisplus.extension.kotlin.KtQueryWrapper
import com.shiminfxcvii.turing.entity.Dict
import com.shiminfxcvii.turing.mapper.DictMapper
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
 * @author ShiminFXCVII
 * @since 2023/7/16 14:00
 */
@Component
class RedisApplicationRunner(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val dictMapper: DictMapper
) : ApplicationRunner {
    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Throws(Exception::class)
    override fun run(args: ApplicationArguments) {
        val dictList = dictMapper.selectList(
            KtQueryWrapper(Dict()).eq(Dict::type, "area")
        )
        redisTemplate.opsForValue().multiSet(
            dictList.stream().collect(Collectors.toMap(
                { dict: Dict -> Dict.REDIS_KEY_PREFIX + dict.value }, { dict: Dict -> dict.name })
            )
        )
    }
}
package com.shiminfxcvii.turing.config

import com.baomidou.mybatisplus.annotation.DbType
import com.baomidou.mybatisplus.core.incrementer.IKeyGenerator
import com.baomidou.mybatisplus.extension.incrementer.KingbaseKeyGenerator
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.DataChangeRecorderInnerInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor
import org.mybatis.spring.annotation.MapperScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Mybatis Plus 配置类
 */
@Configuration
@MapperScan("com.shiminfxcvii.turing.mapper")
class MybatisPlusConfig {
    /**
     * 拦截器配置
     * 注意（摘自 baomidou.com 官网）:
     * 使用多个功能需要注意顺序关系，建议使用如下顺序
     * 多租户，动态表名
     * 分页，乐观锁
     * sql 性能规范,防止全表更新与删除
     * 总结: 对 sql 进行单次改造的优先放入，不对 sql 进行改造的最后放入
     */
    @Bean
    fun mybatisPlusInterceptor(): MybatisPlusInterceptor {
        val interceptor = MybatisPlusInterceptor()
        // 多租户
//        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor());
        // 动态表名
//        interceptor.addInnerInterceptor(new DynamicTableNameInnerInterceptor());
        // 分页拦截器
        interceptor.addInnerInterceptor(PaginationInnerInterceptor(DbType.KINGBASE_ES))
        // 乐观锁插件
        interceptor.addInnerInterceptor(OptimisticLockerInnerInterceptor(true))
        // 非法 SQL 拦截器
//        interceptor.addInnerInterceptor(new IllegalSQLInnerInterceptor());
        // 攻击 SQL 阻断解析器，防止全表更新与删除
        interceptor.addInnerInterceptor(BlockAttackInnerInterceptor())
        // 数据权限处理器
//        interceptor.addInnerInterceptor(new DataPermissionInterceptor());
        // 功能类似于 GlobalConfig.DbConfig.isReplacePlaceholder(), 只是这个是在运行时实时替换,适用范围更广
//        interceptor.addInnerInterceptor(new ReplacePlaceholderInnerInterceptor());
        // 数据变动记录插件，将会在增删改操作之后在控制台打印一条 log
        interceptor.addInnerInterceptor(DataChangeRecorderInnerInterceptor())
        return interceptor
    }

    /**
     * 表主键生成器（测试中...）
     *
     * @return 表主键生成器实现类
     * @author ShiminFXCVII
     * @since 11/26/2022 10:12 PM
     */
    @Bean
    fun iKeyGenerator(): IKeyGenerator {
        return KingbaseKeyGenerator()
    }
}
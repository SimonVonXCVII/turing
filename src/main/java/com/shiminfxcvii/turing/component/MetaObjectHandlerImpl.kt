package com.shiminfxcvii.turing.component

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler
import com.shiminfxcvii.turing.utils.UserUtils
import org.apache.ibatis.reflection.MetaObject
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * mybatis plus 自动填充类
 */
@Component
class MetaObjectHandlerImpl : MetaObjectHandler {
    /**
     * 自动添加
     *
     * @param metaObject 元对象
     */
    override fun insertFill(metaObject: MetaObject) {
        val userId = UserUtils.getUserId()
        val now = LocalDateTime.now()
        metaObject.setValue("createBy", userId)
        metaObject.setValue("createTime", now)
        metaObject.setValue("updateBy", userId)
        metaObject.setValue("updateTime", now)
        metaObject.setValue("deleted", false)
        metaObject.setValue("version", 0)
    }

    /**
     * 自动更新
     *
     * @param metaObject 元对象
     */
    override fun updateFill(metaObject: MetaObject) {
        metaObject.setValue("updateBy", UserUtils.getUserId())
        metaObject.setValue("updateTime", LocalDateTime.now())
    }
}
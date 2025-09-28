package com.simonvonxcvii.turing.listener

import com.simonvonxcvii.turing.entity.AbstractAuditable
import jakarta.persistence.*
import org.apache.commons.logging.LogFactory

/**
 * 自定义审计实体监听器
 *
 * @author Simon Von
 * @see org.springframework.data.jpa.domain.support.AuditingEntityListener
 *
 * @since 2023/8/18 14:55
 */
class CustomAuditingEntityListener {
    /**
     * select 后置回调
     */
    @PostLoad
    fun postLoad(entity: AbstractAuditable) {
//        log.info("查询后：$entity")
    }

    /**
     * insert 前置回调
     */
    @PrePersist
    fun prePersist(entity: AbstractAuditable) {
//        log.info("插入前：$entity")
        entity.deleted = false
    }

    /**
     * insert 后置回调
     */
    @PostPersist
    fun postPersist(entity: AbstractAuditable) {
//        log.info("插入后：$entity")
    }

    /**
     * delete 前置回调
     */
    @PreRemove
    fun preRemove(entity: AbstractAuditable) {
        log.info("删除前：$entity")
    }

    /**
     * delete 后置回调
     */
    @PostRemove
    fun postRemove(entity: AbstractAuditable) {
        log.info("删除后：$entity")
    }

    /**
     * update 前置回调
     */
    @PreUpdate
    fun preUpdate(entity: AbstractAuditable) {
        log.info("更新前：$entity")
    }

    /**
     * update 后置回调
     */
    @PostUpdate
    fun postUpdate(entity: AbstractAuditable) {
        log.info("更新后：$entity")
    }

    companion object {
        private val log = LogFactory.getLog(CustomAuditingEntityListener::class.java)
    }
}

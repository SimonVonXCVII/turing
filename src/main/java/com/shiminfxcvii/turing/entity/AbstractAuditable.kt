package com.shiminfxcvii.turing.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.shiminfxcvii.turing.listener.CustomAuditingEntityListener
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serial
import java.io.Serializable
import java.time.LocalDateTime

/**
 * 公共实体父类
 *
 * @author ShiminFXCVII
 * @since 2023-04-01 23:08:08
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class, CustomAuditingEntityListener::class)
abstract class AbstractAuditable : Serializable {
    /**
     * 主键 id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: String? = null

    /**
     * 创建主体
     */
    @CreatedBy
    open var createdBy: String? = null

    /**
     * 创建日期
     */
    @CreatedDate
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    open var createdDate: LocalDateTime? = null

    /**
     * 最后修改主体
     */
    @LastModifiedBy
    open var lastModifiedBy: String? = null

    /**
     * 最后修改日期
     */
    @LastModifiedDate
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    open var lastModifiedDate: LocalDateTime? = null

    /**
     * 乐观锁字段
     */
    @Version
    open var version: Int? = null

    /**
     * 逻辑删除标记
     */
    open var deleted: Boolean? = null

    companion object {
        const val ID = "id"
        const val CREATED_BY = "createdBy"
        const val CREATED_DATE = "createdDate"
        const val LAST_MODIFIED_BY = "lastModifiedBy"
        const val LAST_MODIFIED_DATE = "lastModifiedDate"
        const val VERSION = "version"
        const val DELETED = "deleted"

        @Serial
        private val serialVersionUID = 141481953116476081L
    }
}

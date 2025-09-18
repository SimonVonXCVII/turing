package com.simonvonxcvii.turing.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.simonvonxcvii.turing.listener.CustomAuditingEntityListener
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.io.Serializable
import java.time.LocalDateTime

/**
 * 公共实体父类
 * TODO 这样写还需要实现 Serializable 吗
 * 9/18/2025: Gemini 2.5 Pro 说保留现在这样是最好的选择
 *
 * @author Simon Von
 * @since 2023-04-01 23:08:08
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class, CustomAuditingEntityListener::class)
abstract class AbstractAuditable(
    /**
     * 主键 id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, columnDefinition = "INTEGER")
    @Comment("表主键 id")
    open var id: Int = 0,

    /**
     * 创建主体
     */
    @CreatedBy
    @Column(name = "created_by", columnDefinition = "INTEGER")
    @Comment("创建主体")
    open var createdBy: Int? = null,

    /**
     * 创建日期 TODO 通过字节码发现 @field: 才是对的，那么到底是使用 @param: 更好还是 @field: 更好
     */
    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    @CreatedDate
    @Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP")
    @Comment("创建时间")
    open var createdDate: LocalDateTime = LocalDateTime.now(),

    /**
     * 最后修改主体
     */
    @LastModifiedBy
    @Column(name = "last_modified_by", columnDefinition = "INTEGER")
    @Comment("更新主体")
    open var lastModifiedBy: Int? = null,

    /**
     * 最后修改日期
     */
    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = false, columnDefinition = "TIMESTAMP")
    @Comment("更新时间")
    open var lastModifiedDate: LocalDateTime = LocalDateTime.now(),

    /**
     * 乐观锁版本
     */
    @Version
    @Column(name = "version", nullable = false, columnDefinition = "INTEGER")
    @Comment("乐观锁版本")
    open var version: Int = 0,

    /**
     * 逻辑删除标记
     */
    @Column(name = "deleted", nullable = false, columnDefinition = "BOOLEAN")
    @Comment("逻辑删除")
    open var deleted: Boolean = false
) : Serializable {
    companion object {
        const val ID = "id"
        const val CREATED_BY = "createdBy"
        const val CREATED_DATE = "createdDate"
        const val LAST_MODIFIED_BY = "lastModifiedBy"
        const val LAST_MODIFIED_DATE = "lastModifiedDate"
        const val VERSION = "version"
        const val DELETED = "deleted"
    }
}

package com.simonvonxcvii.turing.entity

import com.simonvonxcvii.turing.listener.CustomAuditingEntityListener
import jakarta.persistence.*
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
    @Column(nullable = false, columnDefinition = "INTEGER", comment = "表主键 id")
    open var id: Int = 0,

    /**
     * 创建者
     */
    @CreatedBy
    @Column(columnDefinition = "INTEGER", comment = "创建者")
    open var createdBy: Int? = null,

    /**
     * 创建时间 TODO 通过字节码发现 @field: 才是对的，那么到底是使用 @param: 更好还是 @field: 更好
     */
//    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
//    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    @CreatedDate
    @Column(nullable = false, columnDefinition = "TIMESTAMP", comment = "创建时间")
    open var createdDate: LocalDateTime = LocalDateTime.now(),

    /**
     * 最后修改者
     */
    @LastModifiedBy
    @Column(columnDefinition = "INTEGER", comment = "最后修改者")
    open var lastModifiedBy: Int? = null,

    /**
     * 最后修改时间
     */
//    @field:JsonDeserialize(using = LocalDateTimeDeserializer::class)
//    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
//    @field:JsonSerialize(using = LocalDateTimeSerializer::class)
    @LastModifiedDate
    @Column(nullable = false, columnDefinition = "TIMESTAMP", comment = "最后修改时间")
    open var lastModifiedDate: LocalDateTime = LocalDateTime.now(),

    /**
     * 乐观锁版本
     */
    @Version
    @Column(nullable = false, columnDefinition = "INTEGER", comment = "乐观锁版本")
    open var version: Int = 0,

    /**
     * 逻辑删除标记
     */
    @Column(nullable = false, columnDefinition = "BOOLEAN", comment = "逻辑删除")
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

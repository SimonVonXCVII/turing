package com.simonvonxcvii.turing.entity

import com.simonvonxcvii.turing.enums.FileTypeEnum
import jakarta.persistence.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.util.*

/**
 * 文件表
 * Transient 属性的字段，建议添加到圆括号 () 中，而不是花括号 {} 中，因为可以存在于生成的 toString() 等方法中
 *
 * @author Simon Von
 * @since 2023-04-01 23:08:08
 */
//@RedisHash("turing_app_file")
@Entity
@Table(
    schema = "public",
    name = "turing_app_file",
    uniqueConstraints = [
        UniqueConstraint(name = "con_public_turing_app_file_constraint_1", columnNames = arrayOf("id"))
    ],
    comment = "文件表"
)
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_app_file SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@SQLRestriction("deleted = FALSE")
class AppFile(
    /**
     * 所有者 id
     */
    @Column(nullable = false, columnDefinition = "INTEGER", comment = "所有者 id")
    var ownerId: Int = 0,

    /**
     * 文件名
     */
    @Column(nullable = false, columnDefinition = "VARCHAR(128)", comment = "文件名")
    var filename: String = "",

    /**
     * 原始文件名
     */
    @Column(nullable = false, columnDefinition = "VARCHAR(128)", comment = "原始文件名")
    var originFilename: String = "",

    /**
     * 后缀
     */
    @Column(nullable = false, columnDefinition = "VARCHAR(8)", comment = "后缀")
    var suffix: String = "",

    /**
     * 内容类型
     */
    @Column(nullable = false, columnDefinition = "VARCHAR(128)", comment = "内容类型")
    var contentType: String = "",

    /**
     * 内容长度 TODO Int 就够用了吧？
     */
    @Column(nullable = false, columnDefinition = "BIGINT", comment = "内容长度")
    var contentLength: Long = 0,

    /**
     * md5
     */
    @Column(unique = true, nullable = false, columnDefinition = "VARCHAR(64)", comment = "md5")
    var md5: String = "",

    /**
     * 存放路径 TODO length 不用给这么长吧？
     */
    @Column(nullable = false, columnDefinition = "VARCHAR(1024)", comment = "存放路径")
    var path: String = "",

    /**
     * 业务类型
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(64)", comment = "业务类型")
    var bizType: FileTypeEnum = FileTypeEnum.INFORMATION_COLLECTION,

    /**
     * 备注 TODO length 不用给这么长吧？
     */
    @Column(columnDefinition = "VARCHAR(1024)", comment = "备注")
    var remark: String? = null
) : AbstractAuditable() {
    companion object {
        /**
         * ES 索引名称
         */
        const val INDEX = "turing_app_file"

        /**
         * Redis key 前缀
         */
        const val REDIS_KEY_PREFIX = "$INDEX:"

        const val OWNER_ID = "ownerId"
        const val FILENAME = "filename"
        const val ORIGIN_FILENAME = "originFilename"
        const val SUFFIX = "suffix"
        const val CONTENT_TYPE = "contentType"
        const val CONTENT_LENGTH = "contentLength"
        const val MD5 = "md5"
        const val PATH = "path"
        const val BIZ_TYPE = "bizType"
        const val REMARK = "remark"
    }
}

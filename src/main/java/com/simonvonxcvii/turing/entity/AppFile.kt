package com.simonvonxcvii.turing.entity

import com.simonvonxcvii.turing.enums.FileTypeEnum
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.util.*

/**
 * 文件表
 *
 * @author Simon Von
 * @since 2023-04-01 23:08:08
 */
@Entity
@Table(
    schema = "public",
    name = "turing_app_file",
    uniqueConstraints = [
        UniqueConstraint(name = "con_public_turing_app_file_constraint_1", columnNames = arrayOf("id")),
        UniqueConstraint(columnNames = arrayOf("md5"))
    ]
)
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_app_file SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@SQLRestriction("deleted = FALSE")
data class AppFile(
    /**
     * 所有者 id
     */
    @Column(name = "owner_id", nullable = false, columnDefinition = "INTEGER")
    @Comment("所有者 id")
    var ownerId: Int = 0,

    /**
     * 文件名
     */
    @Column(name = "filename", nullable = false, columnDefinition = "VARCHAR", length = 128)
    @Comment("文件名")
    var filename: String = "",

    /**
     * 原始文件名
     */
    @Column(name = "origin_filename", nullable = false, columnDefinition = "VARCHAR", length = 128)
    @Comment("原始文件名")
    var originFilename: String = "",

    /**
     * 后缀
     */
    @Column(name = "suffix", nullable = false, columnDefinition = "VARCHAR", length = 8)
    @Comment("后缀")
    var suffix: String = "",

    /**
     * 内容类型
     */
    @Column(name = "content_type", nullable = false, columnDefinition = "VARCHAR", length = 128)
    @Comment("内容类型")
    var contentType: String = "",

    /**
     * 内容长度 TODO Int 就够用了吧？
     */
    @Column(name = "content_length", nullable = false, columnDefinition = "BIGINT")
    @Comment("内容长度")
    var contentLength: Long = 0,

    /**
     * md5
     */
    @Column(name = "md5", nullable = false, columnDefinition = "VARCHAR", length = 64)
    @Comment("md5")
    var md5: String = "",

    /**
     * 存放路径 TODO length 不用给这么长吧？
     */
    @Column(name = "path", nullable = false, columnDefinition = "VARCHAR", length = 1024)
    @Comment("存放路径")
    var path: String = "",

    /**
     * 业务类型
     */
    @Column(name = "biz_type", nullable = false, columnDefinition = "SMALLINT")
    @Comment("业务类型")
    var bizType: FileTypeEnum = FileTypeEnum.INFORMATION_COLLECTION,

    /**
     * 备注 TODO length 不用给这么长吧？
     */
    @Column(name = "remark", columnDefinition = "VARCHAR", length = 1024)
    @Comment("备注")
    var remark: String = ""
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

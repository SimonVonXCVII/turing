package com.simonvonxcvii.turing.entity

import com.simonvonxcvii.turing.enums.DictTypeEnum
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

/**
 * 字典表
 * Transient 属性的字段，建议添加到圆括号 () 中，而不是花括号 {} 中，因为可以存在于生成的 toString() 等方法中
 *
 * @author Simon Von
 * @since 2022-12-30 12:49:40
 */
@Entity
@Table(
    schema = "public",
    name = "turing_dict",
    uniqueConstraints = [
        UniqueConstraint(name = "con_public_turing_dict_constraint_1", columnNames = arrayOf("id"))
    ]
)
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_dict SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@SQLRestriction("deleted = FALSE")
data class Dict(
    /**
     * 字典类型
     */
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(64)")
    @Comment("字典类型")
    var type: DictTypeEnum = DictTypeEnum.AREA,

    /**
     * 上级 id
     */
    @Column(columnDefinition = "INTEGER")
    @Comment("上级 id")
    var pid: Int? = null,

    /**
     * 字典名称
     */
    @Column(nullable = false, columnDefinition = "VARCHAR(32)")
    @Comment("字典名称")
    var name: String = "",

    /**
     * 字典值
     */
    @Column(nullable = false, columnDefinition = "VARCHAR(32)")
    @Comment("字典值")
    var value: String = "",

    /**
     * 说明
     */
    @Column(columnDefinition = "VARCHAR(128)")
    @Comment("说明")
    var description: String? = null,

    /**
     * 排序
     */
    @Column(columnDefinition = "SMALLINT")
    @Comment("排序")
    var sort: Short? = 0
) : AbstractAuditable() {
    companion object {
        /**
         * ES 索引名称
         */
        const val INDEX: String = "turing_dict"

        /**
         * Redis key 前缀
         */
        const val REDIS_KEY_PREFIX: String = "$INDEX:"

        const val TYPE: String = "type"
        const val PID: String = "pid"
        const val NAME: String = "name"
        const val VALUE: String = "value"
        const val DESCRIPTION: String = "description"
        const val STATUS: String = "status"
        const val SORT: String = "sort"
    }
}

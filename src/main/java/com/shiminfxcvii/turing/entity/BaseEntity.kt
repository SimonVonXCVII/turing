package com.shiting.soil.entity

import com.baomidou.mybatisplus.annotation.FieldFill
import com.baomidou.mybatisplus.annotation.OrderBy
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.Version
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import lombok.Data
import java.io.Serial
import java.io.Serializable
import java.time.LocalDateTime

@Data
open class BaseEntity : Serializable {
    /**
     * 主键 id
     * 主键类型跟随 [com.baomidou.mybatisplus.core.config.GlobalConfig]
     * OrderBy(asc = true) 说明: 默认倒序，设置 true 为顺序
     * 注：根据数字排序通常比根据时间排序快速
     * 如果需要个性化指定排序规则，请在查询语句中自定义
     */
    @OrderBy(asc = true)
    var id: String? = null

    /**
     * 创建主体
     */
    @TableField(fill = FieldFill.INSERT)
    var createBy: String? = null

    /**
     * 创建时间
     */
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @TableField(fill = FieldFill.INSERT)
    var createTime: LocalDateTime? = null

    /**
     * 更新主体
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    var updateBy: String? = null

    /**
     * 更新时间
     * update = "now()" 说明: 每次更新数据都将该字段更新为当前时间(now() 为 SQL 函数)
     */
    @JsonDeserialize(using = LocalDateTimeDeserializer::class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer::class)
    @TableField(fill = FieldFill.INSERT_UPDATE, update = "now()")
    var updateTime: LocalDateTime? = null

    /**
     * 逻辑删除标记
     */
    @TableField(fill = FieldFill.INSERT)
    var deleted: Boolean? = null

    /**
     * 乐观锁字段
     */
    @TableField(fill = FieldFill.INSERT)
    @Version
    var version: Int? = null

    companion object {
        const val ID = "id"
        const val CREATE_BY = "create_by"
        const val CREATE_TIME = "create_time"
        const val UPDATE_BY = "update_by"
        const val UPDATE_TIME = "update_time"
        const val DELETED = "deleted"
        const val VERSION = "version"

        @Serial
        private val serialVersionUID = 1L
    }
}
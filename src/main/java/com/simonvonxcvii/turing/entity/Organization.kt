package com.simonvonxcvii.turing.entity

import com.simonvonxcvii.turing.enums.OrganizationTypeEnum
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

/**
 * 单位表
 * Transient 属性的字段，建议添加到圆括号 () 中，而不是花括号 {} 中，因为可以存在于生成的 toString() 等方法中
 * @jakarta.persistence.Transient 是最佳选择：
 * 作用：
 * 告诉 JPA 提供者（Hibernate/EclipseLink 等）：这个属性不是持久化字段，不要映射到数据库表，也不要生成列。
 * 在 Spring Boot JPA 项目中这是标准的做法。
 * 适用场景：
 * JPA 实体类中某个属性只是运行期的临时值，或者是计算值，不需要入库。
 *
 * @author Simon Von
 * @since 2022-12-29 11:33:31
 */
@Entity
@Table(
    schema = "public",
    name = "turing_organization",
    uniqueConstraints = [
        UniqueConstraint(name = "con_public_turing_organization_constraint_1", columnNames = arrayOf("id"))
    ]
)
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_organization SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@SQLRestriction("deleted = FALSE")
data class Organization(
    /**
     * 上级单位 id
     */
    @Column(columnDefinition = "INTEGER")
    @Comment("上级单位 id")
    var pid: Int? = null,

    /**
     * 单位名称
     */
    @Column(unique = true, nullable = false, columnDefinition = "VARCHAR(64)")
    @Comment("单位名称")
    var name: String = "",

    /**
     * 信用代码
     */
    @Column(unique = true, nullable = false, columnDefinition = "VARCHAR(18)")
    @Comment("单位名称")
    var code: String = "",

    /**
     * 单位法人
     */
    @Column(nullable = false, columnDefinition = "VARCHAR(32)")
    @Comment("单位地址详情")
    var legalPerson: String = "",

    /**
     * 单位联系电话
     */
    @Column(unique = true, nullable = false, columnDefinition = "VARCHAR(32)")
    @Comment("单位联系电话")
    var phone: String = "",

    /**
     * 单位类型
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(64)")
    @Comment("单位类型")
    var type: OrganizationTypeEnum = OrganizationTypeEnum.PLATFORM,

    /**
     * 单位所在省（市、区）编码
     */
    @Column(nullable = false, columnDefinition = "INTEGER")
    @Comment("单位所在省（市、区）编码")
    var provinceCode: Int = 0,

    /**
     * 单位所在市（州、盟）编码
     */
    @Column(nullable = false, columnDefinition = "INTEGER")
    @Comment("单位所在市（州、盟）编码")
    var cityCode: Int = 0,

    /**
     * 单位所在县（市、旗）编码
     */
    @Column(nullable = false, columnDefinition = "INTEGER")
    @Comment("单位所在区县（市、旗）编码")
    var districtCode: Int = 0,

    /**
     * 单位所在省（市、区）名称
     */
    @Column(nullable = false, columnDefinition = "VARCHAR(16)")
    @Comment("单位所在省（市、区）名称")
    var provinceName: String = "",

    /**
     * 单位所在市（州、盟）名称
     */
    @Column(nullable = false, columnDefinition = "VARCHAR(16)")
    @Comment("单位所在市（州、盟）名称")
    var cityName: String = "",

    /**
     * 单位所在县（市、旗）名称
     */
    @Column(nullable = false, columnDefinition = "VARCHAR(16)")
    @Comment("单位所在区县（市、旗）名称")
    var districtName: String = "",

    /**
     * 单位地址详情
     */
    @Column(nullable = false, columnDefinition = "VARCHAR(128)")
    @Comment("单位地址详情")
    var address: String = "",

    /**
     * 单位性质
     */
    @Transient
    var property: String? = null,

    /**
     * 单位管理员 id
     */
    @Transient
    var orgManagerId: String? = null,

    /**
     * 单位管理员姓名
     */
    @Transient
    var orgManagerName: String? = null,

    /**
     * 单位管理员电话
     */
    @Transient
    var orgManagerMobile: String? = null,

    /**
     * 单位状态
     */
    @Transient
    var status: Int? = null,

    /**
     * 单位剩余时间
     */
    @Transient
    var remainingTime: Int? = null,

    /**
     * 单位法人姓名
     */
    @Transient
    var openPersonName: String? = null,

    /**
     * 单位等级
     */
    @Transient
    var orgLevel: String? = null
) : AbstractAuditable() {
    companion object {
        /**
         * ES 索引名称
         */
        const val ES_INDEX = "turing_organization"

        /**
         * Redis key 前缀
         */
        const val REDIS_KEY_PREFIX = "$ES_INDEX:"

        const val PID = "pid"
        const val NAME = "name"
        const val CODE = "code"
        const val TYPE = "type"
        const val PROVINCE_CODE = "provinceCode"
        const val CITY_CODE = "cityCode"
        const val DISTRICT_CODE = "districtCode"
        const val ADDRESS = "address"
        const val LEGAL_PERSON = "legalPerson"
        const val PHONE = "phone"
    }
}

package com.simonvonxcvii.turing.entity

import com.simonvonxcvii.turing.enums.OrganizationBusinessBusinessLinksEnum
import com.simonvonxcvii.turing.enums.OrganizationBusinessLevelEnum
import com.simonvonxcvii.turing.enums.OrganizationBusinessQualityControlTypeEnum
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

/**
 * 单位业务表
 * Transient 属性的字段，建议添加到圆括号 () 中，而不是花括号 {} 中，因为可以存在于生成的 toString() 等方法中
 *
 * @author Simon Von
 * @since 2022-12-29 11:33:31
 */
@Entity
@Table(
    schema = "public",
    name = "turing_organization_business",
    uniqueConstraints = [
        UniqueConstraint(name = "con_public_turing_organization_business_constraint_1", columnNames = arrayOf("id"))
    ]
)
// @SQLDelete 只支持 delete(T entity) 和 deleteById(ID id)
@SQLDelete(sql = "UPDATE turing_organization_business SET deleted = TRUE WHERE id = ? AND version = ? AND deleted = FALSE")
@SQLRestriction("deleted = FALSE")
data class OrganizationBusiness(
    /**
     * 单位 id
     */
    @Column(nullable = false, columnDefinition = "INTEGER")
    @Comment("单位 id")
    var orgId: Int = 0,

    /**
     * 单位名称
     */
    @Column(nullable = false, columnDefinition = "VARCHAR(128)")
    @Comment("单位名称")
    var orgName: String = "",

    /**
     * 业务环节
     * TODO MutableSet<OrganizationBusinessBusinessLinksEnum>? 还是 MutableSet<OrganizationBusinessBusinessLinksEnum?>?
     */
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR[]")
    @Comment("业务环节")
    var link: MutableSet<OrganizationBusinessBusinessLinksEnum?>? = null,

    /**
     * 质控类型
     */
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR[]")
    @Comment("质控类型")
    var type: MutableSet<OrganizationBusinessQualityControlTypeEnum?>? = null,

    /**
     * 业务申请所在省（市、区）编码
     */
    @Column(nullable = false, columnDefinition = "INTEGER")
    @Comment("业务申请所在省（市、区）编码")
    var provinceCode: Int = 0,

    /**
     * 业务申请所在市（州、盟）编码
     */
    @Column(columnDefinition = "INTEGER")
    @Comment("业务申请所在市（州、盟）编码")
    var cityCode: Int? = null,

    /**
     * 业务申请所在县（市、旗）编码
     */
    @Column(columnDefinition = "INTEGER")
    @Comment("业务申请所在区县（市、旗）编码")
    var districtCode: Int? = null,

    /**
     * 业务申请所在省（市、区）名称
     */
    @Column(nullable = false, columnDefinition = "VARCHAR(16)")
    @Comment("业务申请所在省（市、区）名称")
    var provinceName: String = "",

    /**
     * 业务申请所在市（州、盟）名称
     */
    @Column(columnDefinition = "VARCHAR(16)")
    @Comment("业务申请所在市（州、盟）名称")
    var cityName: String? = null,

    /**
     * 业务申请所在县（市、旗）名称
     */
    @Column(columnDefinition = "VARCHAR(16)")
    @Comment("业务申请所在区县（市、旗）名称")
    var districtName: String? = null,

    /**
     * 业务申请状态
     */
    @Column(nullable = false, columnDefinition = "VARCHAR(3)")
    @Comment("业务申请状态")
    var state: String = "",

    /**
     * 申请业务级别
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(64)")
    @Comment("申请业务级别")
    var businessLevel: OrganizationBusinessLevelEnum = OrganizationBusinessLevelEnum.DISTRICT
) : AbstractAuditable() {
    companion object {
        /**
         * ES 索引名称
         */
        const val INDEX: String = "turing_organization_business"

        /**
         * Redis key 前缀
         */
        const val REDIS_KEY_PREFIX: String = "$INDEX:"

        const val ORG_ID: String = "orgId"
        const val ORG_NAME: String = "orgName"
        const val LINK: String = "link"
        const val TYPE: String = "type"
        const val PROVINCE_CODE: String = "provinceCode"
        const val CITY_CODE: String = "cityCode"
        const val DISTRICT_CODE: String = "districtCode"
        const val PROVINCE_NAME: String = "provinceName"
        const val CITY_NAME: String = "cityName"
        const val DISTRICT_NAME: String = "districtName"
        const val STATE: String = "state"
        const val BUSINESS_LEVEL: String = "businessLevel"
    }
}

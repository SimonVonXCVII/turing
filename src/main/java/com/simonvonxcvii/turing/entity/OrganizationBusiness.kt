package com.simonvonxcvii.turing.entity

import com.simonvonxcvii.turing.enums.OrganizationBusinessLevelEnum
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.Comment
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

/**
 * 单位业务表
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
    @Column(name = "org_id", nullable = false, columnDefinition = "INTEGER")
    @Comment("单位 id")
    var orgId: Int = 0,

    /**
     * 单位名称
     */
    @Column(name = "org_name", nullable = false, columnDefinition = "VARCHAR", length = 128)
    @Comment("单位名称")
    var orgName: String = "",

    /**
     * 业务环节
     *
     * @see com.simonvonxcvii.turing.enums.OrganizationBusinessBusinessLinksEnum
     */
    @Column(name = "link", columnDefinition = "VARCHAR", length = 128)
    @Comment("业务环节")
    var link: String? = null,

    /**
     * 质控类型
     *
     * @see com.simonvonxcvii.turing.enums.OrganizationBusinessQualityControlTypeEnum
     */
    @Column(name = "type", columnDefinition = "VARCHAR", length = 128)
    @Comment("质控类型")
    var type: String? = null,

    /**
     * 业务申请所在省（市、区）编码
     */
    @Column(name = "province_code", nullable = false, columnDefinition = "INTEGER")
    @Comment("业务申请所在省（市、区）编码")
    var provinceCode: Int = 0,

    /**
     * 业务申请所在市（州、盟）编码
     */
    @Column(name = "city_code", columnDefinition = "INTEGER")
    @Comment("业务申请所在市（州、盟）编码")
    var cityCode: Int? = null,

    /**
     * 业务申请所在县（市、旗）编码
     */
    @Column(name = "district_code", columnDefinition = "INTEGER")
    @Comment("业务申请所在区县（市、旗）编码")
    var districtCode: Int? = null,

    /**
     * 业务申请所在省（市、区）名称
     */
    @Column(name = "province_name", nullable = false, columnDefinition = "VARCHAR", length = 16)
    @Comment("业务申请所在省（市、区）名称")
    var provinceName: String = "",

    /**
     * 业务申请所在市（州、盟）名称
     */
    @Column(name = "city_name", columnDefinition = "VARCHAR", length = 16)
    @Comment("业务申请所在市（州、盟）名称")
    var cityName: String? = null,

    /**
     * 业务申请所在县（市、旗）名称
     */
    @Column(name = "district_name", columnDefinition = "VARCHAR", length = 16)
    @Comment("业务申请所在区县（市、旗）名称")
    var districtName: String? = null,

    /**
     * 业务申请状态
     */
    @Column(name = "state", nullable = false, columnDefinition = "VARCHAR", length = 3)
    @Comment("业务申请状态")
    var state: String = "",

    /**
     * 申请业务级别
     */
    @Column(name = "business_level", columnDefinition = "VARCHAR", length = 16)
    @Comment("申请业务级别")
    var businessLevel: OrganizationBusinessLevelEnum? = null
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

package com.simonvonxcvii.turing.model.dto

import com.simonvonxcvii.turing.model.query.PageQuery
import com.simonvonxcvii.turing.utils.Insert
import com.simonvonxcvii.turing.utils.Update
import jakarta.validation.constraints.NotNull

/**
 * Organization Business DTO
 * 
 * @author Simon Von
 * @since 1/4/2023 4:59 PM
 */
data class OrganizationBusinessDTO(
    /**
     * 业务 id
     */
    @field:NotNull(message = "业务 id 不能为空", groups = [Update::class])
    var id: Int? = null,

    /**
     * 单位名称
     */
    var orgName: String? = null,

    /**
     * 业务申请所在省（市、区）编码
     */
    @field:NotNull(message = "业务申请所在省不能为空", groups = [Insert::class])
    var provinceCode: Int? = null,

    /**
     * 业务申请所在市（州、盟）编码
     */
    var cityCode: Int? = null,

    /**
     * 业务申请所在区县（市、旗）编码
     */
    var districtCode: Int? = null,

    /**
     * 业务申请所在省（市、区）名称
     */
    var provinceName: String? = null,

    /**
     * 业务申请所在市（州、盟）名称
     */
    var cityName: String? = null,

    /**
     * 业务申请所在区县（市、旗）名称
     */
    var districtName: String? = null,

    /**
     * 业务环节 TODO，尝试改成 Set<String> 或者 Set<OrganizationBusinessBusinessLinksEnum>
     */
    var link: Array<String>?,

    /**
     * 质控类型 TODO，尝试改成 Set<String> 或者 Set<OrganizationBusinessQualityControlTypeEnum>
     */
    var type: Array<String>?,

    /**
     * 业务申请状态
     */
    @field:NotNull(message = "业务申请状态不能为空", groups = [Update::class])
    var state: String? = null
) : PageQuery()

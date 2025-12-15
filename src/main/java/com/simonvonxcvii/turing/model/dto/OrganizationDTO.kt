package com.simonvonxcvii.turing.model.dto

import com.simonvonxcvii.turing.model.query.PageQuery
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import java.time.LocalDateTime

/**
 * Organization DTO
 * 
 * @author Simon Von
 * @since 12/30/2022 4:03 PM
 */
class OrganizationDTO(
    /**
     * 单位所在省名称
     */
    var provinceName: String? = null,

    /**
     * 单位所在市名称
     */
    var cityName: String? = null,

    /**
     * 单位所在县名称
     */
    var districtName: String? = null,

    /**
     * 主键 id
     */
    var id: Int? = null,

    /**
     * 单位名称
     */
    @field:NotBlank(message = "单位名称不能为空")
    var name: String? = null,

    /**
     * 信用代码
     */
    @field:NotBlank(message = "信用代码不能为空")
    @field:Pattern(regexp = "^\\w{18}$", message = "请输入正确的十八位信用代码")
    var code: String? = null,

    /**
     * 单位类型
     */
    @field:NotBlank(message = "单位类型不能为空")
    var type: String? = null,

    /**
     * 单位所在省（市、区）编码
     */
    @field:NotNull(message = "单位所在省（市、区）不能为空")
    var provinceCode: Int? = null,

    /**
     * 单位所在市（州、盟）编码
     */
    @field:NotNull(message = "单位所在市（州、盟）不能为空")
    var cityCode: Int? = null,

    /**
     * 单位所在县（市、旗）编码
     */
    @field:NotNull(message = "单位所在县（市、旗）不能为空")
    var districtCode: Int? = null,

    /**
     * 单位地址详情
     */
    @field:NotBlank(message = "单位地址详情不能为空")
    var address: String? = null,

    /**
     * 单位法人
     */
    @field:NotBlank(message = "单位法人不能为空")
    var legalPerson: String? = null,

    /**
     * 联系电话
     */
    @field:NotBlank(message = "联系电话不能为空")
    var phone: String? = null,

    /**
     * 创建时间
     */
    var createdDate: LocalDateTime? = null
) : PageQuery()

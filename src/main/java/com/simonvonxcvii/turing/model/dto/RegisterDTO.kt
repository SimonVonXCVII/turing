package com.simonvonxcvii.turing.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

/**
 * Register DTO
 * 
 * @author Simon Von
 * @since 12/29/2022 11:45 AM
 */
data class RegisterDTO(
    /**
     * 单位信息 -------------------------------------------
     * 单位名称
     */
    @field:NotBlank(message = "单位名称不能为空")
    var name: String? = null,

    /**
     * 信用代码
     */
    @field:NotBlank(message = "信用代码不能为空")
    @field:Size(min = 18, max = 18, message = "请填写正确的信用代码")
    var code: String? = null,

    /**
     * 单位所在省
     */
    @field:NotNull(message = "单位所在省不能为空")
    var provinceCode: Int? = null,

    /**
     * 单位所在市
     */
    @field:NotNull(message = "单位所在市不能为空")
    var cityCode: Int? = null,

    /**
     * 单位所在县
     */
    @field:NotNull(message = "单位所在县不能为空")
    var districtCode: Int? = null,

    /**
     * 单位地址详情
     */
    var address: String? = null,

    /**
     * 单位法人
     */
    var legalPerson: String? = null,

    /**
     * 联系电话
     */
    var phone: String? = null,

    /**
     * 用户信息 -------------------------------------------
     * 用户姓名
     */
    @field:NotBlank(message = "用户姓名不能为空")
    var nickName: String? = null,

    /**
     * 用户手机号
     */
    @field:NotNull(message = "用户手机号不能为空")
    var mobile: Long? = null,

    /**
     * 登录账号
     */
    @field:NotBlank(message = "登录账号不能为空")
    var username: String? = null,

    /**
     * 性别
     */
    var gender: String? = null
)

package com.simonvonxcvii.turing.model.dto;

import com.simonvonxcvii.turing.model.query.PageQuery;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Accessors(chain = true)
@Getter
@Setter
public class OrganizationDTO extends PageQuery {
    /**
     * 单位所在省名称
     */
    public String provinceName;
    /**
     * 单位所在市名称
     */
    public String cityName;
    /**
     * 单位所在县名称
     */
    public String districtName;
    /**
     * 主键 id
     */
    private Integer id;
    /**
     * 单位名称
     */
    @NotBlank(message = "单位名称不能为空")
    private String name;
    /**
     * 信用代码
     */
    @NotBlank(message = "信用代码不能为空")
    @Pattern(regexp = "^\\w{18}$", message = "请输入正确的十八位信用代码")
    private String code;
    /**
     * 单位类型
     */
    @NotBlank(message = "单位类型不能为空")
    private String type;
    /**
     * 单位所在省（市、区）编码
     */
    @NotNull(message = "单位所在省（市、区）不能为空")
    private Integer provinceCode;
    /**
     * 单位所在市（州、盟）编码
     */
    @NotNull(message = "单位所在市（州、盟）不能为空")
    private Integer cityCode;
    /**
     * 单位所在县（市、旗）编码
     */
    @NotNull(message = "单位所在县（市、旗）不能为空")
    private Integer districtCode;
    /**
     * 单位地址详情
     */
    @NotBlank(message = "单位地址详情不能为空")
    private String address;
    /**
     * 单位法人
     */
    @NotBlank(message = "单位法人不能为空")
    private String legalPerson;
    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String phone;
    /**
     * 创建时间
     */
    private LocalDateTime createdDate;
}

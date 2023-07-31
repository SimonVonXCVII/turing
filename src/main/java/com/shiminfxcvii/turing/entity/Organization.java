package com.shiminfxcvii.turing.entity;

import com.shiminfxcvii.turing.enums.OrganizationTypeEnum;
import com.shiting.soil.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 单位表
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-29 11:33:31
 */
@Getter
@Setter
@Accessors(chain = true)
public class Organization extends BaseEntity {

    /**
     * 单位名称
     */
    private String name;

    /**
     * 信用代码
     */
    private String code;

    /**
     * 上级单位 id
     */
    private String pid;

    /**
     * 单位类型
     */
    private OrganizationTypeEnum type;

    /**
     * 单位所在省
     */
    private String provinceCode;

    /**
     * 单位所在市
     */
    private String cityCode;

    /**
     * 单位所在县
     */
    private String districtCode;

    /**
     * 单位地址详情
     */
    private String address;

    /**
     * 单位法人
     */
    private String legalPerson;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 组织机构成立日期
     */
    private LocalDateTime establishmentDate;

}
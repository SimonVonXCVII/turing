package com.shiminfxcvii.turing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.shiminfxcvii.turing.enums.OrganizationBusinessBusinessLinksEnum;
import com.shiminfxcvii.turing.enums.OrganizationBusinessLevelEnum;
import com.shiminfxcvii.turing.enums.OrganizationBusinessQualityControlTypeEnum;
import com.shiting.soil.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 单位业务表
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-29 11:33:31
 */
@Accessors(chain = true)
@Getter
@Setter
@TableName("soil_organization_business")
public class OrganizationBusiness extends BaseEntity {

    /**
     * ES 索引名称
     */
    public static final String INDEX = "soil_organization_business";

    /**
     * Redis key 前缀
     */
    public static final String REDIS_KEY_PREFIX = INDEX + ":";

    /**
     * 单位 id
     */
    private String orgId;

    /**
     * 单位名称
     */
    private String orgName;

    /**
     * 业务环节
     *
     * @see OrganizationBusinessBusinessLinksEnum
     */
    private String link;

    /**
     * 质控类型
     *
     * @see OrganizationBusinessQualityControlTypeEnum
     */
    private String type;

    /**
     * 业务申请所在省区号
     */
    private Integer provinceCode;

    /**
     * 业务申请所在市区号
     */
    private Integer cityCode;

    /**
     * 业务申请所在区县区号
     */
    private Integer districtCode;

    /**
     * 业务申请所在省名称
     */
    private String provinceName;

    /**
     * 业务申请所在市名称
     */
    private String cityName;

    /**
     * 业务申请所在区县名称
     */
    private String districtName;

    /**
     * 业务申请状态
     */
    private String state;

    /**
     * 申请业务级别
     */
    private OrganizationBusinessLevelEnum businessLevel;

}
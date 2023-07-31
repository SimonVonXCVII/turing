package com.shiminfxcvii.turing.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 单位业务 Query
 *
 * @author ShiminFXCVII
 * @since 1/4/2023 4:59 PM
 */
@Schema(name = "单位业务 Query")
@Getter
@Setter
public class OrganizationBusinessQuery extends PageQuery {

    /**
     * 单位名称
     */
    @Schema(description = "单位名称")
    private String orgName;

    /**
     * 业务申请所在省
     */
    @Schema(description = "业务申请所在省")
    private Integer provinceCode;

    /**
     * 业务申请所在市
     */
    @Schema(description = "业务申请所在市")
    private Integer cityCode;

    /**
     * 业务申请所在区县
     */
    @Schema(description = "业务申请所在区县")
    private Integer districtCode;

    /**
     * 业务环节
     */
    @Schema(description = "业务环节")
    private String link;

    /**
     * 质控类型
     */
    @Schema(description = "质控类型")
    private String type;

    /**
     * 业务申请状态
     */
    @Schema(description = "业务申请状态")
    private String state;

}
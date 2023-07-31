package com.shiminfxcvii.turing.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 单位申请业务实体类
 *
 * @author ShiminFXCVII
 * @since 1/4/2023 4:59 PM
 */
@Schema(name = "单位申请业务实体类")
@Getter
@Setter
public class OrganizationBusinessDTO {

    /**
     * 业务 id
     */
    @Schema(description = "业务 id")
    private String id;

    /**
     * 单位名称
     */
    @Schema(description = "单位名称")
    private String orgName;

    /**
     * 业务申请所在省
     */
    @Schema(description = "业务申请所在省")
    private String provinceName;

    /**
     * 业务申请所在市
     */
    @Schema(description = "业务申请所在市")
    private String cityName;

    /**
     * 业务申请所在区县
     */
    @Schema(description = "业务申请所在区县")
    private String districtName;

    /**
     * 业务环节
     */
    @Schema(description = "业务环节")
    private String[] link;

    /**
     * 质控类型
     */
    @Schema(description = "质控类型")
    private String[] type;

    /**
     * 业务申请状态
     */
    @Schema(description = "业务申请状态")
    private String state;

}
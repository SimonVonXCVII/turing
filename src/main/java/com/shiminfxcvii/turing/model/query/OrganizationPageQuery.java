package com.shiminfxcvii.turing.model.query;

import com.shiminfxcvii.turing.enums.OrganizationTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationPageQuery extends PageQuery {

    /**
     * 单位名称
     */
    private String orgName;

    /**
     * 单位类型
     */
    private OrganizationTypeEnum orgType;

    private String provinceCode;

    private String cityCode;

    private String districtCode;

    /**
     * 前端忽略这个参数
     */
    @Schema(description = "前端忽略该参数")
    private Integer type;
}
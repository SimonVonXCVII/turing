package com.shiminfxcvii.turing.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * the PageQuery
 *
 * @author ShiminFXCVII
 * @since 3/7/2023 5:32 PM
 */
@Schema(name = "RoleQuery")
@Getter
@Setter
public class RoleQuery extends PageQuery {

    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String name;
    /**
     * 状态
     */
    @Schema(description = "角色编码")
    private String code;
    /**
     * 备注
     */
    @Schema(description = "角色说明")
    private String description;

}
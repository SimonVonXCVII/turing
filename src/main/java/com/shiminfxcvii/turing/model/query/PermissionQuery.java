package com.shiminfxcvii.turing.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * the permission query
 *
 * @author ShiminFXCVII
 * @since 3/4/2023 10:07 PM
 */
@Schema(name = "PermissionQuery")
@Getter
@Setter
public class PermissionQuery {

    /**
     * 权限名称
     */
    @Schema(description = "权限名称")
    private String name;
    /**
     * 权限编码
     */
    @Schema(description = "权限编码")
    private String code;
    /**
     * 权限排序
     */
    @Schema(description = "权限排序")
    private Integer sort;

}
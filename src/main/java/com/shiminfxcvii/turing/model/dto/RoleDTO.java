package com.shiminfxcvii.turing.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
@Schema(name = "RoleDTO")
public class RoleDTO {

    /**
     * 角色 id
     */
    @Schema(description = "角色 id")
    private String id;
    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String name;
    /**
     * 角色编码
     */
    @Schema(description = "角色编码")
    private String code;
    /**
     * 角色说明
     */
    @Schema(description = "角色说明")
    private String description;
    /**
     * 角色权限
     */
    @Schema(description = "角色权限")
    private List<String> permissionIdList;

}
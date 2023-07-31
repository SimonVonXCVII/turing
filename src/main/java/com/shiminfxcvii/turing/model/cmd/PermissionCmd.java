package com.shiminfxcvii.turing.model.cmd;

import com.shiminfxcvii.turing.utils.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "PermissionCmd")
@Getter
@Setter
public class PermissionCmd {

    /**
     * 权限 id
     */
    @Schema(description = "权限 id")
    @NotNull(message = "权限 id 不能为空", groups = Update.class)
    private String id;

    /**
     * 权限名称
     */
    @Schema(description = "权限名称")
    @NotBlank(message = "权限名称不能为空")
    private String name;

    /**
     * 权限编码
     */
    @Schema(description = "权限编码")
    private String code;

    /**
     * 排序
     */
    @Schema(description = "排序")
    @NotNull(message = "排序不能为空")
    private Integer sort;

    /**
     * 上级权限 id
     */
    @Schema(description = "上级权限 id")
    private String pid;

}
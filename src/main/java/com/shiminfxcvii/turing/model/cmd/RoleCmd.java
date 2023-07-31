package com.shiminfxcvii.turing.model.cmd;

import com.shiminfxcvii.turing.utils.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema(name = "RoleCmd")
@Getter
@Setter
public class RoleCmd {

    /**
     * 角色 id
     */
    @Schema(description = "角色 id")
    @NotNull(message = "角色 id 不能为空", groups = Update.class)
    private String id;
    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    @NotBlank(message = "角色名称不能为空")
    private String name;
    /**
     * 角色编码
     */
    @Schema(description = "角色编码")
    @NotBlank(message = "角色编码不能为空")
    private String code;
    /**
     * 角色说明
     */
    @Schema(description = "角色说明")
    @NotBlank(message = "角色说明不能为空")
    private String description;
    /**
     * 权限 id 集合
     */
    @Schema(description = "权限 id 集合")
    @NotEmpty(message = "权限 id 集合不能为空")
    private List<String> permissionIdList;

}
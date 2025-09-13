package com.simonvonxcvii.turing.model.dto;

import com.simonvonxcvii.turing.model.query.PageQuery;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Getter
@Setter
public class RoleDTO extends PageQuery {
    /**
     * 角色 id
     */
    private Integer id;
    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String name;
    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    private String authority;
    /**
     * 角色说明
     */
    @NotBlank(message = "角色说明不能为空")
    private String description;
    /**
     * 权限 id 集合
     */
    @NotEmpty(message = "权限 id 集合不能为空")
    private List<Integer> permissionIdList;
}

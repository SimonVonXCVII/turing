package com.simonvonxcvii.turing.model.dto;

import com.simonvonxcvii.turing.model.query.PageQuery;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedList;
import java.util.List;

@Accessors(chain = true)
@Getter
@Setter
public class PermissionDTO extends PageQuery {
    /**
     * 权限 id
     */
    private Integer id;
    /**
     * 上级权限 id
     */
    private Integer pid;
    /**
     * 权限名称
     */
    @NotBlank(message = "权限名称不能为空")
    private String name;
    /**
     * 权限编码
     */
    @NotBlank(message = "权限名称不能为空")
    private String code;
    /**
     * 排序编号
     */
    @NotNull(message = "排序编号不能为空")
    private Short sort;
    /**
     * 子级权限集合
     */
    private List<PermissionDTO> children = new LinkedList<>();
}

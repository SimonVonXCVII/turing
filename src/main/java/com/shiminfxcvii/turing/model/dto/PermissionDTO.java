package com.shiminfxcvii.turing.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Schema(name = "PermissionDTO")
@Getter
@Setter
public class PermissionDTO {

    /**
     * 权限 id
     */
    @Schema(description = "权限 id")
    private String id;
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
    /**
     * 上级权限 id
     */
    @Schema(description = "上级权限 id")
    private String pid;
    /**
     * 子级权限集合
     */
    @Schema(description = "子级权限集合")
    private List<PermissionDTO> children = new LinkedList<>();

}
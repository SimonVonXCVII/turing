package com.shiminfxcvii.turing.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 字典 DTO
 *
 * @author ShiminFXCVII
 * @since 12/30/2022 4:03 PM
 */
@Schema(description = "字典 DTO")
@Getter
@Setter
public class DictDTO {

    /**
     * 字典 id
     */
    @Schema(description = "字典 id")
    private String id;

    /**
     * 字典名称
     */
    @Schema(description = "字典名称")
    private String name;

    /**
     * 字典值
     */
    @Schema(description = "字典值")
    private String value;

    /**
     * 字典类型
     */
    @Schema(description = "字典类型")
    private String type;

    /**
     * 上级字典 id
     */
    @Schema(description = "上级字典 id")
    private String pid;

    /**
     * 字典说明
     */
    @Schema(description = "字典说明")
    private String description;

    /**
     * 字典状态
     */
    @Schema(description = "字典状态")
    private String status;

    /**
     * 字典排序
     */
    @Schema(description = "字典排序")
    private Integer sort;

    /**
     * 上级字典
     */
    @Schema(description = "上级字典")
    private DictDTO parent;

    /**
     * 下级字典
     */
    @Schema(description = "子字典")
    private List<DictDTO> children;

}
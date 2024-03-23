package com.shiminfxcvii.turing.model.dto;

import com.shiminfxcvii.turing.model.query.PageQuery;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 字典 DTO
 *
 * @author ShiminFXCVII
 * @since 12/30/2022 4:03 PM
 */
@Getter
@Setter
public class DictDTO extends PageQuery {
    /**
     * 字典 id
     */
    private String id;
    /**
     * 字典类型
     */
    @NotBlank(message = "字典类型不能为空")
    private String type;
    /**
     * 上级字典 id
     */
    private String pid;
    /**
     * 字典名称
     */
    @NotBlank(message = "字典名称不能为空")
    private String name;
    /**
     * 字典值
     */
    @NotBlank(message = "字典值不能为空")
    private String value;
    /**
     * 字典说明
     */
    private String description;
    /**
     * 字典排序
     */
    private Integer sort;
    /**
     * 下级字典
     */
    private List<DictDTO> children;
}

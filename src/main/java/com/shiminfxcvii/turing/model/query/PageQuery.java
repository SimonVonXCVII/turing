package com.shiminfxcvii.turing.model.query;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

// TODO: 2023/5/11 尝试将 PageQuery 替换为 mp 的 Page
@Schema(name = "PageQuery")
@Getter
@Setter
public class PageQuery {

    /**
     * 当前页数
     */
    @Min(value = 1, message = "分页页数不能小于 1")
    @Schema(description = "当前页数", defaultValue = "1")
    private Integer pageIndex = 1;
    /**
     * 当前页数大小
     */
    @Min(value = 1, message = "分页大小不能小于 1")
    @Max(value = 1000, message = "分页大小不能超过 1000")
    @Schema(description = "当前页数大小", defaultValue = "10")
    private Integer pageSize = 10;

}
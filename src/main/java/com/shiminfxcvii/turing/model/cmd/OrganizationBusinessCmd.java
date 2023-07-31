package com.shiminfxcvii.turing.model.cmd;

import com.shiminfxcvii.turing.utils.Insert;
import com.shiminfxcvii.turing.utils.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 单位申请业务实体类
 *
 * @author ShiminFXCVII
 * @since 1/4/2023 4:59 PM
 */
@Schema(name = "单位申请业务实体类")
@Getter
@Setter
public class OrganizationBusinessCmd {

    /**
     * 业务 id
     */
    @Schema(description = "业务 id")
    @NotNull(message = "业务 id 不能为空", groups = Update.class)
    private String id;

    /**
     * 业务申请所在省
     */
    @Schema(description = "业务申请所在省")
    @NotNull(message = "业务申请所在省不能为空", groups = Insert.class)
    private Integer provinceCode;

    /**
     * 业务申请所在市
     */
    @Schema(description = "业务申请所在市")
    private Integer cityCode;

    /**
     * 业务申请所在区县
     */
    @Schema(description = "业务申请所在区县")
    private Integer districtCode;

    /**
     * 业务环节
     */
    @Schema(description = "业务环节")
    private String[] link;

    /**
     * 质控类型
     */
    @Schema(description = "质控类型")
    private String[] type;

    /**
     * 业务申请状态
     */
    @Schema(description = "业务申请状态")
    @NotNull(message = "业务申请状态不能为空", groups = Update.class)
    private String state;

}
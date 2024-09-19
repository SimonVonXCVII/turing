package com.simonvonxcvii.turing.model.dto;

import com.simonvonxcvii.turing.model.query.PageQuery;
import com.simonvonxcvii.turing.utils.Insert;
import com.simonvonxcvii.turing.utils.Update;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 单位申请业务实体类
 *
 * @author SimonVonXCVII
 * @since 1/4/2023 4:59 PM
 */
@Getter
@Setter
public class OrganizationBusinessDTO extends PageQuery {
    /**
     * 业务 id
     */
    @NotNull(message = "业务 id 不能为空", groups = Update.class)
    private String id;
    /**
     * 单位名称
     */
    private String orgName;
    /**
     * 业务申请所在省编码
     */
    @NotNull(message = "业务申请所在省不能为空", groups = Insert.class)
    private Integer provinceCode;
    /**
     * 业务申请所在市编码
     */
    private Integer cityCode;
    /**
     * 业务申请所在区县编码
     */
    private Integer districtCode;
    /**
     * 业务申请所在省名称
     */
    private String provinceName;
    /**
     * 业务申请所在市名称
     */
    private String cityName;
    /**
     * 业务申请所在区县名称
     */
    private String districtName;
    /**
     * 业务环节
     */
    private String[] link;
    /**
     * 质控类型
     */
    private String[] type;
    /**
     * 业务申请状态
     */
    @NotNull(message = "业务申请状态不能为空", groups = Update.class)
    private String state;
}

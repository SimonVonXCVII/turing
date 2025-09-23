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
 * @author Simon Von
 * @since 1/4/2023 4:59 PM
 */
@Getter
@Setter
public class OrganizationBusinessDTO extends PageQuery {
    /**
     * 业务 id
     */
    @NotNull(message = "业务 id 不能为空", groups = Update.class)
    private Integer id;
    /**
     * 单位名称
     */
    private String orgName;
    /**
     * 业务申请所在省（市、区）编码
     */
    @NotNull(message = "业务申请所在省不能为空", groups = Insert.class)
    private Integer provinceCode;
    /**
     * 业务申请所在市（州、盟）编码
     */
    private Integer cityCode;
    /**
     * 业务申请所在区县（市、旗）编码
     */
    private Integer districtCode;
    /**
     * 业务申请所在省（市、区）名称
     */
    private String provinceName;
    /**
     * 业务申请所在市（州、盟）名称
     */
    private String cityName;
    /**
     * 业务申请所在区县（市、旗）名称
     */
    private String districtName;
    /**
     * 业务环节 TODO，尝试改成 Set<String> 或者 Set<OrganizationBusinessBusinessLinksEnum>
     */
    private String[] link;
    /**
     * 质控类型 TODO，尝试改成 Set<String> 或者 Set<OrganizationBusinessQualityControlTypeEnum>
     */
    private String[] type;
    /**
     * 业务申请状态
     */
    @NotNull(message = "业务申请状态不能为空", groups = Update.class)
    private String state;
}

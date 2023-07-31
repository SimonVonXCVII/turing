package com.shiminfxcvii.turing.model.dto;

import com.shiminfxcvii.turing.entity.Organization;
import lombok.Data;

@Data
public class OrgDetailDTO {

    /**
     * 单位基本信息
     */
    private OrganizationDTO organizationDTO;

    /**
     * 单位管理员信息
     */
    private OrgManagerDTO orgManagerDTO;

    /**
     * 上级单位信息
     */
    private Organization upOrganization;
}
package com.shiminfxcvii.turing.model.dto;

import com.shiminfxcvii.turing.entity.Organization;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationDTO extends Organization {
    private String province;
    private String city;
    private String district;
    private Boolean needSetManager;

    private String managerId;

    private String managerName;

    private String managerMobile;
}
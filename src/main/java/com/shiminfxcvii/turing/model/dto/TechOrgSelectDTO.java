package com.shiminfxcvii.turing.model.dto;

import lombok.Data;

/**
 * @DATE: 2023-03-28 10:35
 * @Author: zhq123
 */
@Data
public class TechOrgSelectDTO {
    private String orgId;

    private String orgName;

    private String managerId;

    private String managerName;

    private String managerMobile;
}
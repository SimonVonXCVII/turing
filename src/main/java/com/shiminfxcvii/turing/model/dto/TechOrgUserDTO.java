package com.shiminfxcvii.turing.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * @DATE: 2023-03-11 10:16
 * @Author: zhq123
 */
@Data
public class TechOrgUserDTO {

    private String userId;

    private String username;

    private String realName;

    private String mobile;

    private List<String> roleList;

    private Integer gender;

    private String orgId;

    private String orgName;

    private String idCard;

    private LocalDate birthday;

    private String education;

    private String department;
}
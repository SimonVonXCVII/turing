package com.shiminfxcvii.turing.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @DATE: 2023-03-08 10:49
 * @Author: zhq123
 */
@Data
public class PlatFormUserDTO {
    private String userId;

    private String realName;

    private String username;

    private String mobile;

    private String idCard;

    private String userType;

    private String role;

    private Integer gender;

    private LocalDateTime createTime;
}
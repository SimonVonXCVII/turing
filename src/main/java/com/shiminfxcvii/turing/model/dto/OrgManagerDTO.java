package com.shiminfxcvii.turing.model.dto;

import lombok.Data;

/**
 * 用户基本信息
 */
@Data
public class OrgManagerDTO {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户真实姓名
     */
    private String nickName;

    /**
     * 用户手机号
     */
    private String mobile;


    private String sex;


    private String orgId;
}
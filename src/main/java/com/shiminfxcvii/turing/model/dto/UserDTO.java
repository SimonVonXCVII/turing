package com.shiminfxcvii.turing.model.dto;

import com.shiminfxcvii.turing.enums.GenderEnum;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDTO {

    List<String> roleList;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户登录账号
     */
    private String username;
    /**
     * 用户真实姓名
     */
    private String nickName;
    /**
     * 用户手机号
     */
    private String mobile;
    /**
     * 用户所在单位名称
     */
    private String orgName;
    /**
     * 用户性别
     */
    private GenderEnum gender;
    /**
     * 是否为单位管理员
     */
    private Boolean manager;
    private String orgId;
    private LocalDateTime birthday;
}
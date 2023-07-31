package com.shiminfxcvii.turing.model.dto;

import com.shiminfxcvii.turing.entity.Organization;
import lombok.Data;

@Data
public class UserDetailDTO {

    /**
     * 用户基本信息
     */
    private UserDTO userDTO;

    /**
     * 用户所在单位信息
     */
    private Organization organization;
}
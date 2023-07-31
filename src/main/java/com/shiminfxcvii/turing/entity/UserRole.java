package com.shiminfxcvii.turing.entity;

import com.shiting.soil.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色与用户关联记录表
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-22 16:22:50
 */
@Getter
@Setter
@Accessors(chain = true)
public class UserRole extends BaseEntity {

    /**
     * ES 索引名称
     */
    public static final String INDEX = "soil_user_role";

    /**
     * 用户 id
     */
    public String userId;

    /**
     * 角色 id
     */
    public String roleId;
}
package com.shiminfxcvii.turing.entity;

import com.shiting.soil.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色与权限关联记录表
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-22 16:22:50
 */
@Getter
@Setter
@Accessors(chain = true)
public class RolePermission extends BaseEntity {

    /**
     * 角色 id
     */
    private String roleId;

    /**
     * 权限 id
     */
    private String permissionId;
}
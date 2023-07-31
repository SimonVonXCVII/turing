package com.shiminfxcvii.turing.entity;

import com.shiting.soil.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-22 16:22:50
 */
@Getter
@Setter
@Accessors(chain = true)
public class Role extends BaseEntity {

    /**
     * 角色名称
     */
    private String name;
    /**
     * 角色编码
     */
    public String code;
    /**
     * 角色说明
     */
    private String description;

}
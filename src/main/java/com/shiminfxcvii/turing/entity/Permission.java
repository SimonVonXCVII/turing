package com.shiminfxcvii.turing.entity;

import com.shiting.soil.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 权限表
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-22 16:22:49
 */
@Getter
@Setter
@Accessors(chain = true)
public class Permission extends BaseEntity {

    /**
     * 权限名称
     */
    private String name;
    /**
     * 权限编码
     */
    private String code;
    /**
     * 权限排序
     */
    private Integer sort;
    /**
     * 上级权限 id
     */
    private String pid;

}
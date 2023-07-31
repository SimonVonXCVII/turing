package com.shiminfxcvii.turing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiminfxcvii.turing.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 角色与权限关联记录表 Mapper 接口
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-22 16:22:50
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

}
package com.shiminfxcvii.turing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiminfxcvii.turing.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-22 16:22:49
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

}
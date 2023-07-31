package com.shiminfxcvii.turing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiminfxcvii.turing.entity.RolePermission;
import com.shiminfxcvii.turing.mapper.RolePermissionMapper;
import com.shiminfxcvii.turing.service.IRolePermissionService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色与权限关联记录表 服务实现类
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-22 16:22:50
 */
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements IRolePermissionService {
}
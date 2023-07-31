package com.shiminfxcvii.turing.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiminfxcvii.turing.entity.UserRole;
import com.shiminfxcvii.turing.mapper.UserRoleMapper;
import com.shiminfxcvii.turing.service.IUserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 角色与用户关联记录表 服务实现类
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-22 16:22:50
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements IUserRoleService {

    @Override
    public void addUserRoleRecord(String userId, String roleId) {
        UserRole userRole = new UserRole();
        userRole.setRoleId(roleId);
        userRole.setUserId(userId);
        save(userRole);
    }

    @Override
    public List<String> getUserIdsWithAdminAndCustomerService() {
        List<String> list = new ArrayList<>();
        List<UserRole> userRoles = list(Wrappers.<UserRole>lambdaQuery()
                .eq(UserRole::getRoleId, "1610936924685307577")
                .or()
                .eq(UserRole::getRoleId, "1526969062440824834"));
        if (!userRoles.isEmpty()) {
            return userRoles.stream().map(UserRole::getUserId).toList();
        }
        return list;
    }

    @Override
    public void deleteRecordsByUserId(String userId) {
        // 逻辑删除
        this.baseMapper.deleteRecordsByUserIdLogically(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddUserRoleRecord(String userId, List<String> roleList) {
        List<UserRole> list = new ArrayList<>();
        for (String roleId : roleList) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            list.add(userRole);
        }
        saveBatch(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateUserRoleRecord(String userId, List<String> roleList) {
        // 先删掉之前的
        deleteRecordsByUserId(userId);
        batchAddUserRoleRecord(userId, roleList);
    }
}
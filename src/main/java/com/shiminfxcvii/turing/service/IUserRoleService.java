package com.shiminfxcvii.turing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shiminfxcvii.turing.entity.UserRole;

import java.util.List;

/**
 * <p>
 * 角色与用户关联记录表 服务类
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-22 16:22:50
 */
public interface IUserRoleService extends IService<UserRole> {

    void addUserRoleRecord(String userId, String roleId);

    List<String> getUserIdsWithAdminAndCustomerService();

    void deleteRecordsByUserId(String userId);

    void batchAddUserRoleRecord(String userId, List<String> roleList);

    void batchUpdateUserRoleRecord(String userId, List<String> roleList);
}
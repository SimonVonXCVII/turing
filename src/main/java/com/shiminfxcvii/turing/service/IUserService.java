package com.shiminfxcvii.turing.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shiminfxcvii.turing.entity.User;
import com.shiminfxcvii.turing.model.dto.*;
import com.shiminfxcvii.turing.model.query.UserPageQuery;

import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-19 15:58:28
 */
public interface IUserService extends IService<User> {

    OrgManagerDTO getOrgManager(String orgId);

    IPage<UserDTO> getUserPage(UserPageQuery query);

    UserDetailDTO getUserDetail(String id);

    void updateUser(UserDTO dto);

    void addUser(User user);

    void setOrgManager(OrgManagerDTO dto);

    List<UserDTO> getUserListByOrgId(String orgId);

    void addPlatformUser(PlatFormUserDTO dto);

    void maintainPlatformUser(PlatFormUserDTO dto);

    void resetPassword(String userId);

    void deletePlatformUser(String userId);

    void addTechOrgUser(TechOrgUserDTO dto);


    void updateTechOrgUser(TechOrgUserDTO dto);

    void deleteTechOrgUser(String userId);
}
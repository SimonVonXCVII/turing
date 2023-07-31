package com.shiminfxcvii.turing.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shiminfxcvii.turing.common.exception.BizRuntimeException;
import com.shiminfxcvii.turing.entity.Organization;
import com.shiminfxcvii.turing.entity.Role;
import com.shiminfxcvii.turing.entity.User;
import com.shiminfxcvii.turing.entity.UserRole;
import com.shiminfxcvii.turing.enums.GenderEnum;
import com.shiminfxcvii.turing.mapper.OrganizationMapper;
import com.shiminfxcvii.turing.mapper.UserMapper;
import com.shiminfxcvii.turing.model.dto.*;
import com.shiminfxcvii.turing.model.query.UserPageQuery;
import com.shiminfxcvii.turing.service.IRoleService;
import com.shiminfxcvii.turing.service.IUserRoleService;
import com.shiminfxcvii.turing.service.IUserService;
import com.shiminfxcvii.turing.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author ShiminFXCVII
 * @since 2022-12-19 15:58:28
 */
@RequiredArgsConstructor
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    private final OrganizationMapper organizationMapper;
    private final PasswordEncoder passwordEncoder;
    private final IUserRoleService userRoleService;
    private final IRoleService roleService;

    @Override
    public OrgManagerDTO getOrgManager(String orgId) {
        User user = getOne(Wrappers.<User>lambdaQuery()
                .eq(User::getOrgId, orgId)
                .eq(User::getManager, true));
        if (user != null) {
            OrgManagerDTO managerDTO = new OrgManagerDTO();
            if (user.getGender() != null) {
                managerDTO.setSex(GenderEnum.getValueByOrdinal(user.getGender().ordinal()));
            }
            managerDTO.setNickName(user.getNickName());
            managerDTO.setUserId(user.getId());
            managerDTO.setMobile(user.getMobile());
            return managerDTO;
        } else {
            return null;
        }
    }

    @Override
    public IPage<UserDTO> getUserPage(UserPageQuery query) {
        boolean flag = StringUtils.hasText(query.getUsername()) ||
                StringUtils.hasText(query.getOrgName()) ||
                StringUtils.hasText(query.getNickName()) ||
                StringUtils.hasText(query.getMobile());
        IPage<UserDTO> page = this.baseMapper.getUserPage(
                new Page<>(query.getPageIndex(), query.getPageSize()), query, flag);
        List<UserDTO> records = page.getRecords();
        if (!records.isEmpty()) {
            List<String> userIds = records.stream().map(UserDTO::getUserId).toList();
            List<UserRole> userRoles = userRoleService.list(Wrappers.<UserRole>lambdaQuery().in(UserRole::getUserId, userIds));
            if (userRoles != null && !userRoles.isEmpty()) {
                Map<String, List<UserRole>> map = userRoles.parallelStream().collect(Collectors.groupingBy(UserRole::getUserId));
                List<Role> roleList = roleService.list();
                Map<String, String> roleIdToName = new HashMap<>();
                for (Role role : roleList) {
                    roleIdToName.put(role.getId(), role.getName());
                }
                for (UserDTO record : records) {
                    List<String> roleNames = new ArrayList<>();
                    List<UserRole> userRoleList = map.get(record.getUserId());
                    if (userRoleList != null) {
                        for (UserRole userRole : userRoleList) {
                            String name = roleIdToName.get(userRole.getRoleId());
                            roleNames.add(name);
                        }
                    }
                    record.setRoleList(roleNames);
                }
            }
        }
        return page;
    }

    @Override
    public UserDetailDTO getUserDetail(String id) {
        UserDetailDTO dto = new UserDetailDTO();
        User user = getById(id);
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(id);
        BeanUtils.copyProperties(user, userDTO);
        dto.setUserDTO(userDTO);
        if (user.getOrgId() != null) {
            Organization organization = organizationMapper.selectById(user.getOrgId());
            dto.setOrganization(organization);
        }
        return dto;
    }

    @Override
    public void updateUser(UserDTO dto) {
        User user = getById(dto.getUserId());
        if (StringUtils.hasText(dto.getUsername())) {
            user.setUsername(dto.getUsername());
        }
        if (StringUtils.hasText(dto.getMobile())) {
            user.setMobile(dto.getMobile());
        }
        if (StringUtils.hasText(dto.getNickName())) {
            user.setNickName(dto.getNickName());
        }
        if (dto.getOrgId() != null) {
            user.setOrgId(dto.getOrgId());
        }
        updateById(user);
    }

    @Override
    public void addUser(User user) {
        //新增用户密码为手机号后八位，先校验手机号是否填写了
        if (!StringUtils.hasText(user.getMobile())) {
            throw new BizRuntimeException("请输入手机号");
        }
        user.setPassword(passwordEncoder.encode(user.getMobile().substring(3)));
        //如果输入了身份证号码，则根据身份证号码倒数第二位判断是男是女
        if (StringUtils.hasText(user.getIdCard())) {
            user.setGender((user.getIdCard().charAt(16) % 2) == 0 ? GenderEnum.FEMALE : GenderEnum.MALE);
        }
        //校验单位是否填了
        if (user.getOrgId() == null) {
            throw new BizRuntimeException("请选择单位");
        }
        save(user);
    }

    @Override
    public void setOrgManager(OrgManagerDTO dto) {
        //先判断是否已经设置过了
        OrgManagerDTO orgManager = getOrgManager(dto.getOrgId());
        if (orgManager != null) {
            throw new BizRuntimeException("该单位已设置过管理员");
        }
        update(Wrappers.<User>lambdaUpdate()
                .set(User::getManager, 1)
                .set(User::getUpdateBy, UserUtils.getUserId())
                .set(User::getUpdateTime, LocalDateTime.now())
                .eq(User::getId, dto.getUserId())
                .eq(User::getOrgId, dto.getOrgId()));
    }

    @Override
    public List<UserDTO> getUserListByOrgId(String orgId) {
        List<User> users = list(Wrappers.<User>lambdaQuery().eq(User::getOrgId, orgId).orderByDesc(User::getManager));
        List<UserDTO> list = new ArrayList<>();
        for (User user : users) {
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(user.getId());
            BeanUtils.copyProperties(user, userDTO);
            list.add(userDTO);
        }
        return list;
    }

    @Override
    public void addPlatformUser(PlatFormUserDTO dto) {
        if (!StringUtils.hasText(dto.getMobile())) {
            throw new BizRuntimeException("手机号不能为空");
        }
        User user = new User();
        if (StringUtils.hasText(dto.getRole())) {
            user.setAdmin(dto.getRole().equals("超级管理员"));
        } else {
            user.setAdmin(false);
        }
        user.setNickName(dto.getRealName());
        user.setUsername(StringUtils.hasText(dto.getUsername()) ? dto.getUsername() : dto.getMobile());
        user.setIdCard(dto.getIdCard());
        user.setMobile(dto.getMobile());
        user.setGender(dto.getGender() == 0 ? GenderEnum.FEMALE : GenderEnum.MALE);
        user.setPassword(passwordEncoder.encode(dto.getMobile().substring(3)));
        save(user);
        // 添加一条用户-角色记录
        userRoleService.addUserRoleRecord(user.getId(), user.getAdmin() ? "1526969062440824834" : "1610936924685307577");
    }

    @Override
    public void maintainPlatformUser(PlatFormUserDTO dto) {
        if (dto.getUserId() == null) {
            throw new BizRuntimeException("用户id不能为空");
        }
        User user = getById(dto.getUserId());
        if (StringUtils.hasText(dto.getRealName())) {
            user.setNickName(dto.getRealName());
        }
        if (StringUtils.hasText(dto.getMobile())) {
            user.setMobile(dto.getMobile());
        }
        if (StringUtils.hasText(dto.getIdCard())) {
            user.setIdCard(dto.getIdCard());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender() == 0 ? GenderEnum.FEMALE : GenderEnum.MALE);
        }
        updateById(user);
    }

    @Override
    public void resetPassword(String userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BizRuntimeException("该用户不存在，请联系管理员");
        }
        if (!StringUtils.hasText(user.getMobile())) {
            throw new BizRuntimeException("手机号为空，请联系管理员");
        }
        // 将密码重置为手机号的后八位
        user.setPassword(passwordEncoder.encode(user.getMobile().substring(3)));
        updateById(user);
    }

    @Override
    public void deletePlatformUser(String userId) {
        // 删除用户 目前是逻辑删除
        removeById(userId);
        // 删除有关的用户-角色记录
        userRoleService.deleteRecordsByUserId(userId);

    }

    @Transactional
    @Override
    public void addTechOrgUser(TechOrgUserDTO dto) {
        if (!StringUtils.hasText(dto.getMobile())) {
            throw new BizRuntimeException("手机号不能为空");
        }
        if (getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, dto.getUsername())) != null) {
            throw new BizRuntimeException("用户名已存在");
        }
        if (getOne(Wrappers.<User>lambdaQuery().eq(User::getMobile, dto.getMobile())) != null) {
            throw new BizRuntimeException("手机号已存在");
        }
        User user = new User();
        // username
        user.setUsername(dto.getUsername());
        // 真实姓名
        user.setNickName(dto.getRealName());
        // 电话号码
        user.setMobile(dto.getMobile());
        // 密码
        user.setPassword(passwordEncoder.encode(dto.getMobile().substring(3)));
        // 是否为管理员
        user.setAdmin(false);
        // 单位id和单位名称
        user.setOrgId(dto.getOrgId());
        user.setOrgName(dto.getOrgName());
        // 身份证
        user.setIdCard(dto.getIdCard());
        // 性别
        user.setGender(dto.getGender() == 0 ? GenderEnum.FEMALE : GenderEnum.MALE);
        // 生日
        user.setBirthday(dto.getBirthday());
        // 学历
        user.setEducation(dto.getEducation());
        save(user);
        // TODO 角色？
        List<String> roleList = dto.getRoleList();
        if (roleList == null || roleList.isEmpty()) {
            throw new BizRuntimeException("请选择角色");
        }
        userRoleService.batchAddUserRoleRecord(user.getId(), roleList);

    }

    @Transactional
    @Override
    public void updateTechOrgUser(TechOrgUserDTO dto) {
        if (dto.getUserId() == null) {
            throw new BizRuntimeException("用户id为null");
        }
        User user = getById(dto.getUserId());
        if (user == null) {
            throw new BizRuntimeException("数据异常，请联系管理员");
        }
        // 真实姓名
        user.setNickName(dto.getRealName());
        // 电话号码
        user.setMobile(dto.getMobile());
        // 性别
        user.setGender(dto.getGender() == 0 ? GenderEnum.FEMALE : GenderEnum.MALE);
        // 身份证
        user.setIdCard(dto.getIdCard());
        updateById(user);
        // TODO 角色？
        List<String> roleList = dto.getRoleList();
        if (roleList == null || roleList.isEmpty()) {
            throw new BizRuntimeException("请选择角色");
        }
        userRoleService.batchUpdateUserRoleRecord(user.getId(), roleList);
    }

    @Transactional
    @Override
    public void deleteTechOrgUser(String userId) {
        this.baseMapper.deleteTechOrgUser(userId);
        // 删除角色信息
        userRoleService.deleteRecordsByUserId(userId);
    }


}
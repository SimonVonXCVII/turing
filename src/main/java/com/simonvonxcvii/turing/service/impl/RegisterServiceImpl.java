package com.simonvonxcvii.turing.service.impl;

import com.simonvonxcvii.turing.common.exception.BizRuntimeException;
import com.simonvonxcvii.turing.entity.Organization;
import com.simonvonxcvii.turing.entity.User;
import com.simonvonxcvii.turing.entity.UserRole;
import com.simonvonxcvii.turing.enums.OrganizationTypeEnum;
import com.simonvonxcvii.turing.model.dto.RegisterDTO;
import com.simonvonxcvii.turing.repository.OrganizationRepository;
import com.simonvonxcvii.turing.repository.UserRepository;
import com.simonvonxcvii.turing.repository.UserRoleRepository;
import com.simonvonxcvii.turing.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 注册 服务实现类
 *
 * @author SimonVonXCVII
 * @since 2023/4/12 22:20
 */
@RequiredArgsConstructor
@Service
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * // TODO: 2023/8/31 测试
     * 注册
     *
     * @author SimonVonXCVII
     * @since 2023/4/12 22:20
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO dto) {
        boolean exists = organizationRepository.exists((root, _, _) ->
                root.get(Organization.NAME).in(dto.getName()));
        if (exists) {
            throw new BizRuntimeException("该单位名称已经注册，请重新输入");
        }

        exists = organizationRepository.exists((root, _, _) ->
                root.get(Organization.CODE).in(dto.getCode()));
        if (exists) {
            throw new BizRuntimeException("该信用代码已经注册，请重新输入");
        }

        exists = organizationRepository.exists((root, _, _) ->
                root.get(Organization.PHONE).in(dto.getPhone()));
        if (exists) {
            throw new BizRuntimeException("该联系电话已被使用，请重新输入");
        }

        exists = userRepository.exists((root, _, _) ->
                root.get(User.MOBILE).in(dto.getMobile()));
        if (exists) {
            throw new BizRuntimeException("该手机号码已被使用，请重新输入");
        }

        exists = userRepository.exists((root, _, _) ->
                root.get(User.USERNAME).in(dto.getUsername()));
        if (exists) {
            throw new BizRuntimeException("该登录账号已被使用，请重新输入");
        }

        // 单位信息
        Organization organization = new Organization();
        BeanUtils.copyProperties(dto, organization);
        // 省市县
        organization.setProvinceCode(dto.getProvinceCode());
        organization.setCityCode(dto.getCityCode());
        organization.setDistrictCode(dto.getDistrictCode());
        // 单位类型
        organization.setType(OrganizationTypeEnum.BUSINESS_TECHNOLOGY.getDesc());
        organizationRepository.save(organization);

        // 管理员信息
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setNeedSetPassword(Boolean.TRUE);
        // 密码
        user.setPassword(passwordEncoder.encode(user.getMobile().substring(3)));
        user.setOrgId(organization.getId());
        user.setOrgName(organization.getName());
        user.setManager(Boolean.TRUE);
        user.setAccountNonLocked(Boolean.TRUE);
        user.setEnabled(Boolean.TRUE);
        user.setAdmin(Boolean.FALSE);
        userRepository.save(user);

        // 赋予单位管理员角色
        UserRole userRole = new UserRole()
                .setUserId(user.getId())
                // TODO: 2023/9/8
                .setRoleId("40");
        userRoleRepository.save(userRole);
    }

}

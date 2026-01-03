package com.simonvonxcvii.turing.resource.server.service.impl;

import com.simonvonxcvii.turing.resource.server.entity.Organization;
import com.simonvonxcvii.turing.resource.server.entity.Role;
import com.simonvonxcvii.turing.resource.server.entity.User;
import com.simonvonxcvii.turing.resource.server.entity.UserRole;
import com.simonvonxcvii.turing.resource.server.enums.OrganizationTypeEnum;
import com.simonvonxcvii.turing.resource.server.model.dto.RegisterDTO;
import com.simonvonxcvii.turing.resource.server.repository.jpa.OrganizationJpaRepository;
import com.simonvonxcvii.turing.resource.server.repository.jpa.UserJpaRepository;
import com.simonvonxcvii.turing.resource.server.repository.jpa.UserRoleJpaRepository;
import com.simonvonxcvii.turing.resource.server.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 注册 服务实现类
 *
 * @author Simon Von
 * @since 2023/4/12 22:20
 */
@RequiredArgsConstructor
@Service
public class RegisterServiceImpl implements RegisterService {

    private final UserJpaRepository userJpaRepository;
    private final UserRoleJpaRepository userRoleJpaRepository;
    private final OrganizationJpaRepository organizationJpaRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * // TODO: 2023/8/31 测试
     * 注册
     *
     * @author Simon Von
     * @since 2023/4/12 22:20
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO dto) {
        boolean exists = organizationJpaRepository.existsByName(dto.getName());
        if (exists) {
            throw new RuntimeException("该单位名称已经注册，请重新输入");
        }

        exists = organizationJpaRepository.existsByCode(dto.getCode());
        if (exists) {
            throw new RuntimeException("该信用代码已经注册，请重新输入");
        }

        exists = organizationJpaRepository.existsByPhone(dto.getPhone());
        if (exists) {
            throw new RuntimeException("该联系电话已被使用，请重新输入");
        }

        exists = userJpaRepository.existsByMobile(dto.getMobile());
        if (exists) {
            throw new RuntimeException("该手机号码已被使用，请重新输入");
        }

        exists = userJpaRepository.existsByUsername(dto.getUsername());
        if (exists) {
            throw new RuntimeException("该登录账号已被使用，请重新输入");
        }

        // 单位信息
        Organization organization = new Organization();
        BeanUtils.copyProperties(dto, organization);
        // 省市县
        organization.setProvinceCode(dto.getProvinceCode());
        organization.setCityCode(dto.getCityCode());
        organization.setDistrictCode(dto.getDistrictCode());
        // 单位类型
        organization.setType(OrganizationTypeEnum.BUSINESS_TECHNOLOGY);
        organizationJpaRepository.save(organization);

        // 管理员信息
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setNeedResetPassword(Boolean.TRUE);
        // 密码
        String password = passwordEncoder.encode(String.valueOf(user.getMobile()).substring(3));
        assert password != null;
        user.setPassword(password);
        user.setOrgId(organization.getId());
        user.setOrgName(organization.getName());
        user.setManager(Boolean.TRUE);
        user.setAccountNonLocked(Boolean.TRUE);
        user.setEnabled(Boolean.TRUE);
        userJpaRepository.save(user);

        // 赋予单位管理员角色
        UserRole userRole = new UserRole();
        userRole.setUser(user);
        // TODO: 2023/9/8
        userRole.setRole(new Role());
        userRoleJpaRepository.save(userRole);
    }

}

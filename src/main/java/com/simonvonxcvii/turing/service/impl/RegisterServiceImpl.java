package com.simonvonxcvii.turing.service.impl;

import com.simonvonxcvii.turing.common.exception.BizRuntimeException;
import com.simonvonxcvii.turing.entity.Organization;
import com.simonvonxcvii.turing.entity.User;
import com.simonvonxcvii.turing.entity.UserRole;
import com.simonvonxcvii.turing.enums.OrganizationTypeEnum;
import com.simonvonxcvii.turing.model.dto.RegisterDTO;
import com.simonvonxcvii.turing.repository.jpa.OrganizationJpaRepository;
import com.simonvonxcvii.turing.repository.jpa.UserJpaRepository;
import com.simonvonxcvii.turing.repository.jpa.UserRoleJpaRepository;
import com.simonvonxcvii.turing.service.RegisterService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;
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
        Specification<@NonNull Organization> spec = (root, query, builder) -> {
            Predicate predicate = builder.equal(root.get(Organization.NAME), dto.getName());
            return query.where(predicate).getRestriction();
        };
        boolean exists = organizationJpaRepository.exists(spec);
        if (exists) {
            throw new BizRuntimeException("该单位名称已经注册，请重新输入");
        }

        Specification<@NonNull Organization> spec2 = (root, query, builder) -> {
            Predicate predicate = builder.equal(root.get(Organization.CODE), dto.getCode());
            return query.where(predicate).getRestriction();
        };
        exists = organizationJpaRepository.exists(spec2);
        if (exists) {
            throw new BizRuntimeException("该信用代码已经注册，请重新输入");
        }

        Specification<@NonNull Organization> spec3 = (root, query, builder) -> {
            Predicate predicate = builder.equal(root.get(Organization.PHONE), dto.getPhone());
            return query.where(predicate).getRestriction();
        };
        exists = organizationJpaRepository.exists(spec3);
        if (exists) {
            throw new BizRuntimeException("该联系电话已被使用，请重新输入");
        }

        Specification<@NonNull User> userSpec = (root, query, builder) -> {
            Predicate predicate = builder.equal(root.get(User.MOBILE), dto.getMobile());
            return query.where(predicate).getRestriction();
        };
        exists = userJpaRepository.exists(userSpec);
        if (exists) {
            throw new BizRuntimeException("该手机号码已被使用，请重新输入");
        }

        Specification<@NonNull User> userSpec2 = (root, query, builder) -> {
            Predicate predicate = builder.equal(root.get(User.USERNAME), dto.getUsername());
            return query.where(predicate).getRestriction();
        };
        exists = userJpaRepository.exists(userSpec2);
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
        user.setAdmin(Boolean.FALSE);
        userJpaRepository.save(user);

        // 赋予单位管理员角色
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        // TODO: 2023/9/8
        userRole.setRoleId(40);
        userRoleJpaRepository.save(userRole);
    }

}

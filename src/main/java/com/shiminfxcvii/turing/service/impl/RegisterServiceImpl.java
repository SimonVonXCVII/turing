package com.shiminfxcvii.turing.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.shiminfxcvii.turing.common.exception.BizRuntimeException;
import com.shiminfxcvii.turing.entity.Organization;
import com.shiminfxcvii.turing.entity.User;
import com.shiminfxcvii.turing.entity.UserRole;
import com.shiminfxcvii.turing.enums.GenderEnum;
import com.shiminfxcvii.turing.mapper.OrganizationMapper;
import com.shiminfxcvii.turing.mapper.UserMapper;
import com.shiminfxcvii.turing.mapper.UserRoleMapper;
import com.shiminfxcvii.turing.model.cmd.RegisterCmd;
import com.shiminfxcvii.turing.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

/**
 * 注册 服务实现类
 *
 * @author ShiminFXCVII
 * @since 2023/4/12 22:20
 */
@RequiredArgsConstructor
@Service
public class RegisterServiceImpl implements RegisterService {

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final OrganizationMapper organizationMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 注册
     *
     * @author ShiminFXCVII
     * @since 2023/4/12 22:20
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterCmd cmd) {
//        UnifiedSocialCreditCode unifiedSocialCreditCode = unifiedSocialCreditCodeMapper.selectOne(Wrappers
//                .<UnifiedSocialCreditCode>lambdaQuery()
//                .eq(UnifiedSocialCreditCode::getCode, cmd.getCode()));
//        if (unifiedSocialCreditCode == null) {
//            throw new BizRuntimeException("该信用代码未录入，请联系管理员");
//        }
//        if (unifiedSocialCreditCode.getRegistered()) {
//            throw new BizRuntimeException("该信用代码已被使用，请联系管理员");
//        }

        Organization organization = organizationMapper.selectOne(Wrappers.<Organization>lambdaQuery().eq(Organization::getName, cmd.getName()));
        if (organization != null) {
            throw new BizRuntimeException("该单位名称已经注册");
        }

        organization = organizationMapper.selectOne(Wrappers.<Organization>lambdaQuery().eq(Organization::getCode, cmd.getCode()));
        if (organization != null) {
            throw new BizRuntimeException("该信用代码已经注册");
        }

        organization = organizationMapper.selectOne(Wrappers.<Organization>lambdaQuery().eq(Organization::getPhone, cmd.getPhone()));
        if (organization != null) {
            throw new BizRuntimeException("该联系电话已被使用，请重新输入");
        }

        User user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getMobile, cmd.getMobile()));
        if (user != null) {
            throw new BizRuntimeException("该手机号码已被使用，请重新输入");
        }

        user = userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, cmd.getUsername()));
        if (user != null) {
            throw new BizRuntimeException("该登录账号已被使用，请重新输入");
        }

        // 单位信息
        organization = new Organization();
        BeanUtils.copyProperties(cmd, organization);
        // 省市县
        organization.setProvinceCode(cmd.getProvinceCode().toString());
        organization.setCityCode(cmd.getCityCode().toString());
        organization.setDistrictCode(cmd.getDistrictCode().toString());
//        organization.setType(unifiedSocialCreditCode.getType());
        organizationMapper.insert(organization);

        // 管理员信息
        user = new User();
        BeanUtils.copyProperties(cmd, user);
        user.setNeedSetPassword(Boolean.TRUE);
        // 密码
        user.setPassword(passwordEncoder.encode(user.getMobile().substring(3)));
        if (StringUtils.hasText(cmd.getIdCard())) {
            // 生日
            int yearBirth = Integer.parseInt(cmd.getIdCard().substring(6, 10)),
                    monthBirth = Integer.parseInt(cmd.getIdCard().substring(10, 12)),
                    dayBirth = Integer.parseInt(cmd.getIdCard().substring(12, 14));
            LocalDate birthday = LocalDate.of(yearBirth, monthBirth, dayBirth);
            user.setBirthday(birthday);
            // 性别
            int gender = Integer.parseInt(cmd.getIdCard().substring(16, 17)) % 2;
            user.setGender(GenderEnum.getGenderByOrdinal(gender));
        }
        user.setOrgId(organization.getId());
        user.setOrgName(organization.getName());
        user.setManager(Boolean.TRUE);
        user.setLocked(Boolean.FALSE);
        user.setDisabled(Boolean.FALSE);
        user.setAdmin(Boolean.FALSE);
        userMapper.insert(user);

        // 更新信用代码数据
//        unifiedSocialCreditCode.setRegistered(Boolean.TRUE);
//        unifiedSocialCreditCodeMapper.updateById(unifiedSocialCreditCode);

        // 赋予单位管理员角色
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId("1633735924623921154");
        userRoleMapper.insert(userRole);
    }

}
package com.simonvonxcvii.turing.resource.server.service.impl;

import com.simonvonxcvii.turing.resource.server.entity.*;
import com.simonvonxcvii.turing.resource.server.model.dto.RoleDTO;
import com.simonvonxcvii.turing.resource.server.model.dto.UserDTO;
import com.simonvonxcvii.turing.resource.server.repository.jpa.OrganizationJpaRepository;
import com.simonvonxcvii.turing.resource.server.repository.jpa.RoleJpaRepository;
import com.simonvonxcvii.turing.resource.server.repository.jpa.UserJpaRepository;
import com.simonvonxcvii.turing.resource.server.repository.jpa.UserRoleJpaRepository;
import com.simonvonxcvii.turing.resource.server.service.IUserService;
import com.simonvonxcvii.turing.resource.server.utils.UserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Simon Von
 * @since 2022-12-19 15:58:28
 */
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements IUserService {

    private final PasswordEncoder passwordEncoder;
    private final UserJpaRepository userJpaRepository;
    private final OrganizationJpaRepository organizationJpaRepository;
    private final UserRoleJpaRepository userRoleJpaRepository;
    private final RoleJpaRepository roleJpaRepository;

    /**
     * 获取用户信息
     *
     * @author Simon Von
     * @since 12/17/2022 8:19 PM
     */
    @Override
    public UserDTO info() {
        User user = UserUtils.getUser();
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertOrUpdate(UserDTO dto) {
        User user;
        // 新增
        if (dto.getId() == null) {
            user = new User();
        }
        // 修改
        else {
            user = userJpaRepository.findById(dto.getId()).orElseThrow(() -> new RuntimeException("无法查找到用户数据"));
        }
        dto.setAccountNonExpired(Boolean.TRUE);
        dto.setAccountNonLocked(Boolean.TRUE);
        dto.setCredentialsNonExpired(Boolean.TRUE);
        dto.setEnabled(Boolean.TRUE);
        dto.setManager(Boolean.FALSE);
        dto.setNeedResetPassword(Boolean.TRUE);
        BeanUtils.copyProperties(dto, user, AbstractAuditable.CREATED_DATE);
        // 密码
        String password = passwordEncoder.encode(dto.getMobile().toString().substring(3));
        assert password != null;
        user.setPassword(password);
        // 单位名称
        Organization organization = organizationJpaRepository.findById(dto.getOrgId())
                .orElseThrow(() -> new RuntimeException("无法查找到单位数据"));
        user.setOrgName(organization.getName());
        userJpaRepository.save(user);
        // 更新用户角色表
        // TODO 可以优化成只添加需要添加的，只删除需要删除的
        userRoleJpaRepository.deleteByUserId(dto.getId());
        List<UserRole> userRoleList = new LinkedList<>();
        roleJpaRepository.findAllById(dto.getRoleIdList())
                .forEach(role -> {
                    UserRole userRole = new UserRole();
                    userRole.setUser(user);
                    userRole.setRole(role);
                    userRoleList.add(userRole);
                });
        userRoleJpaRepository.saveAll(userRoleList);
    }

    @Override
    public Page<UserDTO> selectPage(UserDTO dto) {
        Page<User> userPage;
        try {
            Specification<User> spec = Specification.<User>where((from, criteriaBuilder) -> {
                if (!StringUtils.hasText(dto.getRealName())) {
                    return null;
                }
                return criteriaBuilder.like(from.get(User.NAME), "%" + dto.getRealName() + "%");
            }).and((from, criteriaBuilder) -> {
                if (dto.getMobile() == null) {
                    return null;
                }
                return criteriaBuilder.like(from.get(User.MOBILE), "%" + dto.getMobile() + "%");
            }).and((from, criteriaBuilder) -> {
                if (!StringUtils.hasText(dto.getGender())) {
                    return null;
                }
                return criteriaBuilder.equal(from.get(User.GENDER), dto.getGender());
            }).and((from, criteriaBuilder) -> {
                if (!StringUtils.hasText(dto.getOrgName())) {
                    return null;
                }
                return criteriaBuilder.like(from.get(User.ORG_NAME), "%" + dto.getOrgName() + "%");
            }).and((from, criteriaBuilder) -> {
                if (!StringUtils.hasText(dto.getDepartment())) {
                    return null;
                }
                return criteriaBuilder.like(from.get(User.DEPARTMENT), "%" + dto.getDepartment() + "%");
            }).and((from, criteriaBuilder) -> {
                if (!StringUtils.hasText(dto.getUsername())) {
                    return null;
                }
                return criteriaBuilder.like(from.get(User.USERNAME), "%" + dto.getUsername() + "%");
            }).and((from, criteriaBuilder) -> {
                if (CollectionUtils.isEmpty(dto.getRoleIdList())) {
                    return null;
                }
                List<UserRole> userRoleList = userRoleJpaRepository.findAllByRoleIdIn(dto.getRoleIdList());
                if (userRoleList.isEmpty()) {
                    // todo
                    throw new RuntimeException();
                }
                List<Integer> userIdlist = userRoleList.stream().map(UserRole::getUser).map(User::getId).toList();
                return criteriaBuilder.in(from.get(User.ID).in(userIdlist));
            }).and((from, criteriaBuilder) -> {
                if (dto.getAccountNonExpired() == null) {
                    return null;
                }
                return criteriaBuilder.equal(from.get(User.ACCOUNT_NON_EXPIRED), dto.getAccountNonExpired());
            }).and((from, criteriaBuilder) -> {
                if (dto.getAccountNonLocked() == null) {
                    return null;
                }
                return criteriaBuilder.equal(from.get(User.ACCOUNT_NON_LOCKED), dto.getAccountNonLocked());
            }).and((from, criteriaBuilder) -> {
                if (dto.getCredentialsNonExpired() == null) {
                    return null;
                }
                return criteriaBuilder.equal(from.get(User.CREDENTIALS_NON_EXPIRED), dto.getCredentialsNonExpired());
            }).and((from, criteriaBuilder) -> {
                if (dto.getEnabled() == null) {
                    return null;
                }
                return criteriaBuilder.equal(from.get(User.ENABLED), dto.getEnabled());
            }).and((from, criteriaBuilder) -> {
                if (dto.getManager() == null) {
                    return null;
                }
                return criteriaBuilder.equal(from.get(User.MANAGER), dto.getManager());
            }).and((from, criteriaBuilder) -> {
                if (dto.getNeedResetPassword() == null) {
                    return null;
                }
                return criteriaBuilder.equal(from.get(User.NEED_RESET_PASSWORD), dto.getNeedResetPassword());
            });
            // TODO: 2023/8/29 设置前端 number 默认从 0 开始，或许就不需要减一了
            PageRequest pageRequest = PageRequest.of(dto.getPage() - 1, dto.getPageSize());
            userPage = userJpaRepository.findAll(spec, pageRequest);
        } catch (Exception e) {
            return Page.empty();
        }
        return userPage.map(user -> {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            List<UserRole> userRoleList = userRoleJpaRepository.findAllByUserId(user.getId());
            if (userRoleList.isEmpty()) {
                throw new RuntimeException("数据异常，该用户没有角色：" + user.getUsername());
            }
            List<Integer> iDList = userRoleList.stream().map(AbstractAuditable::getId).toList();
            List<Role> roleList = roleJpaRepository.findAllById(iDList);
            if (roleList.isEmpty()) {
                throw new RuntimeException("数据异常，该用户没有角色：" + user.getUsername());
            }
            List<RoleDTO> roleDTOList = roleList.stream()
                    .map(role -> {
                        RoleDTO roleDTO = new RoleDTO();
                        BeanUtils.copyProperties(role, roleDTO);
                        return roleDTO;
                    })
                    .toList();
            userDTO.setAuthorities(roleDTOList);
            return userDTO;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Integer id) {
        // 逻辑删除用户-角色关联数据
        userRoleJpaRepository.deleteByUserId(id);
        // 逻辑删除用户数据
        userJpaRepository.deleteById(id);
    }

}
